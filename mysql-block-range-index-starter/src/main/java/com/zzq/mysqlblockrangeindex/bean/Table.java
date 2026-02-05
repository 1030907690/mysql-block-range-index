package com.zzq.mysqlblockrangeindex.bean;


/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/4/2026 10:15 PM
 */
public class Table {
    public Table() {
    }

    public Table(String name,String primaryKeyAutoIncrementColumn, String createTimeColumn) {
        this.name = name;
        this.primaryKeyAutoIncrementColumn = primaryKeyAutoIncrementColumn;
        this.createTimeColumn = createTimeColumn;
    }

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
