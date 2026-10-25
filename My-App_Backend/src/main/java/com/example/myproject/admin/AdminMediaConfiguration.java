package com.example.myproject.admin;

import com.example.myproject.content.MediaStorage;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.*;
import org.springframework.util.unit.DataSize;

@Configuration(proxyBeanMethods=false)
public class AdminMediaConfiguration {
    @Bean @ConditionalOnMissingBean(MultipartConfigElement.class)
    public MultipartConfigElement adminMultipartConfig(@Value("${ADMIN_MEDIA_MAX_BYTES:15728640}") long maxBytes) {
        if(maxBytes<1024||maxBytes>MediaStorage.MAX_BYTES)throw new IllegalArgumentException("Invalid admin media byte limit");
        var factory=new MultipartConfigFactory();factory.setMaxFileSize(DataSize.ofBytes(maxBytes));factory.setMaxRequestSize(DataSize.ofBytes(maxBytes+1024*1024));
        factory.setFileSizeThreshold(DataSize.ofBytes(0));return factory.createMultipartConfig();
    }
}
