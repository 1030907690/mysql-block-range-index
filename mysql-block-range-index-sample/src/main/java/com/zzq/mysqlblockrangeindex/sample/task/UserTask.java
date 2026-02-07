package com.zzq.mysqlblockrangeindex.sample.task;


import com.zzq.mysqlblockrangeindex.job.BlockRangeIndexJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Zhou Zhongqing
 * @date: 2/4/2026 9:33 PM
 */
@Component
@Order
public class UserTask implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(UserTask.class);

    @Autowired
    private BlockRangeIndexJob blockRangeIndexJob;

//    @Scheduled(fixedDelay = 1000)
    public void task() {
        log.info("task");
        blockRangeIndexJob.execute();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        task();
    }
}
