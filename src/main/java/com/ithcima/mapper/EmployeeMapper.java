package com.ithcima.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.ithcima.domain.Approval;
import com.ithcima.domain.Employee;
import com.ithcima.domain.EmployeeEvaluation;
import com.ithcima.domain.EmployeeEvent;
import com.ithcima.domain.EmployeeLogin;
import com.ithcima.domain.EmployeePerf;
import com.ithcima.domain.SimpleEmployeePerf;

@Repository
public interface EmployeeMapper {

	EmployeeLogin selectEmployee(String emPh);
	
	void updateEmployeePerf(EmployeePerf employeePerf);
	
	EmployeePerf selectAllEmployeePerfs(String emId);
	
	ArrayList<EmployeePerf> selectNoAuditEmployee(String comId);	
	
	ArrayList<EmployeePerf> selectHistoryEmployee(String emId);
	
	EmployeePerf selectHistory(EmployeePerf employeePerf);
	
	ArrayList<EmployeePerf> selectEmployeePerf(String comId);
	
	EmployeePerf	selectEmployeePerfFor(String emId);
	
	void updateEmployeePerfTwo(EmployeePerf employeePerf);
	
	ArrayList<EmployeePerf> selectAllPassAudit(String emId);
	
	EmployeePerf selectPassAuditEmployeePerf(EmployeePerf employeePerf);
	
	ArrayList<EmployeeEvent> selectComEmployeeEvent(@Param("emId") String emId,@Param("comId") String comId);

	ArrayList<EmployeeEvaluation> selectComEmployeeEvaluation(EmployeeEvaluation employeeEvaluation);
	
	Employee selectComEmployeeByEmId(String emId);

	int insertEmployeeEvent(EmployeeEvent employeeEvent);
	
	ArrayList<EmployeeEvent> selectNoAuditEvent(String comId);
	
	void updateEmployeeEvent(EmployeeEvent employeeEvent);
	
	ArrayList<EmployeePerf> selectScorePerformance(String emId);
	
	ArrayList<EmployeeEvaluation> selectScoreEvaluations(String emId);
	
	ArrayList<EmployeePerf> selectEveryYearRecord(EmployeePerf employeePerf);

	int insertEmployeePerf(EmployeePerf employeePerf);
	
//	boolean insertEmployee(Employee employeeScore);
	
	ArrayList<EmployeeEvent> selectOtherComEvent(String emId);
	
//	String selectEmIdByEmPh(String emPh);
	
	EmployeePerf selectMonthScore(EmployeePerf employeePerf);
	
	Employee  selectByEmPh(String emPh);
	
	void  insertApplicant(Approval approval);
	
	ArrayList<Approval> selectApplicantCom(String comId);
	ArrayList<Approval> selectApplicantCom2(Approval approval);
	
	ArrayList<Approval> selectApplicantEm(String emId);
	ArrayList<Approval> selectApplicantEm2(Approval approval);
	
	ArrayList<Approval> selectBackTo(String comId);
	ArrayList<Approval> selectBackTo2(String comId);
	
	ArrayList<Approval> selectBackToAppeal(Approval approval);
	ArrayList<Approval> selectBackToAppeal2(Approval approval);
	
	ArrayList<Employee> selectComEmployee(Employee employee);
}
