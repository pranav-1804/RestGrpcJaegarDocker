package com.example.grpc.interceptor;

import io.grpc.*;
import io.opentelemetry.api.trace.Span;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class DemoIdGrpcInterceptor implements ServerInterceptor {

    public static final Metadata.Key<String> DEMO_KEY = Metadata.Key.of("x-demo-id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<String> DEMO_CTX_KEY = Context.key("demo.id");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String demoId = headers.get(DEMO_KEY);
        if (demoId != null && !demoId.isEmpty()) {
            // store demo id in the gRPC Context so service impls can access it
            Context ctx = Context.current().withValue(DEMO_CTX_KEY, demoId);
            return Contexts.interceptCall(ctx, call, headers, next);
        }
        return next.startCall(call, headers);
    }
}
