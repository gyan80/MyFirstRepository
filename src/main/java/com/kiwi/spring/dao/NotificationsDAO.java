package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Notifications;

@Repository
public class NotificationsDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public AccessToken byAccessToken(String accToken) {
		
		AccessToken aToken = null;
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM AccessToken WHERE access_token=?");
			query.setString(0, accToken);
			aToken = (AccessToken) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return aToken;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Notifications> getAllNotificationsUsingTimeStamp(
			String user_id, Long timestamp) {

		Query query = null;
		List<Notifications> notifications = null;
		Session session = sessionFactory.openSession();
		try {
			if (timestamp != null) {
				String sql = "FROM Notifications "
						+ "WHERE user_id=:trstdid AND updated_date > :timstmp";
				query = session.createQuery(sql);
				query.setString("trstdid", user_id);
				query.setLong("timstmp", timestamp);
				
				notifications = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return notifications;
	}
}
