package com.seezoon.interfaces.web;

import com.seezoon.DistributedIdGrpc.DistributedIdBlockingStub;
import com.seezoon.application.SegmentService;
import com.seezoon.web.api.Result;
import com.seezoon.web.controller.BaseController;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author seezoon-generator 2022年2月7日 下午3:11:30
 *
 *         通过feign 来调用
 *         {@code
 * @FeignClient(name = "distributed-id-server", url = "${id.server.url}")
 *         public interface DistributedIdClient {
 * @GetMapping("/get/{bizTag}") public Result<Long> get(@PathVariable("bizTag") String bizTag,
 * @RequestParam(required = false, name = "token") String token);
 *         }}
 */
@RestController
@RequiredArgsConstructor
public class DistributedIdController extends BaseController {

    private final SegmentService segmentService;

    @GrpcClient("distributed-id-server")
    private DistributedIdBlockingStub distributedIdBlockingStub;

    @GetMapping("/get/{bizTag}")
    public Result<Long> get(@PathVariable String bizTag, String token) {
        long value = segmentService.getId(bizTag, token);
        return Result.ok(value);
    }
}
