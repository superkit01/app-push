package com.superkit.app.push.handler;

import java.util.List;

import com.superkit.app.push.support.NoticeTemplate;
import com.superkit.app.push.support.Receiver;

public interface IPushHandler {

	void process(NoticeTemplate dto, List<Receiver> receivers) throws Exception;

}
