package com.zzq.mysqlblockrangeindex.parser;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.zzq.mysqlblockrangeindex.autoconfigure.MysqlBlockRangeIndexProperties;
import com.zzq.mysqlblockrangeindex.bean.BasicEntity;
import com.zzq.mysqlblockrangeindex.bean.Range;
import com.zzq.mysqlblockrangeindex.bean.Table;
import com.zzq.mysqlblockrangeindex.constant.Constant;
import com.zzq.mysqlblockrangeindex.index.BlockRangeIndex;
import com.zzq.mysqlblockrangeindex.index.BlockRangeIndexHolder;
import com.zzq.mysqlblockrangeindex.utils.BlockRangeIndexSpringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 查询语句解析
 * @author Zhou Zhongqing
 * @since 2/2/2026 11:00 PM
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
        BlockRangeIndex blockRangeIndex = BlockRangeIndexHolder.get();
//        Range range = deduceRange("t_user", LocalDateTime.parse("2026-02-05 11:33:17", DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)),
//                LocalDateTime.parse("2026-02-05 14:34:15", DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        Range range = deduceRange(blockRangeIndex.getTableName(), blockRangeIndex.getStartTime(), blockRangeIndex.getEndTime());


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
                plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(assemblyRangeWhere(range,blockRangeIndex)));
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
        } else {
            AndExpression and = null;
            try {
                and = new AndExpression(where, CCJSqlParserUtil.parseCondExpression(assemblyRangeWhere(range,blockRangeIndex)));
            } catch (JSQLParserException e) {
                throw new RuntimeException(e);
            }
//            log.info("{} ", where);
            plainSelect.setWhere(and);
        }
//        log.info("{} ", plainSelect.getWhere());
//        log.info("{} ", plainSelect);
        return plainSelect.toString();
    }

    private String assemblyRangeWhere(Range range,BlockRangeIndex blockRangeIndex) {
        String tableAliasDot = ObjectUtils.isEmpty(blockRangeIndex.getTableAlias()) ? StrUtil.EMPTY : blockRangeIndex.getTableAlias() + StrUtil.DOT;

        MysqlBlockRangeIndexProperties properties = BlockRangeIndexSpringUtil.getBean(MysqlBlockRangeIndexProperties.class);
        Map<String, Table> tableMap = properties.getTables().stream().collect(Collectors.toMap(Table::getName, Function.identity()));
        Table table = tableMap.get(blockRangeIndex.getTableName());
        Assert.notNull(table, blockRangeIndex.getTableName() + "table not mapping");
        String primaryKeyAutoIncrementColumn = table.getPrimaryKeyAutoIncrementColumn();
        String aliasPkAutoIncColumn = tableAliasDot + primaryKeyAutoIncrementColumn;
        StringBuilder rangeWhere = new StringBuilder();
        if (range.getMinId() != null) {
            rangeWhere.append(aliasPkAutoIncColumn +" >= ").append(range.getMinId());
        }
        if (range.getMaxId() != null) {
            if (rangeWhere.length() > 0) {
                rangeWhere.append(" and " + aliasPkAutoIncColumn + " <= ").append(range.getMaxId());
            } else {
                rangeWhere.append(aliasPkAutoIncColumn + " <= ").append(range.getMaxId());
            }
        }
        return rangeWhere.toString();
    }

    public Range deduceRange(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        StringRedisTemplate stringRedisTemplate = BlockRangeIndexSpringUtil.getBean(StringRedisTemplate.class);
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(Constant.MYSQL_BLOCK_RANGE_INDEX + tableName);
        List<BasicEntity> basicEntities = convertBasicEntity(entries);
        return matchRange(basicEntities, startTime, endTime);
    }

    private Range matchRange(List<BasicEntity> basicEntities, LocalDateTime startTime, LocalDateTime endTime) {
        Range range = new Range();
        if (!CollectionUtils.isEmpty(basicEntities)) {
            for (int i = 0; i < basicEntities.size(); i++) {
                BasicEntity basicEntity = basicEntities.get(i);
                // 开始时间是basicEntity.getCreateTime() 之前的时间 或者等于
                if (!ObjectUtils.isEmpty(startTime) && (startTime.isBefore(basicEntity.getCreateTime()) || startTime.isEqual(basicEntity.getCreateTime()))) {
                    // 最终最小id前移一位
                    if (i > 0) {
                        BasicEntity prevBasicEntity = basicEntities.get(i - 1);
                        range.setMinId(prevBasicEntity.getId());
                    } else {
                        range.setMinId(0);
                    }
                    break;
                }

            }


            for (BasicEntity basicEntity : basicEntities) {
                //   结束时间是basicEntity.getCreateTime() 之前的时间 或者等于
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
