package com.ithcima.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ithcima.domain.Approval;
import com.ithcima.domain.Com;
import com.ithcima.domain.ComAdmin;
import com.ithcima.domain.Employee;
import com.ithcima.domain.EmployeeEvaluation;
import com.ithcima.domain.EmployeeEvent;
import com.ithcima.domain.EmployeeLogin;
import com.ithcima.domain.EmployeePerf;

public interface EmployeeService {

//	Map<String, Object> employeeLogin(EmployeeLogin employeeLogin);
	EmployeeLogin selectEmployeeLogin(String emPh);
	ArrayList<EmployeePerf> selectNoAuditEmployee(String comId);

	EmployeePerf selectAllEmployeePerfs(String emId);

	String selectfindName(String comAdminId);

	Employee selectIdByEmId(String emId);

	void updatePointsByComId(String comId);

	void updateEmployeePerf(EmployeePerf employeePerf);

	ArrayList<EmployeePerf> selectHistoryEmployee(String emId);

	EmployeePerf selectHistory(EmployeePerf employeePerf);

	ArrayList<EmployeePerf> selectEmployeePerf(String comId);

	EmployeePerf selectEmployeePerfFor(String emId);

	void updateEmployeePerfTwo(EmployeePerf employeePerf);

	ArrayList<EmployeePerf> selectAllPassAudit(String emId);

	EmployeePerf selectPassAuditEmployeePerf(EmployeePerf employeePerf);

	ComAdmin selectfindComId(String comAdminId);

	ArrayList<EmployeeEvent> selectComEmployeeEvent(String emId);

	ArrayList<EmployeeEvaluation> selectComEmployeeEvaluation(String emId, String from);

	boolean updateComEmployeeEvent(EmployeeEvent employeeEvent);

	ArrayList<EmployeeEvent> selectNoAuditEvent(String comId);

	void updateEmployeeEvent(EmployeeEvent employeeEvent);

	ArrayList<EmployeePerf> selectEveryYearRecord(EmployeePerf employeePerf);

	String getEmployeeGeneralScore(String emId);

//	HashMap<String, Double> getEmployeePerfScorePerYear(String emId);

	String getEmployeeGeneralAchivement(String emId);

	String getEmployeeGeneralWorkingAbility(String emId);

	String getEmployeeGeneralWorkingAttitude(String emId);

	String getEmployeeGeneralAttendance(String emId);

	String addEmployeePerf(EmployeePerf employeePerf);

	boolean insertEmployeePerf(EmployeePerf employeePerf);
	
	ArrayList<EmployeeEvent> selectOtherComEvent(String emId);
	
	Com selectComInfo(String comId);
	
//	String selectEmIdByEmPh(String emPh);
	
	EmployeePerf selectMonthScore(EmployeePerf employeePerf);
	
	Employee selectByEmPh(String emPh);
	
	Employee selectComEmployeeByEmId(String emId);
	void insertApplicant(Approval approval);
	
	ArrayList<Object> getEmployeeYearlyPerfScore(String emId);
	ArrayList<Approval> selectApplicantCom(String comId);
	ArrayList<Approval> selectApplicantCom2(Approval approval);
	
	ArrayList<Approval> selectApplicantEm(String emId);
	ArrayList<Approval> selectApplicantEm2(Approval approval);
	
	ArrayList<Approval> selectBackToAppeal(Approval approval);
	ArrayList<Approval> selectBackToAppeal2(Approval approval);
	ArrayList<Employee> selectComEmployee(Employee employee);
	

	
	
	


	



}
