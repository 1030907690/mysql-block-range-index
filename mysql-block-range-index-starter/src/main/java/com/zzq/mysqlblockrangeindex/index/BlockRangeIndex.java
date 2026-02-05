package com.zzq.mysqlblockrangeindex.index;


import java.time.LocalDateTime;

/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/5/2026 10:49 PM
 */
public class BlockRangeIndex {
    public BlockRangeIndex() {
    }

    public BlockRangeIndex(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        this.tableName = tableName;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    private String tableName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
