package com.zzq.mysqlblockrangeindex.sample.controller;

import com.zzq.mysqlblockrangeindex.sample.model.User;
import com.zzq.mysqlblockrangeindex.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zzq
 * @date 2026/02/03 18:39:50
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    public List<User> list() {
        return userService.list();
    }

}
