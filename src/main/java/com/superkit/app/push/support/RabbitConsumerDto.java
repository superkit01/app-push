package com.superkit.app.push.support;

import java.util.List;

import lombok.Data;

@Data
public class RabbitConsumerDto {
	
	private List<Receiver> receivers;

	private String title;

	private String content;
	
	private Payload payload;


}
