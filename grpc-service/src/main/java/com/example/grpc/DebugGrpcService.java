package com.example.grpc;

import java.time.Instant;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class DebugGrpcService extends DebugServiceGrpc.DebugServiceImplBase {

    @Override
    public void getDebugInfo(
            DebugReq request,
            StreamObserver<DebugResponse> responseObserver) {

                String podName = getenvOrDefault("POD_NAME", "unknown");
        String podIp = getenvOrDefault("POD_IP", "unknown");
        String nodeName = getenvOrDefault("HOSTNAME", "unknown");

        DebugResponse response = DebugResponse.newBuilder()
                .putData("service", "grpc-service")
                .putData("pod.name", podName)
                .putData("pod.ip", podIp)
                .putData("node.name", nodeName)
                .putData("timestamp", Instant.now().toString())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private String getenvOrDefault(String name, String def) {
        String v = System.getenv(name);
        return (v == null || v.isEmpty()) ? def : v;
    }
}
