package com.kiwi.spring.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.Box;
import com.kiwi.spring.entity.BoxFolders;

@Repository
public class BoxFoldersDAO {

	@Autowired
	Configuration env;

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public Box getBoxAccessToken() {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {

			query = session.createQuery("FROM Box");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Box box = (Box) query.uniqueResult();
		session.close();
		return box;
	}

	@Transactional
	public Long save(BoxFolders box_user) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(box_user);
		session.close();
		return l;
	}

	@Transactional
	public BoxFolders checkUser_id(String user_id) {
		Session session = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();
			query = session.createQuery("FROM BoxFolders WHERE user_id=?");
			query.setString(0, user_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BoxFolders bf = (BoxFolders) query.uniqueResult();
		session.close();
		return bf;
	}
}
