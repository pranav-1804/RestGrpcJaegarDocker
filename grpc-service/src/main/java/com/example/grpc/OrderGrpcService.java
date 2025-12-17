package com.example.grpc;

import com.example.grpc.model.OrderData;
import com.example.grpc.service.OrderService;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    @Autowired
    private OrderService service;

    @Override
    public void createOrder(Order request, StreamObserver<Order> responseObserver) {
        OrderData entity = new OrderData(request.getProduct(), request.getQuantity());
        if (request.getOrderId() != 0L) {
            entity.setOrderId(request.getOrderId());
        }
        OrderData saved = service.create(entity);
        Order resp = Order.newBuilder()
                .setOrderId(saved.getOrderId() == null ? 0L : saved.getOrderId())
                .setProduct(saved.getProduct() == null ? "" : saved.getProduct())
                .setQuantity(saved.getQuantity() == null ? 0 : saved.getQuantity())
                .build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void getOrder(OrderId request, StreamObserver<Order> responseObserver) {
        Long id = request.getId();
        OrderData found = service.findById(id);
        if (found == null) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Order not found").asRuntimeException());
            return;
        }
        Order resp = Order.newBuilder()
                .setOrderId(found.getOrderId() == null ? 0L : found.getOrderId())
                .setProduct(found.getProduct() == null ? "" : found.getProduct())
                .setQuantity(found.getQuantity() == null ? 0 : found.getQuantity())
                .build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void listOrders(Empty request, StreamObserver<OrderList> responseObserver) {
        List<OrderData> all = service.findAll();
        OrderList.Builder builder = OrderList.newBuilder();
        builder.addAllOrders(all.stream().map(o -> Order.newBuilder()
                .setOrderId(o.getOrderId() == null ? 0L : o.getOrderId())
                .setProduct(o.getProduct() == null ? "" : o.getProduct())
                .setQuantity(o.getQuantity() == null ? 0 : o.getQuantity())
                .build()).collect(Collectors.toList()));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateOrder(Order request, StreamObserver<Order> responseObserver) {
        if (request.getOrderId() == 0L) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Order id required for update").asRuntimeException());
            return;
        }
        OrderData entity = new OrderData(request.getProduct(), request.getQuantity());
        entity.setOrderId(request.getOrderId());
        OrderData updated = service.updateOrder(entity);
        Order resp = Order.newBuilder()
                .setOrderId(updated.getOrderId() == null ? 0L : updated.getOrderId())
                .setProduct(updated.getProduct() == null ? "" : updated.getProduct())
                .setQuantity(updated.getQuantity() == null ? 0 : updated.getQuantity())
                .build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteOrder(OrderId request, StreamObserver<Empty> responseObserver) {
        Long id = request.getId();
        service.removeOrder(id);
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
