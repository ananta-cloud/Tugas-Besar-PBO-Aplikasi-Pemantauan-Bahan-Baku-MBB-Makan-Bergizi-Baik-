package com.mbg.dao;

import com.codeway.daoTemplate.dao.GenericDaoImpl;
import com.mbg.config.MBGDataSource;
import com.mbg.model.User;

public class UserDao extends GenericDaoImpl<Integer, User> {
    public UserDao() {
        super(User.class, MBGDataSource.getInstance());
    }

    public User login(String email, String password) throws Exception {
        // Mengambil data user berdasarkan email
        return getSingleEntity("email = ?", email);
    }

    public User getByEmail(String email) throws Exception {
        return getSingleEntity("email = ?", email);
    }
}