package com.zzq.mysqlblockrangeindex.job;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zzq.mysqlblockrangeindex.bean.BasicEntity;
import com.zzq.mysqlblockrangeindex.constant.Constant;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/4/2026 9:39 PM
 */
public class BlockRangeIndexJob {

    private final List<Table> TABLES = Arrays.asList(new Table("t_user","id","create_time"));
    private final Logger log = LoggerFactory.getLogger(BlockRangeIndexJob.class);

    private JdbcTemplate jdbcTemplate;

    private StringRedisTemplate stringRedisTemplate;




    public BlockRangeIndexJob(JdbcTemplate jdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void execute() {
        for (Table table : TABLES) {
            String tableName = table.getName();
            Integer autoIncrement = getTableAutoIncrement(tableName);
            log.info("tableName: {}, autoIncrement: {}", tableName, autoIncrement);

            Integer minId = getLastSegmentId(tableName);
            log.info("tableName {} minId {} ", tableName, minId);
            List<BasicEntity> tableData = getTableData(table, minId, autoIncrement);
            log.info("tableData: {}", tableData);
            saveRedis(table,tableDataMonthGroup(tableData));
        }
    }

    public void saveRedis(Table table, Map<String, List<BasicEntity>> group) {
        group.forEach((yearMonth, basicEntitys) -> {
            BasicEntity maxBasicEntity = basicEntitys.stream().max(Comparator.comparingInt(BasicEntity::getId)).get();
            log.info("yearMonth {} maxBasicEntity:  {}", yearMonth, maxBasicEntity);
            stringRedisTemplate.opsForHash().put(Constant.MYSQL_BLOCK_RANGE_INDEX + table.getName(), DateUtil.format(maxBasicEntity.getCreateTime(), DatePattern.PURE_DATETIME_PATTERN), maxBasicEntity.getId().toString());
        });
    }
    private  Map<String, List<BasicEntity>> tableDataMonthGroup(List<BasicEntity> tableData) {
        if (!CollectionUtils.isEmpty(tableData)) {
            // 数据按年月分组
            return tableData.stream().collect(Collectors.groupingBy(basicEntity -> DateUtil.format(basicEntity.getCreateTime(), "yyyy-MM")));
        }
        return Collections.emptyMap();
    }

    private Integer getLastSegmentId(String tableName) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(Constant.MYSQL_BLOCK_RANGE_INDEX + tableName);
        List<Integer> result = entries.values().stream().map(value -> Integer.parseInt(value.toString())).collect(Collectors.toList());
        // 没有就从 0 开始
        return CollectionUtils.isEmpty(result) ? 0 : Collections.max(result);
    }

    private List<BasicEntity> getTableData(Table table, Integer minId, Integer maxId) {
        String tableName = table.getName();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(table.getPrimaryKeyAutoIncrementColumn() + StrUtil.COMMA + table.getCreateTimeColumn());
        sql.append(" FROM " + tableName);
        sql.append(" WHERE " + table.getPrimaryKeyAutoIncrementColumn() + " >= " + minId);
        sql.append(" and " + table.getPrimaryKeyAutoIncrementColumn() + " <= " + maxId);
        sql.append(" ORDER BY " + table.getPrimaryKeyAutoIncrementColumn());
        log.info("sql: {}", sql);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new BasicEntity(rs.getInt(table.getPrimaryKeyAutoIncrementColumn()), rs.getTimestamp(table.getCreateTimeColumn()).toLocalDateTime()));
    }

    private Integer getTableAutoIncrement(String tableName) {
        List<CreateTableDDL> tables = jdbcTemplate.query("show create table " + tableName, (rs, rowNum) -> {
            CreateTableDDL table = new CreateTableDDL();
            table.setCreateTable(rs.getString("Create Table"));
            return table;
        });
        log.info("tables: {}", tables);
        return parseAutoIncrement(tables.stream().findFirst().get().getCreateTable());
    }

    public Integer parseAutoIncrement(String ddlSql) {
        CreateTable createTable = null;
        try {
            createTable = (CreateTable) CCJSqlParserUtil.parse(ddlSql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        List<?> options = createTable.getTableOptionsStrings();

        String autoIncrementValue = null;
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i).toString();
                if ("AUTO_INCREMENT".equalsIgnoreCase(option) && i + 2 < options.size()) {
                    // JSqlParser 通常把 "AUTO_INCREMENT", "=", "8" 拆成三个连续元素
                    // 所以我们要跳过等号取后面的值
                    autoIncrementValue = options.get(i + 2).toString();
                    break;
                }
            }
        }
        return Integer.parseInt(autoIncrementValue);
    }


    public static class Table {

        /**
         * 表名
         */
        private String name;
        /**
         * 主键自增列
         */
        private String primaryKeyAutoIncrementColumn;

        /**
         * 创建时间列
         */
        private String createTimeColumn;

        public Table() {
        }

        public Table(String name,String primaryKeyAutoIncrementColumn, String createTimeColumn) {
            this.name = name;
            this.primaryKeyAutoIncrementColumn = primaryKeyAutoIncrementColumn;
            this.createTimeColumn = createTimeColumn;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrimaryKeyAutoIncrementColumn() {
            return primaryKeyAutoIncrementColumn;
        }

        public void setPrimaryKeyAutoIncrementColumn(String primaryKeyAutoIncrementColumn) {
            this.primaryKeyAutoIncrementColumn = primaryKeyAutoIncrementColumn;
        }

        public String getCreateTimeColumn() {
            return createTimeColumn;
        }

        public void setCreateTimeColumn(String createTimeColumn) {
            this.createTimeColumn = createTimeColumn;
        }
    }

    public static class CreateTableDDL {

        private String createTable;

        public String getCreateTable() {
            return createTable;
        }

        public void setCreateTable(String createTable) {
            this.createTable = createTable;
        }
    }

}
