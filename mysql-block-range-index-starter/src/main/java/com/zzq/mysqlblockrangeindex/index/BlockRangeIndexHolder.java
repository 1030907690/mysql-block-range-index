package com.zzq.mysqlblockrangeindex.index;


/**
 * ThreadLocal 存储
 * @author Zhou Zhongqing
 * @since 2/5/2026 10:47 PM
 */
public class BlockRangeIndexHolder {
    private static final ThreadLocal<BlockRangeIndex> LOCAL_BLOCK_RANGE_INDEX = new ThreadLocal();
    public static void set(BlockRangeIndex index) {
        LOCAL_BLOCK_RANGE_INDEX.set(index);
    }

    public static void clear() {
        LOCAL_BLOCK_RANGE_INDEX.remove();
    }


    public static BlockRangeIndex get() {
        return LOCAL_BLOCK_RANGE_INDEX.get();
    }

}
