package com.seezoon.infrastructure.exception;


import com.seezoon.core.concept.infrastructure.exception.BaseException;
import com.seezoon.grpc.support.GrpcExceptionTranslator;
import com.seezoon.web.api.Result;
import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GrpcExceptionAdvice {

    @ExceptionHandler({StatusRuntimeException.class})
    public Result statusRuntimeException(StatusRuntimeException e) {
        BaseException baseException = GrpcExceptionTranslator.translate(e);
        return Result.error(baseException.getCode(), baseException.getMessage());
    }
}
