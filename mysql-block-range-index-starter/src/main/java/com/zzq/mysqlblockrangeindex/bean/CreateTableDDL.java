package com.zzq.mysqlblockrangeindex.bean;


/**
 * 建表语句
 * @author Zhou Zhongqing
 * @since 2/5/2026 10:38 PM
 */
public class CreateTableDDL {
    private String createTable;

    public String getCreateTable() {
        return createTable;
    }

    public void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    @Override
    public String toString() {
        return "CreateTableDDL{" +
                "createTable='" + createTable + '\'' +
                '}';
    }
}
