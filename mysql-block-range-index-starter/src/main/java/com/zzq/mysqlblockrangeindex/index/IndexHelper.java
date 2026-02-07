package com.zzq.mysqlblockrangeindex.index;

import java.time.LocalDateTime;

/**
 * @author zzq
 * @date 2026/02/07 14:29:28
 */

public class IndexHelper {

    public static void startDateRangeIndex(String tableName, String tableAlias, LocalDateTime startTime, LocalDateTime endTime) {
        BlockRangeIndex index = new BlockRangeIndex(tableName, tableAlias, startTime, endTime);
        BlockRangeIndexHolder.set(index);
    }
}
