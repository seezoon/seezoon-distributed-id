package com.seezoon.interfaces.grpc;

import com.seezoon.DistributedIdAo.DistributedIdReq;
import com.seezoon.DistributedIdAo.DistributedIdResp;
import com.seezoon.DistributedIdGrpc.DistributedIdImplBase;
import com.seezoon.application.SegmentService;
import com.seezoon.grpc.annotation.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class DistributedIdService extends DistributedIdImplBase {

    private final SegmentService segmentService;

    @Override
    public void get(DistributedIdReq request, StreamObserver<DistributedIdResp> responseObserver) {
        String bizTag = request.getBizTag();
        String token = request.getToken();
        long value = segmentService.getId(bizTag, token);
        DistributedIdResp resp = DistributedIdResp.newBuilder().setValue(value).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
