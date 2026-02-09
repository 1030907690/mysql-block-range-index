package com.zzq.mysqlblockrangeindex.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 属性配置
 * @author Zhou Zhongqing
 * @date 2/2/2026 10:29 PM
 */
@ConfigurationProperties(prefix = MysqlBlockRangeIndexProperties.MYSQL_BLOCK_RANGE_INDEX_PREFIX)
public class MysqlBlockRangeIndexProperties {

    public static final String MYSQL_BLOCK_RANGE_INDEX_PREFIX = "mysql-block-range-index";

}
