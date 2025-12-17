package com.example.grpc.interceptor;

import io.grpc.*;
import io.opentelemetry.api.trace.Span;
import org.springframework.stereotype.Component;

@Component
public class DemoIdGrpcInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> DEMO_KEY = Metadata.Key.of("x-demo-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String demoId = headers.get(DEMO_KEY);
        if (demoId != null && !demoId.isEmpty()) {
            try {
                Span.current().setAttribute("demo.id", demoId);
            } catch (Exception e) {
                // ignore if OpenTelemetry API not available
            }
        }
        return next.startCall(call, headers);
    }
}
