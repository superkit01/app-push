package com.superkit.app.push.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "meizu.push")
public class MeizuProperties {

	private Long appId;
	private String appKey;
	private String appSecret;

}
