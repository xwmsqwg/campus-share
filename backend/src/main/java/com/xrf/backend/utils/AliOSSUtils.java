package com.xrf.backend.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

//阿里云OSS上传工具类
@Component // 1. 交给 Spring 容器管理
public class AliOSSUtils {

    @Autowired
    private AliOSSProperties aliOSSProperties;

    public String upload(MultipartFile file) throws IOException {
        // 获取阿里云OSS的参数值
        String endpoint = aliOSSProperties.getEndpoint();
        String accessKeyId = aliOSSProperties.getAccessKeyId();
        String accessKeySecret = aliOSSProperties.getAccessKeySecret();
        String bucketName = aliOSSProperties.getBucketName();
        // 避免文件覆盖，用UUID和文件后缀名生成唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        try (InputStream inputStream = file.getInputStream()) {
            // 创建 OSSClient
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            try {
                // 上传文件到 OSS
                ossClient.putObject(bucketName, fileName, inputStream);
            } finally {
                // 确保关闭，释放连接资源
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
        // 拼接文件访问路径,例：https://xrf-web-tlias.oss-cn-beijing.aliyuncs.com/0e30907f-0dd8-4bd7-bab8-0d163290d778.jpg
        return endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
    }
}
