package com.superkit.app.push.support;

import lombok.Data;

@Data
public class HuaweiTokenDTO  {
	private String access_token;
	private long expires_in;
	private String scope;
	private Integer error;
	private String error_description;
	private long expiredTime;
}
