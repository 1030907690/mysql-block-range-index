package com.zzq.mysqlblockrangeindex.job;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.zzq.mysqlblockrangeindex.bean.BasicEntity;
import com.zzq.mysqlblockrangeindex.bean.CreateTableDDL;
import com.zzq.mysqlblockrangeindex.bean.Table;
import com.zzq.mysqlblockrangeindex.constant.Constant;
import com.zzq.mysqlblockrangeindex.index.TableNameColumnMapping;
import com.zzq.mysqlblockrangeindex.parser.CreateTableParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 执行任务
 * @author Zhou Zhongqing
 * @since 2/4/2026 9:39 PM
 */
public class DateBlockRangeIndexJob {

    private final Logger log = LoggerFactory.getLogger(DateBlockRangeIndexJob.class);

    private JdbcTemplate jdbcTemplate;

    private StringRedisTemplate stringRedisTemplate;


    private final CreateTableParser createTableParser = new CreateTableParser();


    public DateBlockRangeIndexJob(JdbcTemplate jdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void execute(Table table) {
        TableNameColumnMapping.add(table.getName(), table);
        String tableName = table.getName();
        Integer autoIncrement = getTableAutoIncrement(tableName);
        log.info("tableName: {}, autoIncrement: {}", tableName, autoIncrement);

        Integer minId = getLastSegmentId(tableName);
        log.info("tableName {} minId {} ", tableName, minId);
        List<BasicEntity> tableDatas = getTableData(table, minId, autoIncrement);
        log.info("tableDatas: {}", tableDatas);
        saveRedis(table, tableDataMonthGroup(tableDatas));
        prune(table);
    }
    /**
     * 删除过期数据,同一个月可能有重复数据
     * @param table
     */
    private void prune(Table table) {

    }
    public void saveRedis(Table table, Map<String, List<BasicEntity>> group) {
        group.forEach((yearMonth, basicEntitys) -> {
            BasicEntity maxBasicEntity = basicEntitys.stream().max(Comparator.comparingInt(BasicEntity::getId)).get();
            log.info("yearMonth {} maxBasicEntity:  {}", yearMonth, maxBasicEntity);
            stringRedisTemplate.opsForHash().put(Constant.MYSQL_BLOCK_RANGE_INDEX + table.getName(), DateUtil.format(maxBasicEntity.getCreateTime(), DatePattern.PURE_DATETIME_PATTERN), maxBasicEntity.getId().toString());
        });
    }

    private Map<String, List<BasicEntity>> tableDataMonthGroup(List<BasicEntity> tableDatas) {
        if (!CollectionUtils.isEmpty(tableDatas)) {
            // 数据按年月分组
            return tableDatas.stream().collect(Collectors.groupingBy(basicEntity -> DateUtil.format(basicEntity.getCreateTime(), "yyyy-MM")));
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
        return createTableParser.parseAutoIncrement(tables.stream().findFirst().get().getCreateTable());
    }


}
