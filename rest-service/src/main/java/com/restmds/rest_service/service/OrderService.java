package com.restmds.rest_service.service;

import com.restmds.rest_service.model.OrderData;
import com.restmds.rest_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    public OrderData create(OrderData order) {
        return repository.save(order);
    }

    public List<OrderData> findAll() {
        List<OrderData> orderList = new ArrayList<>();
        repository.findAll().forEach(orderList::add);
        return orderList;
    }

    public OrderData findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public OrderData updateOrder(OrderData order) {
        return repository.save(order);
    }

    public void removeOrder(Long id) {
        repository.deleteById(id);
    }
}