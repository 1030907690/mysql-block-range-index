package com.zzq.mysqlblockrangeindex.parser;


import cn.hutool.core.date.DatePattern;
import cn.hutool.extra.spring.SpringUtil;
import com.zzq.mysqlblockrangeindex.bean.BasicEntity;
import com.zzq.mysqlblockrangeindex.bean.Range;
import com.zzq.mysqlblockrangeindex.constant.Constant;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
        log.info("{} ", where);
        plainSelect.setWhere(and);
        log.info("{} ", plainSelect.getWhere());
        log.info("{} ", plainSelect);
    }


    public String appendPrimaryKeyAutoIncrementWhere(String originalSql) {

        Range range = deduceRange("t_user", LocalDateTime.parse("2025-12-03 01:00:01", DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)),
                LocalDateTime.parse("2026-01-01 23:59:59", DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));

        ;
        Select select = null;
        try {
            select = (Select) CCJSqlParserUtil.parse(originalSql);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        Expression where = plainSelect.getWhere();
        if (where == null) {
            try {
                plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(assemblyRangeWhere(range)));
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
        } else {
            AndExpression and = null;
            try {
                and = new AndExpression(where, CCJSqlParserUtil.parseCondExpression(assemblyRangeWhere(range)));
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
            log.info("{} ", where);
            plainSelect.setWhere(and);
        }
//        log.info("{} ", plainSelect.getWhere());
//        log.info("{} ", plainSelect);
        return plainSelect.toString();
    }

    private String assemblyRangeWhere(Range range) {
        StringBuilder rangeWhere = new StringBuilder();
        if (range.getMinId() != null){
            rangeWhere.append("id >= ").append(range.getMinId());
        }
        if (range.getMaxId() != null){
            if (rangeWhere.length() > 0) {
                rangeWhere.append(" and id <= ").append(range.getMaxId());
            } else {
                rangeWhere.append("id <= ").append(range.getMaxId());
            }
        }
        return rangeWhere.toString();
    }

    public Range deduceRange(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        StringRedisTemplate stringRedisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(Constant.MYSQL_BLOCK_RANGE_INDEX + tableName);
        List<BasicEntity> basicEntities = convertBasicEntity(entries);
        return matchRange(basicEntities, startTime, endTime);
    }

    private Range matchRange(List<BasicEntity> basicEntities, LocalDateTime startTime, LocalDateTime endTime) {
        Range range = new Range();
        if (!CollectionUtils.isEmpty(basicEntities)) {
            // 大于等于开始时间
            for (BasicEntity basicEntity : basicEntities) {
                if (!ObjectUtils.isEmpty(startTime) && (startTime.isBefore(basicEntity.getCreateTime()) || startTime.isEqual(basicEntity.getCreateTime()))) {
                    range.setMinId(basicEntity.getId());
                    break;
                }
            }

            // 小于等于结束时间
            for (BasicEntity basicEntity : basicEntities) {
                if (!ObjectUtils.isEmpty(endTime) && (endTime.isBefore(basicEntity.getCreateTime()) || endTime.isEqual(basicEntity.getCreateTime()))) {
                    range.setMaxId(basicEntity.getId());
                    break;
                }
            }
        }
        return range;
    }


    private List<BasicEntity> convertBasicEntity(Map<Object, Object> entries) {
        if (!CollectionUtils.isEmpty(entries)) {
            List<BasicEntity> result = entries.entrySet().stream().map(entry -> new BasicEntity(Integer.parseInt(entry.getValue().toString()),
                    LocalDateTime.parse(entry.getKey().toString(), DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)))).collect(Collectors.toList());
            // 按照自增主键排序,从小到大
            return result.stream().sorted(Comparator.comparingInt(BasicEntity::getId)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
