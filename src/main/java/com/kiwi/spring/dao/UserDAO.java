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
import com.kiwi.spring.entity.BoxFolders;
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.UserCopy;
import com.kiwi.spring.entity.WealthPlanElements;
import com.kiwi.spring.entity.WealthPlans;

@Repository
public class UserDAO {

	@Autowired
	Configuration env;

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public User byID(String id) {
		Session session = sessionFactory.openSession();

		Query query = session.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
		query.setString(0, id);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}
	
	@Transactional
	public User byTrustedID(String id) {
		
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.user_id=? AND role_id=5 AND is_delete=0");
		query.setString(0, id);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> fetchAllTrusted(String id) {

		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.advisor_id=? AND role_id=5 AND is_delete=0");
		query.setString(0, id);
		List<User> l = (List<User>) query.list();
		session.close();
		return l;
	}
	
	@Transactional
	public User byUserAndAdivsorID(String id, String aid) {
		
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.user_id=? AND u.advisor_id=? AND is_delete=0");
		query.setString(0, id);
		query.setString(1, aid);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> byUserAndAdivsorIDs(String clientid, Long timestamp) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			if (timestamp != null) {

				query = session
						.createQuery("FROM User as u WHERE u.advisor_id=? AND updated_date > "
								+ timestamp);
				query.setString(0, clientid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<User> l = (List<User>) query.list();
		session.close();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<UserCopy> fetchAllWealthPlans(String advisorid, String cl_id,
			Long timestamp) {

		Query query = null;
		List<UserCopy> user = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

				String sql = null;
				if (cl_id != null) {
					sql = "select t.id, t.advisor_id, t.user_id, t.status_id, t.is_delete, t.profile_picture, AES_DECRYPT(t.first_name,'kEyLI1Fy648tzWXGuRcxrg==') first_name, AES_DECRYPT(t.title,'kEyLI1Fy648tzWXGuRcxrg==') title"
							+ ", AES_DECRYPT(t.email,'kEyLI1Fy648tzWXGuRcxrg==') email, AES_DECRYPT(t.middle_name,'kEyLI1Fy648tzWXGuRcxrg==') middle_name"
							+ ", AES_DECRYPT(t.dob,'kEyLI1Fy648tzWXGuRcxrg==') dob, AES_DECRYPT(t.last_name,'kEyLI1Fy648tzWXGuRcxrg==') last_name"
							+ ", AES_DECRYPT(t.relation_to_self,'kEyLI1Fy648tzWXGuRcxrg==') relation_to_self from users t "
							+ "INNER JOIN users c on t.advisor_id=c.user_id "
							+ "where c.user_id=:cl_id AND t.role_id=5";
					query = session.createSQLQuery(sql).addEntity(
							UserCopy.class);
					query.setString("cl_id", cl_id);
				} else {

					sql = "select t.id, t.advisor_id, t.user_id, t.status_id, t.is_delete, t.profile_picture, AES_DECRYPT(t.first_name,'kEyLI1Fy648tzWXGuRcxrg==') first_name, AES_DECRYPT(t.title,'kEyLI1Fy648tzWXGuRcxrg==') title"
							+ ", AES_DECRYPT(t.email,'kEyLI1Fy648tzWXGuRcxrg==') email, AES_DECRYPT(t.middle_name,'kEyLI1Fy648tzWXGuRcxrg==') middle_name"
							+ ", AES_DECRYPT(t.dob,'kEyLI1Fy648tzWXGuRcxrg==') dob, AES_DECRYPT(t.last_name,'kEyLI1Fy648tzWXGuRcxrg==') last_name"
							+ ", AES_DECRYPT(t.relation_to_self,'kEyLI1Fy648tzWXGuRcxrg==') relation_to_self from users t "
							+ "INNER JOIN users c on t.advisor_id=c.user_id "
							+ "INNER JOIN users a on c.advisor_id=a.user_id "
							+ "where a.user_id=:advsr_id "
							+ "AND t.role_id=5";
					query = session.createSQLQuery(sql).addEntity(
							UserCopy.class);
					query.setString("advsr_id", advisorid);
				}

				user = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return user;
	}

	@Transactional
	public User byAdvisorID(String id) {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
		query.setString(0, id);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@Transactional
	public User byUserID(String id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM User as u WHERE u.user_id=? AND u.is_delete=0");
			query.setString(0, id);

		} catch (Exception e) {
			System.out.println(e);
		}
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}
	
	@Transactional
	public int byTrustedId(String id) {

		Query query = null;
		Query wp_q  = null;
		Query g_q  = null;
		Query m_q  = null;
		String date = String.valueOf(System.currentTimeMillis());
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("UPDATE User u SET u.is_delete=1, updated_date='"+date+"' WHERE u.advisor_id=? AND u.role_id=5");
			query.setParameter(0, id);
			
			query = session.createQuery("FROM WealthPlans w WHERE w.client_id=?");
			query.setParameter(0, id);
			
			@SuppressWarnings("unchecked")
			List<WealthPlans> wps = query.list();
			for (WealthPlans wp : wps) {
				String wp_id = wp.getWealth_plan_id();
				wp_q = session.createQuery("UPDATE WealthPlans w SET w.is_delete=1, updated_date='"+date+"' WHERE w.client_id=?");
				wp_q.setParameter(0, id);
				wp_q.executeUpdate();
				
				query = session.createQuery("FROM WealthPlanElements wpe WHERE wpe.wealth_plan_id=?");
				query.setParameter(0, wp_id);
				@SuppressWarnings("unchecked")
				List<WealthPlanElements> wpes = query.list();
				for (WealthPlanElements wpe : wpes) {
					String goal_id = wpe.getWealth_plan_element_id();
					g_q = session.createQuery("UPDATE WealthPlanElements SET is_delete=1, updated_date='"+date+"' WHERE wealth_plan_element_id=?");
					g_q.setParameter(0, goal_id);
					g_q.executeUpdate();
					
					query = session.createQuery("FROM Milestone WHERE wealth_plan_element_id=?");
					query.setParameter(0, goal_id);
					@SuppressWarnings("unchecked")
					List<Milestone> ms = query.list();
					for (Milestone m : ms) {
						String m_id = m.getMilestone_id();
						m_q = session.createQuery("UPDATE Milestone SET is_delete=1, updated_date='"+date+"' WHERE milestone_id=?");
						m_q.setParameter(0, m_id);
						m_q.executeUpdate();
						
						m_q = session.createQuery("UPDATE Todo SET is_delete=1, updated_date='"+date+"' WHERE milestone_id=?");
						m_q.setParameter(0, m_id);
						m_q.executeUpdate();
					}
					
				}
				
			}
			
			
			query = session.createQuery("UPDATE User u SET u.is_delete=1, updated_date='"+date+"' WHERE u.advisor_id=? AND u.role_id=5");
			query.setParameter(0, id);
		} catch (Exception e) {
			System.out.println(e);
		}
		int l = query.executeUpdate();
		session.close();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> fetchAllClients(String advsr_id, Long timestamp) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {
					query = session.createQuery("FROM User as u WHERE u.advisor_id=? AND u.advisor_id<>'0' AND u.role_id=4 AND u.updated_date > " + timestamp);
					query.setString(0, advsr_id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<User> l = (List<User>) query.list();
		session.close();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> fetchAllClientsWithoutTimestamp(String advsr_id) {

		Session session = sessionFactory.openSession();
		Query query = null;
		try{
			query = session.createQuery("FROM User as u WHERE u.advisor_id=? AND u.role_id=4  AND is_delete=0");
			query.setString(0, advsr_id);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		List<User> l = (List<User>) query.list();
		session.close();
		return l;
	}
	
	@Transactional
	public BoxFolders getSharedIdByUserId(String userId) {

		Session session = sessionFactory.openSession();
		Query query = null;
		try{
			query = session.createQuery("FROM BoxFolders WHERE user_id=?");
			query.setString(0, userId);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		BoxFolders bf = (BoxFolders) query.uniqueResult(); 
		session.close();
		return bf;
	}

	@Transactional
	public User byForgotToken(String username, String token) {
		
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.username=? AND forgot_token=? AND is_delete=0");
		query.setString(0, username);
		query.setString(1, token);
		User user = (User) query.uniqueResult();
		session.close();
		return user;
	}

	@Transactional
	public int updatePassword(String userid, String password) {

		Session session = sessionFactory.openSession();
		String sql = "UPDATE User SET password=:pass where user_id=:usr";
		Query qry = session.createQuery(sql);
		qry.setParameter("pass", password);
		qry.setParameter("usr", userid);
		int result = qry.executeUpdate();
		session.close();
		return result;
	}

	@Transactional
	public User byUsername(String username) {

		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.username=? AND is_delete=0");
		query.setString(0, username);
		User user = (User) query.uniqueResult();
		session.close();
		return user;
	}

	@Transactional
	public User byEmail(String email) {
		
		Session session = sessionFactory.openSession();
		User user = null;
		try {
			Query query = session.createQuery("FROM User as u WHERE u.email=? AND is_delete=0");
			query.setString(0, email);
			user = (User) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return user;
	}
	
	@Transactional
	public int getUpdateWealthPlans(String usrId, String timestamp) {
		
		Session session = sessionFactory.openSession();
		int result = 0;
		try {
			Query query = session
					.createQuery("UPDATE WealthPlans SET updated_date='" + timestamp + "' WHERE client_id=?");
			query.setString(0, usrId);
			result = query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return result;
	}

	@Transactional
	public User byEmailOrUsername(String email, String username) {
		
		Session session = sessionFactory.openSession();
		String sql = "SELECT * FROM users WHERE email=:email and username=:username AND is_delete=0";
		User usr = (User) session.createSQLQuery(sql).addEntity(User.class)
		.setString("email", email).setString("username", username)
		.uniqueResult();
		session.close();
		return usr;
	}

	@Transactional
	public User byLoginCredentials(String username, String password) {
		
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.username=? AND u.password=? AND is_delete=0");
		query.setString(0, username);
		query.setString(1, password);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@Transactional
	public Long save(User user) {
		
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(user);
		session.close();
		return l;
	}

	@Transactional
	public void update(User user) {
		
		try {
			Session session = sessionFactory.openSession();
			session.update(user);
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

	@Transactional
	public boolean delete(List<AccessToken> usr) {
		
		Session session = sessionFactory.openSession();
		try {
			for (AccessToken at : usr) {
				session.delete(at);
			}
			session.close();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
    @Transactional
    public List<User> getUserWithsame(String advisor_id, String first_name, String last_name) {
     
		List<User> users = null;
		Session session = sessionFactory.openSession();
        Query query = session.createQuery("FROM User as u WHERE u.advisor_id=? and first_name=? and last_name=? AND is_delete=0");
        query.setString(0, advisor_id);
        query.setString(1, first_name);
        query.setString(2, last_name);
        users = query.list();
        session.close();
        return users;
    }
	
}