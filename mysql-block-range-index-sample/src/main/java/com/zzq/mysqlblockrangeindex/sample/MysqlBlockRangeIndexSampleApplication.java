package com.zzq.mysqlblockrangeindex.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.zzq.mysqlblockrangeindex.sample.mapper")
@EnableScheduling
@EnableTransactionManagement(proxyTargetClass = true)
public class MysqlBlockRangeIndexSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlBlockRangeIndexSampleApplication.class, args);
	}

}
