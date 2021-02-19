package com.superkit.app.push.consumer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkit.app.push.handler.HuaweiPushHandler;
import com.superkit.app.push.handler.IosPushHandler;
import com.superkit.app.push.handler.MeizuPushHandler;
import com.superkit.app.push.handler.MiPushHandler;
import com.superkit.app.push.handler.OppoPushHandler;
import com.superkit.app.push.handler.OtherAndroidPushHandler;
import com.superkit.app.push.handler.VivoPushHandler;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.PlatformType;
import com.superkit.app.push.support.RabbitConsumerDto;
import com.superkit.app.push.support.Receiver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RiskNoticeConsumer {
	private static final String APP_QUEUE = "APP_QUEUE";

	@Autowired
	private HuaweiPushHandler huaweiPushHandler;
	@Autowired
	private MeizuPushHandler meizuPushHandler;
	@Autowired
	private MiPushHandler miPushHandler;
	@Autowired
	private OppoPushHandler oppoPushHandler;
	@Autowired
	private VivoPushHandler vivoPushHandler;
	@Autowired
	private OtherAndroidPushHandler otherAndroidPushHandler;
	@Autowired
	private IosPushHandler iosPushHandler;
	@Autowired
	private ObjectMapper objectMapper;

	@RabbitListener(queues = RiskNoticeConsumer.APP_QUEUE)
	public void consumerRisk(String msg) {
		log.debug("MQ消费：{}", msg);
		try {
			RabbitConsumerDto dto = objectMapper.readValue(msg, RabbitConsumerDto.class);

			NoticeTemplate template = new NoticeTemplate(dto.getTitle(), dto.getContent());
			template.setPayload(dto.getPayload());

			Map<Integer, List<Receiver>> map = dto.getReceivers().stream()
					.collect(Collectors.groupingBy(Receiver::getPlatformType));

			for (Entry<Integer, List<Receiver>> entry : map.entrySet()) {
				Integer platfrom = entry.getKey();
				
				switch (platfrom) {
				case PlatformType.OTHER_ANDROID:
					otherAndroidPushHandler.process(template, entry.getValue());
					break;
				case PlatformType.IOS:
					iosPushHandler.process(template, entry.getValue());
					break;
				case PlatformType.HUAWEI:
					huaweiPushHandler.process(template, entry.getValue());
					break;
				case PlatformType.OPPO:
					oppoPushHandler.process(template, entry.getValue());
					break;
				case PlatformType.VIVO:
					vivoPushHandler.process(template, entry.getValue());
					break;
				case PlatformType.MEIZU:
					meizuPushHandler.process(template, entry.getValue());
					break;
				case PlatformType.XIAOMI:
					miPushHandler.process(template, entry.getValue());
					break;
				default:
					log.error("未知手机类型：{}", platfrom);
					break;
				}
			}
		} catch (Exception e) {
			log.error("消费发生异常", e);
		}
	}

}
