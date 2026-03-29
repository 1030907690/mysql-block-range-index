package com.zzq.mysqlblockrangeindex.index;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zzq.mysqlblockrangeindex.bean.Range;
import com.zzq.mysqlblockrangeindex.parser.SelectParser;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/**
 * 索引帮助类
 * @author zzq
 * @since 2026/02/07 14:29:28
 */

public class IndexHelper {
    private final static SelectParser selectParser = new SelectParser();

    /**
     * 开启时间范围索引
     * @param tableName
     * @param tableAlias
     * @param startTime
     * @param endTime
     */
    public static void startDateRangeIndex(String tableName, String tableAlias, LocalDateTime startTime, LocalDateTime endTime) {
        if (ObjectUtils.isEmpty(startTime) && ObjectUtils.isEmpty(endTime)) {
            return;
        }
        BlockRangeIndex index = new BlockRangeIndex(tableName, tableAlias, startTime, endTime);
        BlockRangeIndexHolder.set(index);
    }

    public static void startDateRangeIndex(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        startDateRangeIndex(tableName, null, startTime, endTime);
    }

    public static void startDateRangeIndex(Class<?> entityClz, String tableAlias, LocalDateTime startTime, LocalDateTime endTime) {
        TableName tableName = entityClz.getAnnotation(TableName.class);
        Assert.notNull(tableName, "entityClz must be annotated with @TableName");
        startDateRangeIndex(tableName.value(), tableAlias, startTime, endTime);
    }

    public static void startDateRangeIndex(Class<?> entityClz, LocalDateTime startTime, LocalDateTime endTime) {
        startDateRangeIndex(entityClz, null, startTime, endTime);
    }

    /**
     * 根据时间范围获取id范围
     * @param tableName
     * @param startTime
     * @param endTime
     * @return
     */
    public static Range getRangeByDateTime(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        return selectParser.deduceRange(tableName, startTime, endTime);
    }

    public static Range getRangeByDateTime(Class<?> entityClz, LocalDateTime startTime, LocalDateTime endTime) {
        TableName tableName = entityClz.getAnnotation(TableName.class);
        Assert.notNull(tableName, "entityClz must be annotated with @TableName");
        return getRangeByDateTime(tableName.value(), startTime, endTime);
    }
}
