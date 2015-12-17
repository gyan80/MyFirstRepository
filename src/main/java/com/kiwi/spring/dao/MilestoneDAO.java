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
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;

@Repository
public class MilestoneDAO {

	@Autowired
	Configuration env;

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public Milestone checkWPID(String wpid) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM Milestone as u WHERE u.milestone_id=?");
			query.setString(0, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Milestone m = (Milestone) query.uniqueResult();
		session.close();
		return m;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public int deleteAllElements(String mId) {
		
		Query query = null;
		Query todo_q = null;
		Query t_q = null;
		Query n_q = null;
		Session session = sessionFactory.openSession();
		String date = String.valueOf(System.currentTimeMillis());
		try {
			query = session.createQuery("UPDATE Milestone SET is_delete=1, updated_date='"
									+ date + "' WHERE milestone_id=?");
			query.setParameter(0, mId);

					todo_q = session.createQuery("FROM Todo WHERE milestone_id=?");
					todo_q.setParameter(0, mId);
					List<Todo> todos = todo_q.list();

					for (Todo todo : todos) {
						String todo_id = todo.getTodo_id();

						t_q = session.createQuery("UPDATE Todo SET is_delete=1, updated_date='"
										+ date + "' WHERE todo_id=?");
						t_q.setParameter(0, todo_id);
						t_q.executeUpdate();

						n_q = session.createQuery("UPDATE Notifications SET is_delete=1, updated_date='"
										+ date + "' WHERE todo_id=?");
						n_q.setParameter(0, todo_id);
						n_q.executeUpdate();
					}
		} catch (Exception e) {
			System.out.println(e);
		}
		int l = query.executeUpdate();
		session.close();
		return l;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Milestone> fetchAllWealthPlans(String advisorid, String cl_id,
			Long timestamp) {

		Query query = null;
		List<Milestone> milestone = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

				String sql = null;
				if (cl_id != null) {
					// String sql =
					// "SELECT m.id, m.milestone_id,m.wealth_plan_element_id,AES_DECRYPT(m.title,'kEyLI1Fy648tzWXGuRcxrg=='),m.status_id,m.is_delete,m.created_date,m.updated_date from milestones m INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id where w.advisor_id=? AND m.updated_date > :timstmp";
					sql = "SELECT m.* from milestones m "
							+ "INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
							+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
							+ "where w.advisor_id=:advsr_id "
							+ "AND w.client_id=:cl_id AND m.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							Milestone.class);
					query.setString("advsr_id", advisorid);
					query.setString("cl_id", cl_id);
					query.setLong("timstmp", timestamp);
				} else {

					sql = "SELECT m.* from milestones m "
							+ "INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
							+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
							+ "where w.advisor_id=:advsr_id "
							+ "AND m.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							Milestone.class);
					query.setString("advsr_id", advisorid);
					query.setLong("timstmp", timestamp);
				}

				milestone = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return milestone;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Milestone> fetchAllSharedWealthPlans(String trstdid, Long timestamp) {

		Query query = null;
		List<Milestone> td = null;
		Session session = sessionFactory.openSession();
		try {
			if (timestamp != null) {

				String sql = "SELECT m.* from milestones m"
						+ " INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id"
						+ " INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id"
						+ " AND wpe.plan_element_id=sp.plan_element_id"
						+ " where sp.shared_to=:trstdid AND m.updated_date > :timstmp";
				
				query = session.createSQLQuery(sql).addEntity(Milestone.class);
				query.setString("trstdid", trstdid);
				query.setLong("timstmp", timestamp);
				td = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return td;
	}
	
	@Transactional
	public ShareWealthPlans getSharedFull(String wpeid, String trstdid) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			String sql = "SELECT sp.* from milestones m "
					+ " INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
					+ " INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id"
					+ " where sp.shared_to=:trstdid";
			
			query = session.createSQLQuery(sql)
			.addEntity(ShareWealthPlans.class);
			query.setString("trstdid", trstdid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
	}

	@Transactional
	public ShareWealthPlans getSharedStatus(String wpeid, String trstdid) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			String sql = "SELECT sp.* from milestones m "
					+ " INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id"
					+ " INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id AND wpe.plan_element_id=sp.plan_element_id"
					+ " Where shared_to = :trstdid AND wpe.wealth_plan_element_id=:wpeid";
			
			query = session.createSQLQuery(sql)
			.addEntity(
					ShareWealthPlans.class);
			query.setString("trstdid", trstdid);
			query.setString("wpeid", wpeid);

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
	public Milestone byAdvisorID(String id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session
					.createQuery("FROM Milestone as u WHERE u.advisor_id=?");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Milestone m = (Milestone) query.uniqueResult();
		session.close();
		return m;
	}
	
	@Transactional
	public Milestone checkWPEId(String id) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session
					.createQuery("FROM Milestone u WHERE u.wealth_plan_element_id=? AND is_delete=0");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Milestone m = (Milestone) query.uniqueResult();
		session.close();
		return m;
	}

	@Transactional
	public Long save(Milestone wp) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(wp);
		session.close();
		return l;
	}

	@Transactional
	public void update(Milestone wp) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(wp);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
