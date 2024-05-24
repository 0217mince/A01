package com.ithcima.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.ithcima.domain.*;

@Repository
public interface ComAdminMapper {
	
	int insertComAdmin(ComAdmin comAdmin);
	int insertComAdminInfo(ComAdmin comAdmin);
	int insertComInfo(ComAdmin admin);
	
	ComAdmin selectComAdmin(String comAdminId); 
	
	Com selectComInfo(String comId);

	int updateComInfo(Com com);
	
    ArrayList<SimpleCom> selectSimpleComs(@Param("auditStatus") String auditStatus,@Param("modStatus") String modStatus);
    
    ArrayList<Com> selectComs(@Param("auditStatus") String auditStatus,@Param("modStatus") String modStatus);

    int updateComInfoMod(ComMod comMod);
    		
    int insertComMod(ComMod comMod);
    
    ArrayList<ComMod> selectComMods(@Param("comId")String comId,@Param("modStatus")String modStatus);
    
    int updateComInfoAudit(ComAudit comAudit);
	
    int insertComAudit(ComAudit comAudit);
    
    ArrayList<ComAudit> selectComAudits(String comId);
    
    ArrayList<ComDepartment> selectComDepartment(@Param("comId") String comId,@Param("isDelete") String isDelete);
    
    int updateComDepartment(ComDepartment comDepartment);
    
    int insertComDepartment(ComDepartment comDepartment);
    
    int updateComDepartmentIsDelete(@Param("comId") String comId, @Param("dName") String dName,@Param("isDelete") String isDelete);

    ArrayList<Employee> selectComEmployees(Employee employee);
    
    int updateComEmployee(Employee employee);
    
    int insertComEmployee(Employee employee);
    
    int updateComEmployeeIsDelete(String emId);

    int insertEmployeeEvent(EmployeeEvent employeeEvent);
    
    int updateEmployeeEventDelete(String emId);
    
    Employee selectComEmployeeByEmPh(String emPh);
    
//	String selectfindComId(String comAdminId);
	
	String selectfindName(String comAdminId);
	
	void updatePointsByComId(String comId);
}
