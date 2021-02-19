package com.superkit.app.push.handler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oppo.push.server.Notification;
import com.oppo.push.server.Result;
import com.oppo.push.server.Sender;
import com.oppo.push.server.Target;
import com.superkit.app.push.config.OppoProperties;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OppoPushHandler implements IPushHandler {

	@Autowired
	private OppoProperties oppoProperties;

	private Sender sender;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void process(NoticeTemplate template,  List<Receiver> receivers) {
		try {
			/**
			 * 拓展字段
			 */
			JSONObject json = new JSONObject();
			json.put("action", template.getPayload());

			if (Objects.isNull(sender)) {
				sender = new Sender(oppoProperties.getAppKey(), oppoProperties.getMasterSecret());
			}

			Notification notification = new Notification();
			notification.setTitle(template.getTitle());
			notification.setContent(template.getContent());
			
			notification.setActionParameters(json.toJSONString());
			Result payLoad = sender.saveNotification(notification);
			log.info("OPPO 保存通知栏消息响应:{}", objectMapper.writeValueAsString(payLoad));

			Result result = sender.broadcastNotification(payLoad.getMessageId(),
					Target.build(String.join(";", receivers.stream().map(Receiver::getRegId).collect(Collectors.toList()))));
			log.info("OPPO Push响应:{}", objectMapper.writeValueAsString(result));
		} catch (Exception e) {
			log.error("OPPO Push异常", e);
		}

	}

}
