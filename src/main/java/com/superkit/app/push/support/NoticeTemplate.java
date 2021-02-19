package com.superkit.app.push.support;

import lombok.Data;

@Data
public class NoticeTemplate {
	
	private String title;

	private String content;

	private Payload payload;

	public NoticeTemplate(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	

}
