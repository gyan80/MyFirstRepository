package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;

@Repository
public class AttachedDocumentDAO {

	@Autowired
	SessionFactory sessionFactory;
	
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
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<WealthPlans> getAllDataUsingTimeStamp(String advisorid,
			String cl_id, Long timestamp) {

		Query query = null;
		List<WealthPlans> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

				String sql = null;
				// "SELECT w.id,w.wealth_plan_id,w.advisor_id,w.client_id,AES_DECRYPT(w.title,'kEyLI1Fy648tzWXGuRcxrg=='),AES_DECRYPT(w.description,'kEyLI1Fy648tzWXGuRcxrg=='),w.status_id,w.is_delete,w.created_by,w.created_date,w.updated_date from wealth_plans w where w.advisor_id=? AND w.updated_date > :timstmp";
				if (cl_id != null) {
					sql = "SELECT w.* from wealth_plans w "
							+ "where w.advisor_id=:advsr_id AND w.client_id=:cl_id AND w.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							WealthPlans.class);
					query.setString("advsr_id", advisorid);
					query.setString("cl_id", cl_id);
					query.setLong("timstmp", timestamp);
				} else {
					sql = "SELECT w.* from wealth_plans w "
							+ "where w.advisor_id=:advsr_id AND w.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							WealthPlans.class);
					query.setString("advsr_id", advisorid);
					query.setLong("timstmp", timestamp);
				}

				td = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return td;
	}
	
	@Transactional
	public User getUserObjectbyId(String userId) {
		Session session = sessionFactory.openSession();

		Query query = session
				.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
		query.setString(0, userId);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}
	
	@Transactional
	public WealthPlans checkWealthPlanID(String wpid) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session
					.createQuery("FROM WealthPlans as u WHERE u.wealth_plan_id=?");
			query.setString(0, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		WealthPlans wp = (WealthPlans) query.uniqueResult();
		session.close();
		return wp;
	}
	
	@Transactional
	public void update(WealthPlans wp) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(wp);
			session.close(); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public void save(WealthPlans wp) {
		Session session = sessionFactory.openSession();
		session.save(wp);
		session.close();
	}
	
	@Transactional
	public User checkId(String id) {
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session
					.createQuery("FROM User u WHERE u.user_id=? AND is_delete=0");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}
}
