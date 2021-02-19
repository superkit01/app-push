package com.superkit.app.push.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "huawei.push")
 public  class HuaweiProperties {
	private static final String defaultTokenUrl = "https://login.cloud.huawei.com/oauth2/v2/token";
	private static final String defaultApiUrl = "https://api.push.hicloud.com/pushsend.do";
	
	
	
	private String appId;
	private String appSecret;
	private String packageName;	
	private String tokenUrl = defaultTokenUrl;
	private String apiUrl = defaultApiUrl;
	
}