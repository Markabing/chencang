package gdufs.agency.service;

import gdufs.agency.entity.User;

public interface UserService {
	User getUser(String openId);
	boolean updateUser(User user);

}
