package com.company.order.canal;

import com.company.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Slf4j
@Component
@CanalTable(value = "order") // 对应的数据库表名
public class OrderHandler implements EntryHandler<Order> {

    @Override
    public void delete(Order t) {
        log.info("删除操作: {}", t);
    }

    @Override
    public void insert(Order t) {
        log.info("插入操作: {}", t);
    }

    @Override
    public void update(Order before, Order after) {
        log.info("更新操作，更新前: {},更新后: {}", before, after);
    }
}
