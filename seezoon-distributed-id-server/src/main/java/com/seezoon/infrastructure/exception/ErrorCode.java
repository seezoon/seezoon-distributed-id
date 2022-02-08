package com.seezoon.infrastructure.exception;

public class ErrorCode {

    /**
     * key不存在时的异常码
     */
    public static final String BIZ_TAG_NOT_EXISTS = "ID_SERVER_BIZ_TAG_NOT_EXISTS";
    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    public static final String SEGMENT_NOT_READY = "ID_SERVER_SEGMENT_NOT_READY";

    /**
     * 鉴权不通过
     */
    public static final String AUTH_FAILED = "ID_SERVER_AUTH_FAILED";
}
