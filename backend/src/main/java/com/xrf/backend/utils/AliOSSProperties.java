package com.xrf.backend.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliOSSProperties {
    //访问域名
    private String endpoint;
    //访问密钥ID
    private String accessKeyId;
    //访问密钥Secret
    private String accessKeySecret;
    //存储空间名
    private String bucketName;
}