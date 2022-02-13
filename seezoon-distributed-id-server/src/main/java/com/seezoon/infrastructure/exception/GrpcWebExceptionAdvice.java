package com.seezoon.infrastructure.exception;


import com.seezoon.core.concept.infrastructure.exception.BaseException;
import com.seezoon.grpc.support.GrpcExceptionTranslator;
import com.seezoon.web.api.Result;
import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * grpc rpc 远程调用时候无法通过grpc client拦截器完成统一异常处理
 */
@RestControllerAdvice
public class GrpcWebExceptionAdvice {

    @ExceptionHandler({StatusRuntimeException.class})
    public Result statusRuntimeException(StatusRuntimeException e) {
        BaseException baseException = GrpcExceptionTranslator.translate(e);
        return Result.error(baseException.getCode(), baseException.getMessage());
    }
}
