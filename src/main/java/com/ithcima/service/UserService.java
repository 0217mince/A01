package com.ithcima.service;

import com.ithcima.domain.User;

public interface UserService {
    
	public User findByName(String name);
	
	public User findById(Integer id);
}
