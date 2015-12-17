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
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.WealthPlanElements;
import com.kiwi.spring.entity.WealthPlans;

@Repository
public class WealthPlanElementsDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public WealthPlanElements checkWPID(String wpid) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM WealthPlanElements as u WHERE u.wealth_plan_element_id=?");
			query.setString(0, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		WealthPlanElements wpe = (WealthPlanElements) query.uniqueResult();
		session.close();
		return wpe;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<WealthPlanElements> fetchAllWealthPlans(String advisorid,
			String cl_id, Long timestamp) {

		Query query = null;
		List<WealthPlanElements> wpe = null;
		Session session = sessionFactory.openSession();
		try {
			if (timestamp != null) {

				String sql = null;
				if (cl_id != null) {
//				String sql = "SELECT wpe.id,wpe.wealth_plan_element_id,wpe.wealth_plan_id,wpe.plan_element_id,AES_DECRYPT(wpe.goal,'kEyLI1Fy648tzWXGuRcxrg=='),wpe.is_delete,wpe.created_date,wpe.updated_date from wealth_plan_elements wpe INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id where w.advisor_id=? AND wpe.updated_date > :timstmp";
				sql = "SELECT wpe.* from wealth_plan_elements wpe "
						+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
						+ "where w.advisor_id=:advsr_id AND w.client_id=:cl_id AND wpe.updated_date > :timstmp";
				query = session.createSQLQuery(sql).addEntity(
						WealthPlanElements.class);
				query.setString("advsr_id", advisorid);
				query.setString("cl_id", cl_id);
				query.setLong("timstmp", timestamp);
				}
				else{
					sql = "SELECT wpe.* from wealth_plan_elements wpe "
							+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
							+ "where w.advisor_id=:advsr_id AND wpe.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(
							WealthPlanElements.class);
					query.setString("advsr_id", advisorid);
					query.setLong("timstmp", timestamp);

				}
				wpe = query.list();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return wpe;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<WealthPlanElements> fetchAllSharedWealthPlans(String trstdid,
			Long timestamp) {

		Query query = null;
		List<WealthPlanElements> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

//				String sql = "SELECT wpe.id,wpe.wealth_plan_element_id,wpe.wealth_plan_id,wpe.plan_element_id,AES_DECRYPT(wpe.goal,'kEyLI1Fy648tzWXGuRcxrg=='),wpe.is_delete,wpe.created_date,wpe.updated_date from wealth_plan_elements wpe INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id where w.advisor_id=? AND wpe.updated_date > :timstmp";
				String sql = "select wpe.* from wealth_plan_elements wpe "
						+ "INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id "
						+ "AND wpe.plan_element_id=sp.plan_element_id "
						+ "Where shared_to = :trstdid AND wpe.updated_date > :timstmp";
				query = session.createSQLQuery(sql).addEntity(
						WealthPlanElements.class);
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
	public ShareWealthPlans getSharedFull(String wpeid, String trstd_id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createSQLQuery("select * FROM share_plans as u "
					+ "WHERE u.shared_to=? AND wealth_plan_id=? limit 0, 1")
			.addEntity(
					ShareWealthPlans.class);
			query.setString(0, trstd_id);
			query.setString(1, wpeid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
	}

	@Transactional
	public ShareWealthPlans getSharedStatus(String wpid, String peid, String trstd_id) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createSQLQuery("select * FROM share_plans as u "
					+ "WHERE u.shared_to=? AND wealth_plan_id=? AND plan_element_id=? limit 0, 1")
			.addEntity(
					ShareWealthPlans.class);
			query.setString(0, trstd_id);
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
	public WealthPlanElements byAdvisorID(String id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session
					.createQuery("FROM WealthPlanElements as u WHERE u.advisor_id=?");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		WealthPlanElements wpe = (WealthPlanElements) query.uniqueResult();
		session.close();
		return wpe;
	}
	
	@Transactional
	public WealthPlans checkWealthPlanId(String wp_id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session
					.createQuery("FROM WealthPlans u WHERE u.wealth_plan_id=? AND is_delete=0");
			query.setString(0, wp_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		WealthPlans wp = (WealthPlans) query.uniqueResult();
		session.close();
		return wp;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public int deleteAllElements(String wpeId, String wpId, String peId) {
		
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
			sp_q = session.createQuery("UPDATE ShareWealthPlans SET shared_status=0, full_plan=0, updated_date='"
					+ date + "' WHERE wealth_plan_id=? AND plan_element_id=?");
				sp_q.setParameter(0, wpId);
				sp_q.setParameter(1, peId);
				sp_q.executeUpdate();
	
			query = session.createQuery("UPDATE WealthPlanElements SET is_delete=1, updated_date='"
							+ date + "' WHERE wealth_plan_id=?");
			query.setParameter(0, wpeId);
			
				mile_q = session
						.createQuery("FROM Milestone WHERE wealth_plan_element_id=?");
				mile_q.setParameter(0, wpeId);
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
		} catch (Exception e) {
			System.out.println(e);
		}
		int l = query.executeUpdate();
		session.close();
		return l;
	}
	
	@Transactional
	public PlanElement checkPlanElementId(String pe_id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session
					.createQuery("FROM PlanElement u WHERE u.plan_element_id=? AND is_delete=0");
			query.setString(0, pe_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		PlanElement pe = (PlanElement) query.uniqueResult();
		session.close();
		return pe;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Long checkAndInsertSharedWPE(String wp_id, String pe_id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			String sql = "SELECT * FROM share_plans s "
					+ "WHERE s.wealth_plan_id=? AND s.full_plan=1 GROUP BY s.shared_to";
			query = session
					.createSQLQuery(sql)
					.addEntity(ShareWealthPlans.class);
			query.setString(0, wp_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<ShareWealthPlans> swps = query.list();
		int count = swps.size();
		Long result =0l;
		if(count > 0){
			for (ShareWealthPlans swp : swps) {
				ShareWealthPlans new_swp = new ShareWealthPlans();
				new_swp.setAdvisor_id(swp.getAdvisor_id());
				new_swp.setClient_id(swp.getClient_id());
				new_swp.setShared_by(swp.getShared_by());
				new_swp.setShared_to(swp.getShared_to());
				new_swp.setWealth_plan_id(swp.getWealth_plan_id());
				new_swp.setPlan_element_id(pe_id);
				new_swp.setShared_status(1l);
				new_swp.setFull_plan(1l);
				new_swp.setCreated_date(String.valueOf(System
						.currentTimeMillis()));
				new_swp.setUpdated_date(String.valueOf(System
														.currentTimeMillis()));
				result = save(new_swp);
			}
		}
		session.close();
		return result;
	}

	@Transactional
	public Long save(WealthPlanElements wp) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(wp);
		session.close();
		return l;
	}
	
	@Transactional
	public Long save(ShareWealthPlans swp) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(swp);
		session.close();
		return l;
	}

	@Transactional
	public void update(WealthPlanElements wp) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(wp);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
