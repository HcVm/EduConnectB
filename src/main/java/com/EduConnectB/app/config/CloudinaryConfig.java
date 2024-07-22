package com.EduConnectB.app.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dpdfnhsmr",
                "api_key", "964397669458499",
                "api_secret", "INFuBokKFtPDIpNCV1Nsdm9X_sA"));
    }
}