package com.superkit.app.push.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkit.app.push.config.MiProperties;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MiPushHandler implements IPushHandler {

	@Autowired
	private MiProperties miProperties;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void process(NoticeTemplate template, List<Receiver> receivers) {
		try {
			Constants.useOfficial();
			Sender sender = new Sender(miProperties.getAppSecret());
			Message message = new Message.Builder()
					.restrictedPackageName(miProperties.getPackageName())
					.passThrough(0)
					.notifyType(1)
					.title(template.getTitle())
					.description(template.getContent())
					.extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_LAUNCHER_ACTIVITY)
					
					
					.extra("payload",objectMapper.writeValueAsString(template.getPayload())).build();
			for (Receiver receiver : receivers) {
				Result result = sender.send(message, receiver.getRegId(), 3);
				log.info("小米Push响应:{}",objectMapper.writeValueAsString(result));
			}
			
			
		} catch (Exception e) {
			log.info("小米Push异常", e);
		}
	}
}
