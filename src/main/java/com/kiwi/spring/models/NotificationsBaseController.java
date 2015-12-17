package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.NotificationsDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Notifications;

public class NotificationsBaseController {

	@Autowired
	NotificationsDAO notificationsDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;
	
	protected boolean validateAccessToken(String accToken) {

		AccessToken aToken = notificationsDAO.byAccessToken(accToken);
		if (aToken != null)
			return true;
		else
			return false;
	}
	
	protected List<Notifications> getAllNotificationsUsingTimeStamp(
			String user_id, Long timestamp) {

		return notificationsDAO.getAllNotificationsUsingTimeStamp(user_id, timestamp);
	}

}
