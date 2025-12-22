package com.example.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class DebugGrpcService extends DebugServiceGrpc.DebugServiceImplBase {

    @Override
    public void getDebugInfo(
            DebugReq request,
            StreamObserver<DebugResponse> responseObserver) {

        DebugResponse response = DebugResponse.newBuilder()
                .putData("status", "OK")
                .putData("service", "debug")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
