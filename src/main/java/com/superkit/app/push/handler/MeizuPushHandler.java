package com.superkit.app.push.handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meizu.push.sdk.server.IFlymePush;
import com.meizu.push.sdk.server.constant.ErrorCode;
import com.meizu.push.sdk.server.constant.PushResponseCode;
import com.meizu.push.sdk.server.constant.ResultPack;
import com.meizu.push.sdk.server.model.push.PushResult;
import com.meizu.push.sdk.server.model.push.VarnishedMessage;
import com.superkit.app.push.config.MeizuProperties;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MeizuPushHandler implements IPushHandler {

	@Autowired
	private MeizuProperties meizuProperties;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void process(NoticeTemplate template,  List<Receiver> receivers) {
		try {
			JSONObject json = new JSONObject();
			json.put("action", template.getPayload());

			IFlymePush push = new IFlymePush(meizuProperties.getAppSecret());

			VarnishedMessage message = new VarnishedMessage.Builder()
					.appId(meizuProperties.getAppId())
					.title(template.getTitle())
					.content(template.getContent())
					.parameters(json).build();
			
			ResultPack<PushResult> result = push.pushMessage(message, receivers.stream().map(Receiver::getRegId).collect(Collectors.toList()));

			if (result.isSucceed()) {
				PushResult pushResult = result.value();
				Map<String, List<String>> targetResultMap = pushResult.getRespTarget();
				if (targetResultMap != null && !targetResultMap.isEmpty()) {
					// 限速重试
					if (targetResultMap.containsKey(PushResponseCode.RSP_SPEED_LIMIT.getValue())) {
						List<String> rateLimitTarget = targetResultMap.get(PushResponseCode.RSP_SPEED_LIMIT.getValue());
						ResultPack<PushResult> result2 = push.pushMessage(message, rateLimitTarget);
						log.info("魅族Push响应:{}", objectMapper.writeValueAsString(result2));
					}
				}
			} else {
				// 超速重试
				if (String.valueOf(ErrorCode.APP_REQUEST_EXCEED_LIMIT.getValue()).equals(result.code())) {
					ResultPack<PushResult> result2 = push.pushMessage(message, receivers.stream().map(Receiver::getRegId).collect(Collectors.toList()));
					log.info("魅族Push响应:{}", objectMapper.writeValueAsString(result2));
				}
			}
			log.info("魅族Push响应:{}", objectMapper.writeValueAsString(result));
		} catch (Exception e) {
			log.error("魅族Push异常", e);

		}
	}

}
