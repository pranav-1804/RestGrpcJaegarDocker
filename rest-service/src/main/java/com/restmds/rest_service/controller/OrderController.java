package com.restmds.rest_service.controller;
import com.restmds.rest_service.model.OrderData;
import com.restmds.rest_service.service.OrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

    @RestController
    @RequestMapping("/orders")
    public class OrderController {
        @GetMapping("/test")
        public String home() {
            return "REST Service is running!";
        }

        @Autowired
        private OrderService service;

        @PostMapping
        public OrderData createOrder(@RequestBody OrderData order) {
            return service.create(order);
        }

        @GetMapping
        public List<OrderData> getAllOrders() {
            return service.findAll();
        }

        @GetMapping("/{id}")
        public OrderData getOrder(@PathVariable Long id) {
            return service.findById(id);
        }

        @PutMapping("/{id}")
        public OrderData updateOrder(@RequestBody OrderData order, @PathVariable Long id){
            order.setOrderId(id);
            return service.updateOrder(order);
        }

        @DeleteMapping("/{id}")
        public String removeOrder(@PathVariable Long id){
            service.removeOrder(id);
            return "Order Deleted Successfully !!";
        }

    }
