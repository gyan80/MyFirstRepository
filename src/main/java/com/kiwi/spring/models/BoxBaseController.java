package com.kiwi.spring.models;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kiwi.spring.config.Configuration;
import com.kiwi.spring.dao.AccessTokenDAO;
import com.kiwi.spring.dao.BoxDAO;
import com.kiwi.spring.dao.UserDAO;
import com.kiwi.spring.entity.AccessToken;
import com.kiwi.spring.entity.Box;
import com.kiwi.spring.entity.BoxFolders;
import com.kiwi.spring.entity.User;

public class BoxBaseController {


	@Autowired
	BoxDAO boxDAO;
	
	@Autowired
	UserDAO userDAO;

	@Autowired
	AccessTokenDAO accessTokenDAO;

	@Autowired
	Configuration env;

	protected boolean validateAccessToken(String accToken) {

		try {
			AccessToken aToken = accessTokenDAO.byAccessToken(accToken);
			if (aToken != null)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	protected Box getBoxAccessToken() {

		try {
			Box box = boxDAO.getBoxAccessToken();
			if (box != null)
				return box;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected User getUserObjectByID(String userid) {

		try {
			User usr = boxDAO.byID(userid);
			if (usr != null) {
				return usr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected BoxFolders getBoxFoldersData(String userId) {

		BoxFolders bf = boxDAO.getBoxFoldersData(userId);
		return bf;

	}
	
	protected List<BoxFolders> getBoxNonEditableFoldersList(String userId) {

		List<BoxFolders> bfs = boxDAO.getBoxNonEditableFoldersList(userId);
		return bfs;

	}
	
}
