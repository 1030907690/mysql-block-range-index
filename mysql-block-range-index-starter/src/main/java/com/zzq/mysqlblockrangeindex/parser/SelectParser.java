package com.zzq.mysqlblockrangeindex.parser;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/2/2026 11:00 PM
 */
public class SelectParser {

    private static final Logger log = LoggerFactory.getLogger(SelectParser.class);

    public static void main(String[] args) throws Exception {
        String sql = "SELECT id FROM  t_user WHERE account = ? and real_name = ? or district = ? or a = ?";
        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = plainSelect.getWhere();
        AndExpression and = new AndExpression(where, CCJSqlParserUtil.parseCondExpression("b = ? and a = ?"));
        log.info("{} ",where);
        plainSelect.setWhere(and);
        log.info("{} ",plainSelect.getWhere());
        log.info("{} ",plainSelect);
    }
}
