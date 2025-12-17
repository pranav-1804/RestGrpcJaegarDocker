package com.example.grpc.config;

import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.config.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    public GrpcServerConfigurer demoIdInterceptorConfigurer(ServerInterceptor demoIdGrpcInterceptor) {
        return serverBuilder -> serverBuilder.intercept(demoIdGrpcInterceptor);
    }
}
