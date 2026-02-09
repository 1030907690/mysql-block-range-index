package com.zzq.mysqlblockrangeindex.autoconfigure;


import cn.hutool.extra.spring.SpringUtil;
import com.zzq.mysqlblockrangeindex.job.DateBlockRangeIndexJob;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 自动装配
 * @author Zhou Zhongqing
 @since 2/2/2026 10:27 PM
 */
@Configuration
@ConditionalOnBean({SqlSessionFactory.class})
@EnableConfigurationProperties({MysqlBlockRangeIndexProperties.class})
@AutoConfigureAfter({MybatisAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, RedisAutoConfiguration.class})
public class MysqlBlockRangeIndexAutoConfiguration implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(MysqlBlockRangeIndexAutoConfiguration.class);
    private final List<SqlSessionFactory> sqlSessionFactoryList;
//    private final MysqlBlockRangeIndexProperties properties;

    public MysqlBlockRangeIndexAutoConfiguration(List<SqlSessionFactory> sqlSessionFactoryList
                                                 //MysqlBlockRangeIndexProperties properties
    ) {
        this.sqlSessionFactoryList = sqlSessionFactoryList;
//        this.properties = properties;
    }

    @Bean
    public DateBlockRangeIndexJob blockRangeIndexJob(JdbcTemplate jdbcTemplate, StringRedisTemplate stringRedisTemplate) {
        return new DateBlockRangeIndexJob(jdbcTemplate, stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(SpringUtil.class)
    public SpringUtil springUtil() {
        return new SpringUtil();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
      /*  Interceptor interceptor = new MysqlBlockRangeIndexInterceptor();
        interceptor.setProperties(null);
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            if (!containsInterceptor(configuration, interceptor)) {
                log.info("add interceptor: {}", interceptor.getClass().getName());
                configuration.addInterceptor(interceptor);
            }
        }*/
    }

    /**
     * 是否已经存在相同的拦截器
     *
     * @param configuration
     * @param interceptor
     * @return
     */
    private boolean containsInterceptor(org.apache.ibatis.session.Configuration configuration, Interceptor interceptor) {
        try {
            // getInterceptors since 3.2.2
            return configuration.getInterceptors().stream().anyMatch(config -> interceptor.getClass().isAssignableFrom(config.getClass()));
        } catch (Exception e) {
            return false;
        }
    }
}
