package com.superkit.app.push.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkit.app.push.config.VivoProperties;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;
import com.vivo.push.sdk.notofication.Message;
import com.vivo.push.sdk.notofication.Result;
import com.vivo.push.sdk.server.Sender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VivoPushHandler implements IPushHandler {
	@Autowired
	private VivoProperties vivoProperties;

	@Autowired
	private ObjectMapper objectMapper;

	private String token;

	public void process(NoticeTemplate template, List<Receiver> receivers) throws Exception {
		try {
			/**
			 * 拓展字段
			 */
			Map<String, String> map = new HashMap<>();
			map.put("payload", objectMapper.writeValueAsString(template.getPayload()));

			if (StringUtils.isEmpty(token)) {
				refreshToken();
			}
			Sender sender = new Sender(vivoProperties.getAppSecret(), token);

			for (Receiver receiver : receivers) {
				Message singleMessage = new Message.Builder().regId(receiver.getRegId()).notifyType(2)
						.title(template.getTitle()).content(template.getContent()).skipType(1)
						.timeToLive(60 * 60 * 24).clientCustomMap(map).requestId(UUID.randomUUID().toString()).build();
				Result result = sender.sendSingle(singleMessage);
				if (result.getResult() == 10000) {
					refreshToken();
					result = sender.sendSingle(singleMessage);
				}
				log.info("VIVO Push响应:{}", objectMapper.writeValueAsString(result));
			}

		} catch (Exception e) {
			log.info("VIVO Push异常", e);
		}
	}

	private void refreshToken() throws Exception {
		token = new Sender(vivoProperties.getAppSecret())
				.getToken(vivoProperties.getAppId(), vivoProperties.getAppKey()).getAuthToken();
	}

}
