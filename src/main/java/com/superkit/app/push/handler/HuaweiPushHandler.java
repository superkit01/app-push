package com.superkit.app.push.handler;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.push.javasdk.message.AndroidConfig;
import com.huawei.push.javasdk.message.AndroidNotification;
import com.huawei.push.javasdk.message.ClickAction;
import com.huawei.push.javasdk.message.Message;
import com.huawei.push.javasdk.message.Notification;
import com.huawei.push.javasdk.messaging.HuaweiApp;
import com.huawei.push.javasdk.messaging.HuaweiCredential;
import com.huawei.push.javasdk.messaging.HuaweiMessaging;
import com.huawei.push.javasdk.messaging.HuaweiOption;
import com.huawei.push.javasdk.messaging.SendResponce;
import com.superkit.app.push.config.HuaweiProperties;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HuaweiPushHandler implements IPushHandler {
	@Autowired
	private HuaweiProperties huaweiProperties;

	private volatile HuaweiApp app;

	private AtomicInteger atomicInteger = new AtomicInteger(0);
	
	@Autowired
	private ObjectMapper mapper;

	@Override
	public void process(NoticeTemplate template, List<Receiver> receivers) {
		try {
			if (Objects.isNull(app)) {
				initializeApp();
			}
			
			Notification notification = new Notification(template.getTitle(), template.getContent());
			
			ClickAction clickAction = ClickAction.builder().setType(3).build();
			
			AndroidNotification androidNotification = AndroidNotification.builder().setClickAction(clickAction)
					.setStyle(0).setAutoClear(86400000).setNotifyId(atomicInteger.incrementAndGet()).build();
			
			AndroidConfig androidConfig = AndroidConfig.builder().setCollapseKey(-1).setTtl("1448s").setBiTag("Trump")
					.setNotification(androidNotification).build();
			
			for (Receiver receiver : receivers) {
				Message message = Message.builder().setNotification(notification).setAndroidConfig(androidConfig)
						.addToken(receiver.getRegId()).build();
				SendResponce response = HuaweiMessaging.getInstance(app).send(message);
				log.info("华为推送响应:{}", mapper.writeValueAsString(response));
			}
			
		} catch (Exception e) {
			log.error("华为推送异常", e);
		}
	}

	public void initializeApp() {
		HuaweiCredential credential = HuaweiCredential.builder().setAppId(huaweiProperties.getAppId())
				.setAppSecret(huaweiProperties.getAppSecret()).build();
		HuaweiOption option = HuaweiOption.builder().setCredential(credential).build();
		app = HuaweiApp.initializeApp(option);
	}

}
