package com.zzq.mysqlblockrangeindex.sample.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.zzq.mysqlblockrangeindex.index.IndexHelper;
import com.zzq.mysqlblockrangeindex.sample.model.User;
import com.zzq.mysqlblockrangeindex.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author zzq
 @since 2026/02/03 18:39:50
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/list")
    public List<User> list() {
        //LocalDateTime 转 Date
        LocalDateTime endTime = LocalDateTime.now().plusDays(1);
        LocalDateTime startTime = endTime.minusDays(35);
        IndexHelper.startDateRangeIndex(User.class, startTime, endTime);
        PageHelper.startPage(1, 100);
        return userService.list(Wrappers.lambdaQuery(User.class).gt(User::getCreateTime, startTime).lt(User::getCreateTime, endTime));
    }

}
