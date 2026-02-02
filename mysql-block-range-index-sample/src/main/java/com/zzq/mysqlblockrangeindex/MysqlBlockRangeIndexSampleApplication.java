package com.zzq.mysqlblockrangeindex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MysqlBlockRangeIndexSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MysqlBlockRangeIndexSampleApplication.class, args);
		  String sql = "SELECT id FROM  eb_user WHERE account = ? and real_name = ? or district = ? or a = ?";
        Select select = (Select) CCJSqlParserUtil.parse(sql);

        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = plainSelect.getWhere();
        AndExpression and = new AndExpression(where, CCJSqlParserUtil.parseCondExpression("b = ? and a = ?"));
        System.out.println(where);
        plainSelect.setWhere(and);
        System.out.println(plainSelect.getWhere());



	}

}
