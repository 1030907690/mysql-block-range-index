package com.zzq.mysqlblockrangeindex.interceptor;

import com.github.pagehelper.BoundSqlInterceptor;
import com.zzq.mysqlblockrangeindex.parser.SelectParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;


/**
 * @author zzq
 * @date 2026/2/5 18:11:01
 */
public class PageHelperBoundSqlInterceptor implements BoundSqlInterceptor {

    private final SelectParser selectParser = new SelectParser();
    @Override
    public BoundSql boundSql(Type type, BoundSql boundSql, CacheKey cacheKey, Chain chain) {
        if (Type.ORIGINAL == type) {
            MetaObject metaObject = SystemMetaObject.forObject(boundSql);
            metaObject.setValue("sql", selectParser.appendPrimaryKeyAutoIncrementWhere(boundSql.getSql()));
        }
        return chain.doBoundSql(type, boundSql, cacheKey);
    }
}
