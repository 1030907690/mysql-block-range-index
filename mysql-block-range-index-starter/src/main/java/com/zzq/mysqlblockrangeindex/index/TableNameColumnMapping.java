package com.zzq.mysqlblockrangeindex.index;

import com.zzq.mysqlblockrangeindex.bean.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表名字段映射
 *
 * @author zzq
 * @since 2026/02/09 17:20:48
 */
public class TableNameColumnMapping {

    private final static Map<String, Table> tableNameColumnMapping = new ConcurrentHashMap<>();

    public static void add(String tableName, Table table) {
        if (!containsKey(tableName)) {
            addCover(tableName, table);
        }
    }

    public static void addCover(String tableName, Table table) {
        tableNameColumnMapping.put(tableName, table);
    }

    public static Table get(String tableName) {
        return tableNameColumnMapping.get(tableName);
    }

    public static Map<String, Table> getAll() {
        return new HashMap<>(tableNameColumnMapping);
    }

    public static boolean containsKey(String tableName) {
        return tableNameColumnMapping.containsKey(tableName);
    }

}
