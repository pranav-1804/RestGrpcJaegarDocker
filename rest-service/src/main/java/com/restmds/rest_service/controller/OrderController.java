package com.restmds.rest_service.controller;
import com.restmds.rest_service.model.OrderData;
import com.restmds.rest_service.service.OrderService;
import org.springframework.web.bind.annotation.*;
import io.opentelemetry.api.trace.Span;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

    @RestController
    @RequestMapping("/orders")
    public class OrderController {
        @Autowired
        private OrderService service;

        @PostMapping
        public OrderData createOrder(@RequestBody OrderData order, @RequestHeader(value = "X-Demo-Id", required = false) String demoId) {
            if (demoId != null && !demoId.isEmpty()) { try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { } }
            return service.create(order);
        }

        @GetMapping
        public List<OrderData> getAllOrders(@RequestHeader(value = "X-Demo-Id", required = false) String demoId) {
            if (demoId != null && !demoId.isEmpty()) { try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { } }
            return service.findAll();
        }

        @GetMapping("/{id}")
        public OrderData getOrder(@PathVariable Long id, @RequestHeader(value = "X-Demo-Id", required = false) String demoId) {
            if (demoId != null && !demoId.isEmpty()) { try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { } }
            return service.findById(id);
        }

        @PutMapping("/{id}")
        public OrderData updateOrder(@RequestBody OrderData order, @PathVariable Long id, @RequestHeader(value = "X-Demo-Id", required = false) String demoId){
            if (demoId != null && !demoId.isEmpty()) { try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { } }
            order.setOrderId(id);
            return service.updateOrder(order);
        }

        @DeleteMapping("/{id}")
        public String removeOrder(@PathVariable Long id, @RequestHeader(value = "X-Demo-Id", required = false) String demoId){
            if (demoId != null && !demoId.isEmpty()) { try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { } }
            service.removeOrder(id);
            return "Order Deleted Successfully !!";
        }

    }
