package com.zzq.mysqlblockrangeindex.bean;


import com.baomidou.mybatisplus.annotation.TableName;
import com.zzq.mysqlblockrangeindex.constant.Constant;
import org.springframework.util.Assert;

/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/4/2026 10:15 PM
 */
public class Table {
    public Table() {
    }

    public Table(String name,String primaryKeyAutoIncrementColumn, String createTimeColumn) {
        Assert.notNull(name, "name can not be null");
        Assert.notNull(primaryKeyAutoIncrementColumn, "primaryKeyAutoIncrementColumn can not be null");
        Assert.notNull(createTimeColumn, "createTimeColumn can not be null");

        this.name = name;
        this.primaryKeyAutoIncrementColumn = primaryKeyAutoIncrementColumn;
        this.createTimeColumn = createTimeColumn;
    }

    public Table(String name) {
        this(name, Constant.DEFAULT_PRIMARY_KEY_AUTO_INCREMENT_COLUMN, Constant.DEFAULT_CREATE_TIME_COLUMN);
    }

    public Table(Class<?> entityClz) {
        this(entityClz.getAnnotation(TableName.class).value(), Constant.DEFAULT_PRIMARY_KEY_AUTO_INCREMENT_COLUMN, Constant.DEFAULT_CREATE_TIME_COLUMN);
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
