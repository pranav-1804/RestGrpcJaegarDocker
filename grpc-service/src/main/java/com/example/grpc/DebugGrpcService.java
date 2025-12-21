package com.example.grpc;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashMap;
import java.util.Map;

@GrpcService
public class DebugGrpcService extends DebugGrpcServiceGrpc.DebugServiceImplBase {

    @Override
    public void getDebugInfo(Empty request, StreamObserver<DebugResponse> responseObserver) {
        Map<String, String> debugData = new HashMap<>();

        try {
            String demoId = com.example.grpc.interceptor.DemoIdGrpcInterceptor.DEMO_CTX_KEY.get();
            if (demoId != null && !demoId.isEmpty()) {
                Span.current().setAttribute("demo.id", demoId);
                debugData.put("demo.id", demoId);
            }
        } catch (Exception e) {
            // ignore safely
        }

        // Add any debug values you want
        debugData.put("service", "debug-service");
        debugData.put("status", "OK");

        DebugResponse response = DebugResponse.newBuilder()
                .putAllData(debugData)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
