package com.zzq.mysqlblockrangeindex.index;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

/**
 * 索引帮助类
 * @author zzq
 * @date 2026/02/07 14:29:28
 */

public class IndexHelper {

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


}
