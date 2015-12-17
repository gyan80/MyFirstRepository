package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.User;

@Repository
public class AccessTokenDAO {

	@Autowired
	Configuration env;

	@Autowired
	SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Transactional
	public List<AccessToken> byID(String id) {
		Session session = sessionFactory.openSession();

		Query query = session
				.createQuery("FROM AccessToken as u WHERE u.user_id=?");
		query.setString(0, id);

		List<AccessToken> at = (List<AccessToken>) query.list();
		session.close();
		return at;
	}

	@Transactional
	public AccessToken byUserID(String id) {
		Query query = null;
		try {
			Session session = sessionFactory.openSession();
			query = session
					.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
			query.setString(0, id);

		} catch (Exception e) {
			System.out.println(e);
		}
		AccessToken at = (AccessToken) query.uniqueResult();
		sessionFactory.close();
		return at;
	}

	@Transactional
	public AccessToken byAccessToken(String accToken) {
		AccessToken aToken = null;
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			
			query = session
					.createQuery("FROM AccessToken WHERE access_token=?");
			query.setString(0, accToken);
			aToken = (AccessToken) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return aToken;
	}

	@Transactional
	public Long save(AccessToken accessToken) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(accessToken);
		session.close();
		return l;
	}

	@Transactional
	public void update(AccessToken accessToken) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(accessToken);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public boolean delete(User usr) {
		Session session = sessionFactory.openSession();

		try {
			session.delete(usr);
			session.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
