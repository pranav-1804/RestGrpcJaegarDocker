package com.example.grpc;

import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;

@Configuration
public class GrpcConfig {

    @Bean
    public BindableService reflectionService() {
        return ProtoReflectionService.newInstance();
    }
}