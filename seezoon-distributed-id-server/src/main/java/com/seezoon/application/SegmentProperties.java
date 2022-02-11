package com.seezoon.application;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "id.server")
@Getter
@Setter
public class SegmentProperties {

    /**
     * 启动后获取一次提前缓存，默认需要第一次获取才会全部缓存
     */
    private boolean initGet;
    /**
     * 启用鉴权
     */
    private boolean enableAuth;

}
