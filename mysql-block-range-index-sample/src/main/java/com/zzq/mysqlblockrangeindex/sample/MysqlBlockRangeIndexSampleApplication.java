package com.zzq.mysqlblockrangeindex.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zzq.mysqlblockrangeindex.sample.mapper")
public class MysqlBlockRangeIndexSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlBlockRangeIndexSampleApplication.class, args);
	}

}
