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
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlanElements;

@Repository
public class ShareWealthPlansDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public ShareWealthPlans checkWP_PE_TAIDValidation(String shareTo,
			String wpid, String peid) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session
					.createQuery("FROM ShareWealthPlans as u WHERE u.shared_to=? AND wealth_plan_id=? AND plan_element_id=?");
			query.setString(0, shareTo);
			query.setString(1, wpid);
			query.setString(2, peid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
	}

	@Transactional
	public User checkIdValidation(String userId) {
		Session session = sessionFactory.openSession();

		Query query = session
				.createQuery("FROM User as u WHERE u.user_id=? AND is_delete=0");
		query.setString(0, userId);
		User usr = (User) query.uniqueResult();
		session.close();
		return usr;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<ShareWealthPlans> checkWP_TAID(String shareTo, String wpid) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session
					.createQuery("FROM ShareWealthPlans as u WHERE u.shared_to=? AND wealth_plan_id=?");
			query.setString(0, shareTo);
			query.setString(1, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<ShareWealthPlans> lswp = (List<ShareWealthPlans>) query.list();
		session.close();
		return lswp;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<ShareWealthPlans> fetchAllSharedWealthPlans(String advisorId,
			String client_id, Long timestamp) {

		Query query = null;
		List<ShareWealthPlans> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {
				String sql = null;
				if (client_id != null) {
					sql = "SELECT * from share_plans "
							+ "WHERE advisor_id=:advsr_id AND client_id=:client_id AND updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							ShareWealthPlans.class);
					query.setString("advsr_id", advisorId);
					query.setString("client_id", client_id);
					query.setLong("timstmp", timestamp);
				} else {
					sql = "SELECT * from share_plans "
							+ "WHERE advisor_id=:advsr_id AND updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							ShareWealthPlans.class);
					query.setString("advsr_id", advisorId);
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
	public List<WealthPlanElements> getPlanElements(String wp) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM WealthPlanElements WHERE wealth_plan_id=? AND is_delete=0");
			query.setString(0, wp);

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<WealthPlanElements> lwp = (List<WealthPlanElements>) query.list();
		session.close();
		return lwp;
	}

	@Transactional
	public ShareWealthPlans getShareWealthPlan(String wealthPlanID,
			String ta_id, String p_id) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM ShareWealthPlans "
							+ "WHERE shared_to=? AND wealth_plan_id=? AND plan_element_id=?");
			query.setString(0, ta_id);
			query.setString(1, wealthPlanID);
			query.setString(2, p_id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
	}

	@Transactional
	public Long save(ShareWealthPlans wp) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(wp);
		session.close();
		return l;
	}

	@Transactional
	public void saveSharedWp(ShareWealthPlans swp) {
		Session session = sessionFactory.openSession();
		session.saveOrUpdate(swp);
		session.close();
	}

	@Transactional
	public void update(ShareWealthPlans wp) {
		try {
			Session session = sessionFactory.openSession();
			session.update(wp);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public void updateSharedPlanElement(String wp_id, String user_id) {

		Session session = sessionFactory.openSession();
		String date = String.valueOf(System.currentTimeMillis());
		Query query1 = session
				.createQuery("FROM ShareWealthPlans where wealth_plan_id = :wp_id and shared_to = :user_id and shared_status=1");
		query1.setParameter("wp_id", wp_id);
		query1.setParameter("user_id", user_id);

		List<ShareWealthPlans> swp = query1.list();
		int i = swp.size();

		Query query2 = session
				.createQuery("FROM WealthPlanElements where wealth_plan_id = :wp_id AND is_delete=0");
		query2.setParameter("wp_id", wp_id);

		List<WealthPlanElements> swp1 = query2.list();
		int j = swp1.size();
		int k = 0;

		if (i == j)
			k = 1;
		Query query = session
				.createQuery("update ShareWealthPlans set updated_date='" + date + "', full_plan = "
						+ k
						+ " "
						+ " where wealth_plan_id = :wp_id and shared_to = :user_id");
		query.setParameter("wp_id", wp_id);
		query.setParameter("user_id", user_id);
		query.executeUpdate();
		if(k == 1){
			Query qrywpe = session
				.createQuery("update WealthPlanElements set updated_date='" + date + "'	where wealth_plan_id = :wp_id");
			qrywpe.setParameter("wp_id", wp_id);
			qrywpe.executeUpdate();
			
			Query qury = session.createQuery("FROM WealthPlanElements WHERE wealth_plan_id=?");
			qury.setParameter(0, wp_id);
			List<WealthPlanElements> wpes = qury.list();
			
			for (WealthPlanElements wpe : wpes) {
				
			Query mile_q = session
					.createQuery("FROM Milestone WHERE wealth_plan_element_id=?");
			mile_q.setParameter(0, wpe.getWealth_plan_element_id());
			List<Milestone> ms = mile_q.list();

			for (Milestone m : ms) {
				String m_id = m.getMilestone_id();
				Query m_q = session
						.createQuery("UPDATE Milestone SET updated_date='" + date + "' WHERE milestone_id=?");
				m_q.setParameter(0, m_id);
				m_q.executeUpdate();

				Query todo_q = session.createQuery("FROM Todo WHERE milestone_id=?");
				todo_q.setParameter(0, m_id);
				List<Todo> todos = todo_q.list();

				for (Todo todo : todos) {
					String todo_id = todo.getTodo_id();

					Query t_q = session.createQuery("UPDATE Todo SET updated_date='" + date + "' WHERE todo_id=?");
					t_q.setParameter(0, todo_id);
					t_q.executeUpdate();

				}
		}
		}
			
		}
		session.close();
	}

	@Transactional
	public boolean delete(List<ShareWealthPlans> swp) {
		Session session = sessionFactory.openSession();

		try {
			for (ShareWealthPlans at : swp) {
				session.delete(at);
			}
			session.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Transactional
	public void deleteSharedTodos(String wp_id, String owner, String pe_id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			String sql = "UPDATE todos SET is_delete=1, updated_date='"+System.currentTimeMillis()	
					+"' where 1 and owner='"+owner+"' and milestone_id in (select m.milestone_id from wealth_plans wp "
					+ " INNER JOIN wealth_plan_elements wpe on wp.wealth_plan_id=wpe.wealth_plan_id"
					+ " INNER JOIN milestones m on wpe.wealth_plan_element_id=m.wealth_plan_element_id"
					+ " WHERE 1 and wp.wealth_plan_id='"+wp_id+"' and wpe.plan_element_id='"+pe_id+"')";
			query = session.createSQLQuery(sql);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
	}
}
