package com.seezoon.interfaces.web;

import com.seezoon.application.SegmentService;
import com.seezoon.web.api.Result;
import com.seezoon.web.controller.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author seezoon-generator 2022年2月7日 下午3:11:30
 */
@RestController
@RequiredArgsConstructor
public class DistributedIdController extends BaseController {

    private final SegmentService segmentService;

    @GetMapping("/get/{bizTag}")
    public Result<Long> query(@PathVariable String bizTag, String token) {
        long value = segmentService.getId(bizTag, token);
        return Result.ok(value);
    }
}
