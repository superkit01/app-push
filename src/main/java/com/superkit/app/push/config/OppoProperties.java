package com.superkit.app.push.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oppo.push")
public class OppoProperties {

	private String appId;
	private String appKey;
	private String appSecret;
	private String masterSecret;

}
