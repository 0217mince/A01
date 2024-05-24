package com.ithcima.service;

import java.text.ParseException;
import java.util.ArrayList;

import com.ithcima.domain.ComAdmin;
import com.ithcima.domain.Com;
import com.ithcima.domain.ComAudit;
import com.ithcima.domain.ComDepartment;
import com.ithcima.domain.ComMod;
import com.ithcima.domain.Employee;
import com.ithcima.domain.SimpleCom;

public interface ComAdminService {

	ComAdmin selectComAdmin(String phoneNumber);

	int insertComAdmin(ComAdmin adm);

	Com selectComInfo(String comId);

	int updateComInfo(Com com);

	String selectComAuditStatus(String comId);

	ArrayList<SimpleCom> selectAuditedSimpleComs();

	ArrayList<SimpleCom> selectToAuditSimpleComs();

	int deleteCom(String comId, String deleteReason) throws ParseException;

	ArrayList<ComMod> selectComMods(String comId);

	ArrayList<ComAudit> selectComAudits(String comId);

	int auditCom(String comId) throws ParseException;

	int applyComAudit(String comId, String auditFile) throws ParseException;

	int auditFailCom(String comId, String failReason) throws ParseException;

	int comAuditWarningConfirm(String comId) throws ParseException;

	ArrayList<SimpleCom> selectDeletedSimpleComs();

	int restoreDeletedCom(String comId);

	ComMod selectComMod(String comId);

	ComAudit selectComAudit(String comId);

	int alterAuditedCom(String comId, String auditFile);

	ArrayList<Com> selectAuditedComs();

	boolean insertComDepartment(ComDepartment dep);

	boolean updateComDepartment(ComDepartment dep);

	boolean deleteComDepartment(String comId, String dName);

	boolean restoreComDepartment(String comId, String dName);

	ArrayList<ComDepartment> selectComDepartments(String comId);

	ArrayList<ComDepartment> selectDeletedComDepartments(String comId);

	boolean updateComEmployee(Employee employee);

	boolean deleteEmployee(String emId);

	ArrayList<Employee> selectComEmployees(Employee employee);
	
}
