package com.ithcima.mapper;

import org.springframework.stereotype.Repository;

import com.ithcima.domain.Admin;

@Repository
public interface AdminMapper {

	Admin selectAdmin(String id);
	
}
