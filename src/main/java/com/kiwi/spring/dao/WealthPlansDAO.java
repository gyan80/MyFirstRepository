package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.AttachedDocuments;
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlanElements;
import com.kiwi.spring.entity.WealthPlans;

@Repository
public class WealthPlansDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public AttachedDocuments checkDocumentID(String docId, String wealth_plan_id,String planElement_id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM AttachedDocuments WHERE document_id=? AND wealth_plan_id=? AND plan_element_id=? AND is_delete=0");
			query.setString(0, docId);
			query.setString(1, wealth_plan_id);
			query.setString(2, planElement_id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		AttachedDocuments ad = (AttachedDocuments) query.uniqueResult();
		session.close();
		return ad;
	}

	@Transactional
	public WealthPlans checkWPID(String wpId) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM WealthPlans WHERE wealth_plan_id=?");
			query.setString(0, wpId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		WealthPlans wp = (WealthPlans) query.uniqueResult();
		session.close();
		return wp;
	}
	
	@Transactional
	public int unShareWP(String wpId) {
		
		Query query = null;
		String date = String.valueOf(System.currentTimeMillis());
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("UPDATE ShareWealthPlans SET shared_status=0, full_plan=0, updated_date='"
					+ date + "' WHERE wealth_plan_id=?");
			query.setParameter(0, wpId);
			query.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		int l = query.executeUpdate();
		session.close();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int deleteAllWealthPlanElements(String wpId) {
	
		Query query = null;
		Query wpe_q = null;
		Query g_q = null;
		Query mile_q = null;
		Query m_q = null;
		Query todo_q = null;
		Query t_q = null;
		Query n_q = null;
		Query share_wp = null;
		String date = String.valueOf(System.currentTimeMillis());
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("UPDATE WealthPlanElements SET is_delete=1, updated_date='"
							+ date + "' WHERE wealth_plan_id=?");
			query.setParameter(0, wpId);

			share_wp = session.createQuery("UPDATE ShareWealthPlans SET shared_status=0, full_plan=0, updated_date='"
					+ date + "' WHERE wealth_plan_id=?");
			share_wp.setParameter(0, wpId);
			share_wp.executeUpdate();
			
			wpe_q = session
					.createQuery("FROM WealthPlanElements wpe WHERE wpe.wealth_plan_id=?");
			wpe_q.setParameter(0, wpId);
			List<WealthPlanElements> wpes = wpe_q.list();

			for (WealthPlanElements wpe : wpes) {
				String goal_id = wpe.getWealth_plan_element_id();
				g_q = session
						.createQuery("UPDATE WealthPlanElements SET is_delete=1, updated_date='"
								+ date + "' WHERE wealth_plan_element_id=?");
				g_q.setParameter(0, goal_id);
				g_q.executeUpdate();

				mile_q = session
						.createQuery("FROM Milestone WHERE wealth_plan_element_id=?");
				mile_q.setParameter(0, goal_id);
				List<Milestone> ms = mile_q.list();

				for (Milestone m : ms) {
					String m_id = m.getMilestone_id();
					m_q = session
							.createQuery("UPDATE Milestone SET is_delete=1, updated_date='"
									+ date + "' WHERE milestone_id=?");
					m_q.setParameter(0, m_id);
					m_q.executeUpdate();

					todo_q = session
							.createQuery("FROM Todo WHERE milestone_id=?");
					todo_q.setParameter(0, m_id);
					List<Todo> todos = todo_q.list();

					for (Todo todo : todos) {
						String todo_id = todo.getTodo_id();

						t_q = session
								.createQuery("UPDATE Todo SET is_delete=1, updated_date='"
										+ date + "' WHERE todo_id=?");
						t_q.setParameter(0, todo_id);
						t_q.executeUpdate();

						n_q = session
								.createQuery("UPDATE Notifications SET is_delete=1, updated_date='"
										+ date + "' WHERE todo_id=?");
						n_q.setParameter(0, todo_id);
						n_q.executeUpdate();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		int l = query.executeUpdate();
		session.close();
		return l;
	}

	@Transactional
	public ShareWealthPlans getSharedFull(String wpid, String user_id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createSQLQuery("select * FROM share_plans as u WHERE u.shared_to=? AND wealth_plan_id=? limit 0, 1")
					.addEntity(ShareWealthPlans.class);
			query.setString(0, user_id);
			query.setString(1, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<WealthPlans> fetchAllWealthPlans(String advisorid,
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

	@SuppressWarnings("unchecked")
	@Transactional
	public List<AttachedDocuments> getAttachedDocumentUsingTimeStamp(
			String wpId, Long time_stamp) {

		Query query = null;
		List<AttachedDocuments> ads = null;
		Session session = sessionFactory.openSession();
		try {

			if (time_stamp != null) {

				String sql = null;
				sql = "FROM AttachedDocuments WHERE wealth_plan_id=:wpId AND updated_date > :timstmp";
				query = session.createQuery(sql);
				query.setString("wpId", wpId);
				query.setLong("timstmp", time_stamp);
				ads = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return ads;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public int getSharedStatus(String wpid, String trstdid) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			String sql = "SELECT sp.* from wealth_plans w "
					+ " INNER JOIN share_plans sp on w.wealth_plan_id=sp.wealth_plan_id"
					+ " Where sp.shared_to = :trstdid AND w.wealth_plan_id=:wpid AND shared_status=1 group by w.wealth_plan_id";
			
			query = session.createSQLQuery(sql)
			.addEntity(
					ShareWealthPlans.class);
			query.setString("trstdid", trstdid);
			query.setString("wpid", wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<ShareWealthPlans> list = query.list();
		session.close();
		int count = list.size();
		if(count > 0)
			return 1;
		else
			return 0;
	}

	@Transactional
	public User getUserObjectbyId(String userId) {
		
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
		query.setString(0, userId);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<WealthPlans> fetchAllSharedWealthPlans(String trstdid,
			Long timestamp) {

		Query query = null;
		List<WealthPlans> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

				// String sql =
				// "SELECT w.id,w.wealth_plan_id,w.advisor_id,w.client_id,AES_DECRYPT(w.title,'kEyLI1Fy648tzWXGuRcxrg=='),AES_DECRYPT(w.description,'kEyLI1Fy648tzWXGuRcxrg=='),w.status_id,w.is_delete,w.created_by,w.created_date,w.updated_date from wealth_plans w where w.advisor_id=? AND w.updated_date > :timstmp";
				String sql = "SELECT w.* from share_plans s "
						+ "INNER JOIN wealth_plans w on s.wealth_plan_id=w.wealth_plan_id "
						+ "INNER JOIN plan_elements pe on s.plan_element_id=pe.plan_element_id "
						+ "where s.shared_to=:advsr_id AND (w.updated_date > :timstmp1 OR s.updated_date > :timstmp2) group by w.wealth_plan_id, s.shared_to";
				query = session.createSQLQuery(sql)
						.addEntity(WealthPlans.class);
				query.setString("advsr_id", trstdid);
				query.setLong("timstmp1", timestamp);
				query.setLong("timstmp2", timestamp);
				td = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return td;
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

	@Transactional
	public User byAdvisorID(String id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session.createQuery("FROM WealthPlans as u WHERE u.advisor_id=?");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@Transactional
	public void save(WealthPlans wp) {
		Session session = sessionFactory.openSession();
		session.save(wp);
		session.close();
	}

	@Transactional
	public void save(AttachedDocuments ad) {
		Session session = sessionFactory.openSession();
		session.save(ad);
		session.close();
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
	public void update(AttachedDocuments ad) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(ad);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
