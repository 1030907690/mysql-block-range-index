package com.zzq.mysqlblockrangeindex.bean;


/**
 * 范围
 * @author Zhou Zhongqing
 * @date: 2/5/2026 10:36 PM
 */
public class Range {
    private Integer minId;
    private Integer maxId;

    public Integer getMinId() {
        return minId;
    }

    public void setMinId(Integer minId) {
        this.minId = minId;
    }

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }
}
