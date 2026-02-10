package com.zzq.mysqlblockrangeindex.autoconfigure;


import com.zzq.mysqlblockrangeindex.bean.Table;
import com.zzq.mysqlblockrangeindex.constant.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 属性配置
 * @author Zhou Zhongqing
 * @since 2/2/2026 10:29 PM
 */
@ConfigurationProperties(prefix = MysqlBlockRangeIndexProperties.MYSQL_BLOCK_RANGE_INDEX_PREFIX)
public class MysqlBlockRangeIndexProperties {

    public static final String MYSQL_BLOCK_RANGE_INDEX_PREFIX = "mysql-block-range-index";

    private List<Table> tables;


    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }


}
