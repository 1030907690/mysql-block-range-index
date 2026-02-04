package com.zzq.mysqlblockrangeindex.job;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.zzq.mysqlblockrangeindex.bean.BasicEntity;
import com.zzq.mysqlblockrangeindex.bean.Table;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/4/2026 9:39 PM
 */
public class BlockRangeIndexJob {

    private final String[] TABLE_NAMES = {"t_user"};
    private final Logger log = LoggerFactory.getLogger(BlockRangeIndexJob.class);

    private JdbcTemplate jdbcTemplate;

    private StringRedisTemplate stringRedisTemplate;

    private String PRIMARY_KEY_COLUMN = "id";

    private String CREATE_TIME_COLUMN = "create_time";

    private String DATABASE_NAME = "test";


    public BlockRangeIndexJob(JdbcTemplate jdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void execute() {
        for (String tableName : TABLE_NAMES) {
            Integer autoIncrement = getTableAutoIncrement(tableName);
            log.info("tableName: {}, autoIncrement: {}", tableName, autoIncrement);
            List<BasicEntity> tableData = getTableData(tableName, 0, autoIncrement);
            log.info("tableData: {}", tableData);
            BasicEntity maxBasicEntity = tableData.stream().max(Comparator.comparingInt(BasicEntity::getId)).get();
            stringRedisTemplate.opsForHash().put(tableName, DateUtil.format(maxBasicEntity.getCreateTime(), DatePattern.PURE_DATETIME_PATTERN),maxBasicEntity.getId().toString());
            Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(tableName);
            entries.forEach((key, value) -> {
                log.info("key: {}, value: {}", key, value);
            });

        }
    }

    private List<BasicEntity> getTableData(String tableName, Integer minId, Integer maxId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(PRIMARY_KEY_COLUMN + "," + CREATE_TIME_COLUMN);
        sql.append(" FROM " + DATABASE_NAME + "." + tableName);
        sql.append(" WHERE " + PRIMARY_KEY_COLUMN + " >= " + minId);
        sql.append(" and " + PRIMARY_KEY_COLUMN + " <= " + maxId);
        log.info("sql: {}", sql);
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new BasicEntity(rs.getInt(PRIMARY_KEY_COLUMN), rs.getDate(CREATE_TIME_COLUMN)));
    }

    private Integer getTableAutoIncrement(String tableName) {
        List<Table> tables = jdbcTemplate.query("SELECT AUTO_INCREMENT from information_schema.`TABLES` WHERE TABLE_NAME = '" + tableName + "' and TABLE_SCHEMA = 'test'", (rs, rowNum) -> {
            Table table = new Table();
            table.setAutoIncrement(rs.getInt("AUTO_INCREMENT"));
            return table;
        });
        Assert.notNull(tables, "tableName " + tableName + " not exists");
        Assert.isTrue(tables.size() == 1, "tableName " + tableName + " many ");
        return tables.stream().findFirst().get().getAutoIncrement();
    }


}
