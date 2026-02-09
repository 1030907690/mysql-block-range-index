package com.zzq.mysqlblockrangeindex.parser;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.List;

/**
 *
 * @author: Zhou Zhongqing
 * @date: 2/5/2026 10:37 PM
 */
public class CreateTableParser {
    public Integer parseAutoIncrement(String ddlSql) {
        CreateTable createTable = null;
        try {
            createTable = (CreateTable) CCJSqlParserUtil.parse(ddlSql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        List<?> options = createTable.getTableOptionsStrings();

        String autoIncrementValue = null;
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                String option = options.get(i).toString();
                if ("AUTO_INCREMENT".equalsIgnoreCase(option) && i + 2 < options.size()) {
                    // JSqlParser 通常把 "AUTO_INCREMENT", "=", "8" 拆成三个连续元素
                    // 所以我们要跳过等号取后面的值
                    autoIncrementValue = options.get(i + 2).toString();
                    break;
                }
            }
        }
        return Integer.parseInt(autoIncrementValue);
    }

}
