package com.seezoon.interfaces.grpc;

import com.seezoon.DistributedIdAo.DistributedIdReq;
import com.seezoon.DistributedIdAo.DistributedIdResp;
import com.seezoon.DistributedIdGrpc.DistributedIdBlockingStub;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.StubTransformer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * 设置超时时间需要添加{@link StubTransformer}
 * stub.withDeadline()
 */
@SpringBootTest
class DistributedIdServiceTest {

    @GrpcClient("distributed-id-server")
    private DistributedIdBlockingStub distributedIdBlockingStub;

    @Disabled
    @Test
    void get() {
        DistributedIdReq req = DistributedIdReq.newBuilder().setBizTag("test").build();
        DistributedIdResp distributedIdResp = this.distributedIdBlockingStub.get(req);
        System.out.println(distributedIdResp.getValue());
    }
}