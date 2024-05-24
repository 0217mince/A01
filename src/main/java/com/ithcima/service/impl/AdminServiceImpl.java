package com.ithcima.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ithcima.domain.Admin;
import com.ithcima.mapper.AdminMapper;
import com.ithcima.mapper.ComAdminMapper;
import com.ithcima.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private AdminMapper adminMapper;
	
	@Override
	public Admin selectAdmin(String id){
		return adminMapper.selectAdmin(id);
	}
	
}
