package com.seezoon.interfaces.grpc;

import com.seezoon.DistributedIdAo.DistributedIdReq;
import com.seezoon.DistributedIdAo.DistributedIdResp;
import com.seezoon.DistributedIdGrpc.DistributedIdBlockingStub;
import com.seezoon.grpc.annotation.GrpcClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DistributedIdServiceTest {

    @GrpcClient
    private DistributedIdBlockingStub distributedIdBlockingStub;

    @Disabled
    @Test
    void get() {
        DistributedIdReq req = DistributedIdReq.newBuilder().setBizTag("test").build();
        DistributedIdResp distributedIdResp = distributedIdBlockingStub.get(req);
        System.out.println(distributedIdResp.getValue());
    }
}