package com.zzq.mysqlblockrangeindex.index;


import java.time.LocalDateTime;

/**
 * 实体
 * @author Zhou Zhongqing
 * @date: 2/5/2026 10:49 PM
 */
public class BlockRangeIndex {
    public BlockRangeIndex() {
    }

    public BlockRangeIndex(String tableName, String tableAlias, LocalDateTime startTime, LocalDateTime endTime) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private String tableName;
    private String tableAlias;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
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
