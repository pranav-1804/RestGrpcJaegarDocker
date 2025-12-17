package com.example.grpc;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class HelloGrpcService extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        // set demo.id attribute from interceptor-provided context if present
        try {
            String demoId = com.example.grpc.interceptor.DemoIdGrpcInterceptor.DEMO_CTX_KEY.get();
            if (demoId != null && !demoId.isEmpty()) {
                Span.current().setAttribute("demo.id", demoId);
            }
        } catch (Exception e) { /* ignore */ }

        String message = "Hello, " + request.getName();

        HelloResponse response = HelloResponse.newBuilder()
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}