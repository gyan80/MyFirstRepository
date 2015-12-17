package com.kiwi.spring.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Milestone;
import com.kiwi.spring.entity.Notifications;
import com.kiwi.spring.entity.PlanElement;
import com.kiwi.spring.entity.ShareWealthPlans;
import com.kiwi.spring.entity.Todo;
import com.kiwi.spring.entity.User;
import com.kiwi.spring.entity.WealthPlans;

@Repository
public class TodoDAO {

	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public Todo checkTodoID(String wpid) {
		
		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("FROM Todo as u WHERE u.todo_id=?");
			query.setString(0, wpid);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Todo td = (Todo) query.uniqueResult();
		session.close();
		return td;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Todo> fetchAllTodo(String advisorid, String cl_id,
			Long timestamp) {

		Query query = null;
		List<Todo> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

				String sql = null;
				if (cl_id != null) {
					// String sql =
					// "SELECT t.id,t.todo_id,t.milestone_id, AES_DECRYPT(t.title,'kEyLI1Fy648tzWXGuRcxrg==') ,AES_DECRYPT(t.owner,'kEyLI1Fy648tzWXGuRcxrg=='),t.due_date,t.status_id,t.is_delete,t.created_date,t.updated_date from todos t INNER JOIN milestones m on t.milestone_id=m.milestone_id INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id where w.advisor_id=:advsr_id AND t.updated_date > :timstmp";
					sql = "SELECT t.* from todos t "
							+ "INNER JOIN milestones m on t.milestone_id=m.milestone_id "
							+ "INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
							+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
							+ "where w.advisor_id=:advsr_id AND w.client_id=:cl_id AND t.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(Todo.class);
					query.setString("advsr_id", advisorid);
					query.setString("cl_id", cl_id);
					query.setLong("timstmp", timestamp);

				} else {

					sql = "SELECT t.* from todos t "
							+ "INNER JOIN milestones m on t.milestone_id=m.milestone_id "
							+ "INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
							+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
							+ "where w.advisor_id=:advsr_id AND t.updated_date > :timstmp";
					query = session.createSQLQuery(sql).addEntity(Todo.class);
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
	public WealthPlans getCreatedByIdUsingId(String milestoneId) {

		Query query = null;
		WealthPlans wp = null;
		String sql = null;
		Session session = sessionFactory.openSession();
		try {
			sql = "SELECT w.* from todos t "
					+ "INNER JOIN milestones m on t.milestone_id=m.milestone_id "
					+ "INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
					+ "INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id "
					+ "where m.milestone_id=:milestoneid";
			query = session.createSQLQuery(sql).addEntity(WealthPlans.class);
			query.setString("milestoneid", milestoneId);

			wp = (WealthPlans) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return wp;
	}
	
	@Transactional
	public PlanElement getWealthPlanElementsUsingTodoAndMilestoneId(String milestoneId, String todoId) {

		Query query = null;
		PlanElement wp = null;
		String sql = null;
		Session session = sessionFactory.openSession();
		try {
			sql = "SELECT pe.* from todos t "
					+ "INNER JOIN milestones m on t.milestone_id=m.milestone_id "
					+ "INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id "
					+ "INNER JOIN plan_elements pe on pe.plan_element_id=wpe.plan_element_id "
					+ "where m.milestone_id=:milestoneid AND t.todo_id=:todoid";
			query = session.createSQLQuery(sql).addEntity(PlanElement.class);
			query.setString("milestoneid", milestoneId);
			query.setString("todoid", todoId);

			wp = (PlanElement) query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return wp;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Todo> fetchAllSharedTodo(String trstdid, Long timestamp) {

		Query query = null;
		List<Todo> td = null;
		Session session = sessionFactory.openSession();
		try {

			if (timestamp != null) {

				// String sql =
				// "SELECT t.id,t.todo_id,t.milestone_id, AES_DECRYPT(t.title,'kEyLI1Fy648tzWXGuRcxrg==') ,AES_DECRYPT(t.owner,'kEyLI1Fy648tzWXGuRcxrg=='),t.due_date,t.status_id,t.is_delete,t.created_date,t.updated_date from todos t INNER JOIN milestones m on t.milestone_id=m.milestone_id INNER JOIN wealth_plan_elements wpe on wpe.wealth_plan_element_id=m.wealth_plan_element_id INNER JOIN wealth_plans w on w.wealth_plan_id=wpe.wealth_plan_id where w.advisor_id=:advsr_id AND t.updated_date > :timstmp";
				String sql = "select t.* from todos t "
						+ " INNER JOIN milestones m on t.milestone_id=m.milestone_id "
						+ " INNER JOIN wealth_plan_elements wpe on m.wealth_plan_element_id=wpe.wealth_plan_element_id"
						+ " INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id AND wpe.plan_element_id=sp.plan_element_id"
						+ " Where shared_to = :trstd_id AND t.updated_date > :timstmp";
				query = session.createSQLQuery(sql).addEntity(Todo.class);
				query.setString("trstd_id", trstdid);
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
	public ShareWealthPlans getSharedFull(String mid, String trstdid) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			String sql = "select sp.* from todos t "
					+ " INNER JOIN milestones m on t.milestone_id=m.milestone_id "
					+ " INNER JOIN wealth_plan_elEments wpe on m.wealth_plan_element_id=wpe.wealth_plan_element_id"
					+ " INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id"
					+ " where sp.shared_to=:trstdid";

			query = session.createSQLQuery(sql).addEntity(
					ShareWealthPlans.class);
			query.setString("trstdid", trstdid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		ShareWealthPlans swp = (ShareWealthPlans) query.uniqueResult();
		session.close();
		return swp;
	}

	@Transactional
	public ShareWealthPlans getSharedStatus(String mid, String trstdid) {

		Query query = null;
		Session session = sessionFactory.openSession();
		try {
			String sql = "select sp.* from todos t "
					+ " INNER JOIN milestones m on t.milestone_id=m.milestone_id "
					+ " INNER JOIN wealth_plan_elements wpe on m.wealth_plan_element_id=wpe.wealth_plan_element_id"
					+ " INNER JOIN share_plans sp on wpe.wealth_plan_id=sp.wealth_plan_id AND wpe.plan_element_id=sp.plan_element_id"
					+ " where sp.shared_to=:trstdid AND m.milestone_id = :mid";

			query = session.createSQLQuery(sql).addEntity(
					ShareWealthPlans.class);
			query.setString("trstdid", trstdid);
			query.setString("mid", mid);

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
	public Milestone checkMilestoneId(String id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session
					.createQuery("FROM Milestone u WHERE u.milestone_id=? AND is_delete=0");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Milestone m = (Milestone) query.uniqueResult();
		session.close();
		return m;
	}

	@Transactional
	public Todo byAdvisorID(String id) {
		
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session.createQuery("FROM Todo as u WHERE u.advisor_id=?");
			query.setString(0, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Todo t = (Todo) query.uniqueResult();
		session.close();
		return t;
	}

	@Transactional
	public Long save(Todo td) {
		Session session = sessionFactory.openSession();
		System.out.println("============");
		Long l = (Long) session.save(td);
		System.out.println("-------------"+l);
		session.close();
		return l;
	}

	@Transactional
	public Long save(Notifications notification) {
		Session session = sessionFactory.openSession();
		Long l = (Long) session.save(notification);
		session.close();
		return l;
	}

	@Transactional
	public void update(Todo wp) {
		try {
			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.update(wp);
			tx.commit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public void update(Notifications notification) {
		try {
			Session session = sessionFactory.openSession();
			session.saveOrUpdate(notification);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public Long checkAndInsertTodo(Todo todo, String createdBy, String bsonId) {

		Long result = 0l;
		Session session = sessionFactory.openSession();
		Query query = null;
		try {

			query = session.createQuery("FROM Notifications WHERE todo_id=?");
			query.setString(0, todo.getTodo_id());
			@SuppressWarnings("unchecked")
			List<Notifications> notifications = query.list();

			if (notifications != null) {
				for (Notifications noti : notifications) {
					if (todo.getStatus() == 1 && todo.getIs_delete() == 0) {
						Notifications notification = new Notifications();
						notification.setCreated_date(String.valueOf(System
								.currentTimeMillis()));
						notification.setFrequency(todo.getNotification_entry());
						notification.setIs_delete(todo.getIs_delete());
						notification.setNotification_id(bsonId);
						notification.setTitle(todo.getTitle());
						notification.setTodo_id(todo.getTodo_id());
						notification.setTodo_status(todo.getStatus());
						notification.setUpdated_date(String.valueOf(System
								.currentTimeMillis()));
						notification.setUser_id(createdBy);
						result = save(notification);
					} else {
						noti.setFrequency(todo.getNotification_entry());
						noti.setIs_delete(todo.getIs_delete());
						noti.setTitle(todo.getTitle());
						noti.setTodo_status(todo.getStatus());
						noti.setUpdated_date(String.valueOf(System
								.currentTimeMillis()));
						noti.setUser_id(todo.getOwner());
						update(noti);
					}
				}
			} else {
				Notifications notification = new Notifications();
				notification.setCreated_date(String.valueOf(System
						.currentTimeMillis()));
				notification.setFrequency(todo.getNotification_entry());
				notification.setIs_delete(todo.getIs_delete());
				notification.setNotification_id(bsonId);
				notification.setTitle(todo.getTitle());
				notification.setTodo_id(todo.getTodo_id());
				notification.setTodo_status(todo.getStatus());
				notification.setUpdated_date(String.valueOf(System
						.currentTimeMillis()));
				notification.setUser_id(todo.getOwner());
				result = save(notification);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.close();
		return result;
	}

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
	public int deleteAllElements(String todoId) {
		
		Query query = null;
		Query n_q = null;
		String date = String.valueOf(System.currentTimeMillis());
		Session session = sessionFactory.openSession();
		try {
			query = session.createQuery("UPDATE Todo SET is_delete=1, updated_date='"
							+ date + "' WHERE todo_id=?");
			query.setParameter(0, todoId);

			n_q = session.createQuery("UPDATE Notifications SET is_delete=1, updated_date='"
							+ date + "' WHERE todo_id=?");
			n_q.setParameter(0, todoId);
			n_q.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}
		int l = query.executeUpdate();
		session.close();
		return l;
	}

	@Transactional
	public Notifications getNotificationObjectById(String todo_id, String userId) {
		Notifications noti = null;
		Session session = sessionFactory.openSession();
		Query query = null;
		try {
			query = session.createQuery("FROM Notifications WHERE todo_id=? AND user_id=?");
			query.setString(0, todo_id);
			query.setString(1, userId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		noti = (Notifications) query.uniqueResult();
		session.close();
		return noti;
	}
}
