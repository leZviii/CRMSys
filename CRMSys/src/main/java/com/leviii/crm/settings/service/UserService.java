package com.leviii.crm.settings.service;

import com.leviii.crm.exception.LoginException;
import com.leviii.crm.settings.domain.User;

import java.util.List;

public interface UserService {

    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserList();
}
