package com.zzq.mysqlblockrangeindex.datasource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author zzq
 * @date 2026/02/04 13:20:19
 */
public class LocalDataSourceStore {


    public LocalDataSourceStore() {
    }


    public LocalDataSourceStore(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Connection getConnection(){
        return DataSourceUtils.getConnection(dataSource);
    }


    private DataSource dataSource;


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
