package com.superkit.app.push.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.StartActivityTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.superkit.app.push.config.GeTuiProperties;
import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IosPushHandler implements IPushHandler {
	@Autowired
	private GeTuiProperties geTuiProperties;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void process(NoticeTemplate template,  List<Receiver> receivers) {
		try {
			IGtPush push = new IGtPush(geTuiProperties.getAppKey(), geTuiProperties.getMasterSecret(), true);

			StartActivityTemplate startActivityTemplate = new StartActivityTemplate();
			startActivityTemplate.setAppId(geTuiProperties.getAppId());
			startActivityTemplate.setAppkey(geTuiProperties.getAppKey());
			Style0 style = new Style0();
			style.setTitle(template.getTitle());
			style.setText(template.getContent());
			startActivityTemplate.setStyle(style);

			SingleMessage message = new SingleMessage();
			message.setData(startActivityTemplate);

			for (Receiver receiver : receivers) {
				Target target = new Target();
				target.setAppId(geTuiProperties.getAppId());
				target.setClientId(receiver.getRegId());
				IPushResult ret = push.pushMessageToSingle(message, target);
				log.info("个推IOS Push响应:{}",  objectMapper.writeValueAsString(ret));
			}

		} catch (Exception e) {
			log.info("个推IOS Push异常", e);
		}
	}

}
