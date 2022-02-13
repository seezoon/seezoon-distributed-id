package com.seezoon.interfaces.grpc;

import com.seezoon.DistributedIdAo.DistributedIdReq;
import com.seezoon.DistributedIdAo.DistributedIdResp;
import com.seezoon.DistributedIdGrpc.DistributedIdImplBase;
import com.seezoon.application.SegmentService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class DistributedIdService extends DistributedIdImplBase {

    private final SegmentService segmentService;

    @SneakyThrows
    @Override
    public void get(DistributedIdReq request, StreamObserver<DistributedIdResp> responseObserver) {
        Thread.sleep(10000);
        String bizTag = request.getBizTag();
        String token = request.getToken();
        long value = segmentService.getId(bizTag, token);
        DistributedIdResp resp = DistributedIdResp.newBuilder().setValue(value).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
