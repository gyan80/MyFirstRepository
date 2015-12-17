package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.entity.Box;
import com.kiwi.spring.entity.BoxFolders;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;

@Repository
public class BoxDAO {

	@Autowired
	Configuration env;

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public Box getBoxAccessToken() {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session
					.createQuery("FROM Box");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Box box = (Box) query.uniqueResult();
		session.close();
		return box;
	}
	
	@Transactional
	public User byID(String id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try{
			query = session.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
			query.setString(0, id);
		}catch(Exception e){
			e.printStackTrace();
		}
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}
	
	@Transactional
	public BoxFolders getBoxFoldersData(String userId) {

		Query query = null;
		BoxFolders bf = null;
		Session session = sessionFactory.openSession();
		try {
				query = session.createQuery("FROM BoxFolders as bf WHERE bf.user_id=?");
				query.setString(0, userId);
				bf = (BoxFolders) query.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return bf;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<BoxFolders> getBoxNonEditableFoldersList(String userId) {

		Query query = null;
		List<BoxFolders> bfs = null;
		Session session = sessionFactory.openSession();
		try {
			String sql = "SELECT bf.* FROM box_folders bf "
					+ "WHERE bf.user_id=:userid "
					+ "OR user_id IN (SELECT user_id FROM users WHERE advisor_id=:advsrid) "
					+ "OR user_id IN (SELECT user_id FROM users WHERE advisor_id "
					+ "IN (SELECT user_id FROM users WHERE advisor_id=:advsrsid))";
					
					query = session.createSQLQuery(sql).addEntity(BoxFolders.class);
					query.setString("userid", userId);
					query.setString("advsrid", userId);
					query.setString("advsrsid", userId);
					
				bfs = query.list();

		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return bfs;
	}
}
