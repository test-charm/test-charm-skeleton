package com.odde.atddv2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odde.atddv2.entity.Order;
import com.odde.atddv2.repo.OrderRepo;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.zh_cn.假如;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderSteps {

    @Autowired
    private OrderRepo orderRepo;

    @假如("存在如下订单:")
    public void 存在如下订单(DataTable table) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);
        table.asMaps().forEach(map -> orderRepo.save(objectMapper.convertValue(map, Order.class)));
    }

}
