package com.ithcima.mapper;

import com.ithcima.domain.User;

public interface UserMapper {
     
	public User findByName(String name);
	
	public User findById(Integer id);
}
