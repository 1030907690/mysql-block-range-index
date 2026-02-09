package com.zzq.mysqlblockrangeindex.bean;


import java.time.LocalDateTime;
import java.util.Date;

/**
 * 基础实体类
 * @author Zhou Zhongqing
 * @since 2/4/2026 10:21 PM
 */
public class BasicEntity {
    public BasicEntity() {
    }

    public BasicEntity(Integer id, LocalDateTime createTime) {
        this.id = id;
        this.createTime = createTime;
    }

    private Integer id;

    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "BasicEntity{" +
                "id=" + id +
                ", createTime=" + createTime +
                '}';
    }
}
