package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.PlanElementCopy;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlanElements;

@Repository
public class PlanElementDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public PlanElement checkPlanElementID(String wpid) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM PlanElement as u WHERE u.plan_element_id=?");
			query.setString(0, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		PlanElement pl = (PlanElement) query.uniqueResult();
		session.close();
		return pl;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<PlanElement> fetchAllPlanElements(String advisorid,
			Long timestamp) {

		Query query = null;
		List<PlanElement> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

//				String sql = "SELECT p.id,p.plan_element_id,p.advisor_id, AES_DECRYPT(p.title,'kEyLI1Fy648tzWXGuRcxrg=='),p.is_delete,p.status_id,p.created_date,p.updated_date from plan_elements p where p.advisor_id=? AND p.updated_date > :timstmp";
				String sql = "SELECT p.* from plan_elements p "
						+ "where (p.advisor_id=:advsr_id OR p.advisor_id='0') AND p.updated_date > :timstmp";
				query = session.createSQLQuery(sql).addEntity(PlanElement.class);
				query.setString("advsr_id", advisorid);
				query.setLong("timstmp", timestamp);
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
	public List<PlanElementCopy> fetchAllSharedPlanElements(String trstdid,
			Long timestamp, Long status) {

		Query query = null;
		List<PlanElementCopy> pec = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

//				String sql = "SELECT p.id,p.plan_element_id,p.advisor_id, AES_DECRYPT(p.title,'kEyLI1Fy648tzWXGuRcxrg=='),p.is_delete,p.status_id,p.created_date,p.updated_date from plan_elements p where p.advisor_id=? AND p.updated_date > :timstmp";
				String sql = "SELECT sp.shared_status, pe.* FROM plan_elements pe"
						+ " INNER JOIN share_plans sp on pe.plan_element_id=sp.plan_element_id "
						+ " INNER JOIN wealth_plan_elements wpe on pe.plan_element_id=wpe.plan_element_id and wpe.wealth_plan_id=sp.wealth_plan_id "
						+ " WHERE sp.shared_to=:trstdid AND (sp.updated_date > :timstmp1 OR pe.updated_date > :timstmp2) AND sp.shared_status=:status";

				query = session.createSQLQuery(sql).addEntity(PlanElementCopy.class);
				query.setString("trstdid", trstdid);
				query.setLong("timstmp1", timestamp);
				query.setLong("timstmp2", timestamp);
				query.setLong("status", status);
				pec = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return pec;
	}
	
	@Transactional
	public ShareWealthPlans getSharedFull(String peid, String trstd_id) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createSQLQuery("select * FROM share_plans as u "
					+ "WHERE u.shared_to=? AND plan_element_id=? limit 0, 1")
			.addEntity(
					ShareWealthPlans.class);
			query.setString(0, trstd_id);
			query.setString(1, peid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
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
	public PlanElement byAdvisorID(String id) {
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session
					.createQuery("FROM PlanElement as u WHERE u.advisor_id=?");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PlanElement pl = (PlanElement) query.uniqueResult();
		session.close();
		return pl;
	}
	
	@Transactional
	public User byID(String id) {
		Session session = sessionFactory.openSession();

		Query query = session
				.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
		query.setString(0, id);

		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@Transactional
	public Long save(PlanElement wp) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(wp);
		session.close();
		return l;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int deleteAllElements(String peId) {
		
		Query p_query = null;
		Query query = null;
		Query mile_q = null;
		Query m_q = null;
		Query todo_q = null;
		Query t_q = null;
		Query n_q = null;
		Query sp_q = null;
		String date = String.valueOf(System.currentTimeMillis());
		Session session = sessionFactory.openSession();
		try {
			p_query = session.createQuery("FROM WealthPlanElements WHERE plan_element_id=?");
			p_query.setParameter(0, peId);
			List<WealthPlanElements> wpes = p_query.list();
			
			for (WealthPlanElements wpe : wpes) {
				
				sp_q = session.createQuery("UPDATE ShareWealthPlans SET shared_status=0, full_plan=0, updated_date='"
						+ date + "' WHERE wealth_plan_id=? AND plan_element_id=?");
					sp_q.setParameter(0, wpe.getWealth_plan_id());
					sp_q.setParameter(1, peId);
					sp_q.executeUpdate();
		
				query = session.createQuery("UPDATE WealthPlanElements SET is_delete=1, updated_date='"
								+ date + "' WHERE wealth_plan_id=? AND plan_element_id=?");
				query.setParameter(0, wpe.getWealth_plan_id());
				query.setParameter(1, peId);
				query.executeUpdate();
				
					mile_q = session
							.createQuery("FROM Milestone WHERE wealth_plan_element_id=?");
					mile_q.setParameter(0, wpe.getWealth_plan_element_id());
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
	public void update(PlanElement wp) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(wp);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
