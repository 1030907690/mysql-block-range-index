package com.zzq.mysqlblockrangeindex.interceptor;

import com.github.pagehelper.BoundSqlInterceptor;
import com.zzq.mysqlblockrangeindex.index.BlockRangeIndex;
import com.zzq.mysqlblockrangeindex.index.BlockRangeIndexHolder;
import com.zzq.mysqlblockrangeindex.index.IndexHelper;
import com.zzq.mysqlblockrangeindex.parser.SelectParser;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 拦截器
 * @author zzq
 * @date 2026/2/5 18:11:01
 */
public class PageHelperBoundSqlInterceptor implements BoundSqlInterceptor {

    private final Logger log = LoggerFactory.getLogger(PageHelperBoundSqlInterceptor.class);
    private final SelectParser selectParser = new SelectParser();
    @Override
    public BoundSql boundSql(Type type, BoundSql boundSql, CacheKey cacheKey, Chain chain) {
        if (Type.ORIGINAL == type) {
            if (shouldUseDateRangeIndex()) {
                try {
                    MetaObject metaObject = SystemMetaObject.forObject(boundSql);
                    metaObject.setValue("sql", selectParser.appendPrimaryKeyAutoIncrementWhere(boundSql.getSql()));
                }catch (Exception e){
                    log.error("index error {}",e);
                    throw new RuntimeException(e);
                }finally {
                    BlockRangeIndexHolder.clear();
                }
            }
        }
        return chain.doBoundSql(type, boundSql, cacheKey);
    }

    private boolean shouldUseDateRangeIndex() {
        BlockRangeIndex blockRangeIndex = BlockRangeIndexHolder.get();
        return blockRangeIndex != null;
    }
}
