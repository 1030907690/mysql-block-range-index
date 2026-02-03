package com.zzq.mysqlblockrangeindex.sample.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzq.mysqlblockrangeindex.sample.mapper.UserMapper;
import com.zzq.mysqlblockrangeindex.sample.model.User;
import com.zzq.mysqlblockrangeindex.sample.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author zzq
 * @date 2026/02/03 18:38:39
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
