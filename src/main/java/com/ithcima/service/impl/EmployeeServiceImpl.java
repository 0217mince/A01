package com.ithcima.service.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.internal.ObjectArrayElementComparisonStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ithcima.domain.Approval;
import com.ithcima.domain.Com;
import com.ithcima.domain.ComAdmin;
import com.ithcima.domain.Employee;
import com.ithcima.domain.EmployeeEvaluation;
import com.ithcima.domain.EmployeeEvent;
import com.ithcima.domain.EmployeeLogin;
import com.ithcima.domain.EmployeePerf;
import com.ithcima.mapper.ComAdminMapper;
import com.ithcima.mapper.EmployeeMapper;
import com.ithcima.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService{

	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private ComAdminMapper comAdminMapper;
	
	@Override
	public EmployeeLogin selectEmployeeLogin(String emPh){
		EmployeeLogin employeelogin=  employeeMapper.selectEmployee(emPh);
		return employeelogin;
	}
	
	@Override
	public  ArrayList<EmployeePerf> selectNoAuditEmployee(String comId){
		return employeeMapper.selectNoAuditEmployee(comId);
	}
	
	@Override
	public ComAdmin selectfindComId(String comAdminId){
		return comAdminMapper.selectComAdmin(comAdminId);
	}
	@Override
	public EmployeePerf selectAllEmployeePerfs(String emId){
		return employeeMapper.selectAllEmployeePerfs(emId);
	}
	@Override
	public String selectfindName(String comAdminId){
		return comAdminMapper.selectfindName(comAdminId);
	}
	@Override
	public Employee selectIdByEmId(String emId){
		return employeeMapper.selectComEmployeeByEmId(emId);
	}
	@Override
	public void updatePointsByComId(String comId){
		comAdminMapper.updatePointsByComId(comId);
	}

	@Override
	public void updateEmployeePerf(EmployeePerf employeePerf) {
		employeeMapper.updateEmployeePerf(employeePerf);
		
	}
	
	@Override
	public ArrayList<EmployeePerf> selectHistoryEmployee(String emId){
		return employeeMapper.selectHistoryEmployee(emId);
	}
	@Override
	public EmployeePerf selectHistory(EmployeePerf employeePerf){
		return employeeMapper.selectHistory(employeePerf);
	}
	@Override
	public ArrayList<EmployeePerf> selectEmployeePerf(String comId){
		return employeeMapper.selectEmployeePerf(comId);
	}
	@Override
	public EmployeePerf	selectEmployeePerfFor(String emId){
		return employeeMapper.selectEmployeePerfFor(emId);
	}
	@Override
	public void updateEmployeePerfTwo(EmployeePerf employeePerf){
		employeeMapper.updateEmployeePerfTwo(employeePerf);
	}
	@Override
	public ArrayList<EmployeePerf> selectAllPassAudit(String emId){
		return employeeMapper.selectAllPassAudit(emId);
	}
	@Override
	public EmployeePerf selectPassAuditEmployeePerf(EmployeePerf employeePerf){
		return employeeMapper.selectPassAuditEmployeePerf(employeePerf);
	}
	
	@Override
	public ArrayList<EmployeeEvent> selectComEmployeeEvent(String emId){
		Employee employee = employeeMapper.selectComEmployeeByEmId(emId);
		String comId = employee.getComId();
		return  employeeMapper.selectComEmployeeEvent(emId,comId);
	
	}
	@Override
	public ArrayList<EmployeeEvent>selectOtherComEvent(String emId){
		return employeeMapper.selectOtherComEvent(emId);
		 
	}
	@Override
	public Com selectComInfo(String comId){
		return comAdminMapper.selectComInfo(comId);	
	}
	@Override
	public ArrayList<Employee> selectComEmployee(Employee employee){
		return employeeMapper.selectComEmployee(employee);
		
	}
	@Override
	public ArrayList<EmployeeEvaluation> selectComEmployeeEvaluation(String emId,String from){
		Employee employee = employeeMapper.selectComEmployeeByEmId(emId);
		EmployeeEvaluation employeeEvaluation = new EmployeeEvaluation();
		employeeEvaluation.setEmId(emId);
		employeeEvaluation.setComId(employee.getComId());
		switch(from){
			case "部门主管":
				employeeEvaluation.setEvaluatorJob("部门主管");
			case "人事部":
				employeeEvaluation.setEvaluatorDep("人事部");
		}
		return employeeMapper.selectComEmployeeEvaluation(employeeEvaluation);
	}

	
	@Override
	public boolean updateComEmployeeEvent(EmployeeEvent employeeEvent){
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;
		String sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
		employeeEvent.setRecordDate(sdf);
		int mark = employeeMapper.insertEmployeeEvent(employeeEvent);
		return mark != 0 ? false : true;
	}
	@Override
	public ArrayList<EmployeeEvent> selectNoAuditEvent(String comId){
		return employeeMapper.selectNoAuditEvent(comId);
		
	}
	
	@Override
	public void updateEmployeeEvent(EmployeeEvent employeeEvent){
		employeeMapper.updateEmployeeEvent(employeeEvent);
	}
	@Override
	public ArrayList<EmployeePerf> selectEveryYearRecord(EmployeePerf employeePerf){
		return employeeMapper.selectEveryYearRecord(employeePerf);
	}
	@Override
	public EmployeePerf selectMonthScore(EmployeePerf employeePerf){
		return employeeMapper.selectMonthScore(employeePerf);
		
	}
	@Override
	public Employee  selectByEmPh(String emPh){
		return employeeMapper.selectByEmPh(emPh);
		
	}
	@Override
	public Employee selectComEmployeeByEmId(String emId){
		return employeeMapper.selectComEmployeeByEmId(emId);
		
	}
	@Override
	public void  insertApplicant(Approval approval){
		employeeMapper.insertApplicant(approval);
	}
	@Override
	public ArrayList<Approval> selectApplicantCom(String comId){
		return employeeMapper.selectApplicantCom(comId);	
	}
	@Override
	public ArrayList<Approval> selectApplicantCom2(Approval approval){
		return employeeMapper.selectApplicantCom2(approval);	
	}
	@Override
	public ArrayList<Approval> selectApplicantEm(String emId){
		return employeeMapper.selectApplicantEm(emId);	
	}
	@Override
	public ArrayList<Approval> selectApplicantEm2(Approval approval){
		return employeeMapper.selectApplicantEm2( approval);	
	}
	@Override
	public ArrayList<Approval> selectBackToAppeal(Approval approval){
		return employeeMapper.selectBackToAppeal( approval);		
	}
	@Override
	public ArrayList<Approval> selectBackToAppeal2(Approval approval){
		return employeeMapper.selectBackToAppeal2(approval);	
	}
	// 计算总分
		@Override
		public String getEmployeeGeneralScore(String emId) {
			ArrayList<EmployeeEvaluation> employeeEvaluations = employeeMapper.selectScoreEvaluations(emId);
			ArrayList<EmployeePerf> employeePerfs = employeeMapper.selectScorePerformance(emId);
			int evaCount = employeeEvaluations.size();
			int perfCount = employeePerfs.size();
			double comprehensive = 0;
			double perfScore = 0;
			double generalCompScore = 0;
			double generalPerfScore = 0;

			// 计算evaluation最终得分
			int count = 0;
			// for (int i = 0; i < evaCount / 2; i++) {
			// EmployeeEvaluation employeeEvaluation = employeeEvaluations.get(i);
			// comprehensive +=
			// Double.valueOf(employeeEvaluation.getComprehensiveEvaluation());
			// count++;
			// }
			// if (count != 0) {
			// comprehensive /= count;
			// generalCompScore = comprehensive;
			// }
			//
			// count = 0;
			// comprehensive = 0;
			// for (int i = evaCount / 2; i < evaCount * 3 / 4; i++) {
			// EmployeeEvaluation employeeEvaluation = employeeEvaluations.get(i);
			// comprehensive +=
			// Double.valueOf(employeeEvaluation.getComprehensiveEvaluation());
			// count++;
			// }
			// if (count != 0) {
			// comprehensive /= count;
			// generalCompScore = (generalCompScore + comprehensive) / 2;
			// }
			//
			// count = 0;
			// comprehensive = 0;
			// for (int i = evaCount * 3 / 4; i < evaCount; i++) {
			// EmployeeEvaluation employeeEvaluation = employeeEvaluations.get(0);
			// comprehensive +=
			// Double.valueOf(employeeEvaluation.getComprehensiveEvaluation());
			// count++;
			// }
			// if (count != 0) {
			// comprehensive /= count;
			// generalCompScore = (generalCompScore + comprehensive) / 2;
			// }

			// 计算perf最终得分
			count = 0;
			for (int i = 0; i < perfCount / 2; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				perfScore = Double.valueOf(employeePerf.getMonthlyComScore());
				count++;
			}
			if (count != 0) {
				perfScore /= count;
				generalPerfScore = perfScore;
			}

			count = 0;
			perfScore = 0;
			for (int i = perfCount / 2; i < perfCount * 3 / 4; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				perfScore = Double.valueOf(employeePerf.getMonthlyComScore());
				count++;
			}
			if (count != 0) {
				perfScore /= count;
				generalPerfScore = (generalPerfScore + perfScore) / 2;
			}

			count = 0;
			perfScore = 0;
			for (int i = perfCount * 3 / 4; i < perfCount; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				perfScore = Double.valueOf(employeePerf.getMonthlyComScore());
				count++;
			}
			if (count != 0) {
				perfScore /= count;
				generalPerfScore = (generalPerfScore + perfScore) / 2;
			}

			DecimalFormat df = new DecimalFormat("0.0");
			return df.format(generalPerfScore);
			
		}
		
		// 计算perf年度分数
		 @Override
		 public ArrayList<Object> getEmployeeYearlyPerfScore(String emId) {
		  ArrayList<EmployeePerf> employeePerfs = employeeMapper.selectScorePerformance(emId);
		  String year = null;
		  HashMap<String, Object> map;
		  ArrayList<Object> list = new ArrayList<>();
		  double perf = 0;

		  int count = 0;
		  for (EmployeePerf employeePerf : employeePerfs) {
		   if (year == null) {
		    year = employeePerf.getYear();
		   } else if (!employeePerf.getYear().equals(year)) {
		    perf /= count;
		    map = new HashMap<>();
		    map.put("year", year);
		    map.put("score", perf);
		    list.add(map);
		    year = employeePerf.getYear();
		    perf = 0;
		    count = 0;
		   }

		   perf += Double.valueOf(employeePerf.getMonthlyComScore());
		   count++;
		  }

		  if (count != 0) {
		   map = new HashMap<>();
		   map.put("year", year);
		   map.put("score", perf);
		   list.add(map);
		  }

		  return list;
		 }
		// 计算achivement总分
		@Override
		public String getEmployeeGeneralAchivement(String emId) {
			ArrayList<EmployeePerf> employeePerfs = employeeMapper.selectScorePerformance(emId);
			int perfCount = employeePerfs.size();
			double achievement = 0;
			double generalAchievement = 0;

			int count = 0;
			for (int i = 0; i < perfCount / 2; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				achievement = Double.valueOf(employeePerf.getAchievement());
				count++;
			}
			if (count != 0) {
				achievement /= count;
				generalAchievement = achievement;
			}

			count = 0;
			achievement = 0;
			for (int i = perfCount / 2; i < perfCount * 3 / 4; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				achievement = Double.valueOf(employeePerf.getAchievement());
				count++;
			}
			if (count != 0) {
				achievement /= count;
				generalAchievement = (generalAchievement + achievement) / 2;
			}

			count = 0;
			achievement = 0;
			for (int i = perfCount * 3 / 4; i < perfCount; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				achievement = Double.valueOf(employeePerf.getAchievement());
				count++;
			}
			if (count != 0) {
				achievement /= count;
				generalAchievement = (generalAchievement + achievement) / 2;
			}

			DecimalFormat df = new DecimalFormat("0.0");
			return df.format(generalAchievement);
		}

		// 计算workingAbility总分
		@Override
		public String getEmployeeGeneralWorkingAbility(String emId) {
			ArrayList<EmployeePerf> employeePerfs = employeeMapper.selectScorePerformance(emId);
			int perfCount = employeePerfs.size();
			double workingAbility = 0;
			double generalWorkingAbility = 0;

			int count = 0;
			for (int i = 0; i < perfCount / 2; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				workingAbility = Double.valueOf(employeePerf.getWorkingAbility());
				count++;
			}
			if (count != 0) {
				workingAbility /= count;
				generalWorkingAbility = workingAbility;
			}

			count = 0;
			workingAbility = 0;
			for (int i = perfCount / 2; i < perfCount * 3 / 4; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				workingAbility = Double.valueOf(employeePerf.getWorkingAbility());
				count++;
			}
			if (count != 0) {
				workingAbility /= count;
				generalWorkingAbility = (generalWorkingAbility + workingAbility) / 2;
			}

			count = 0;
			workingAbility = 0;
			for (int i = perfCount * 3 / 4; i < perfCount; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				workingAbility = Double.valueOf(employeePerf.getWorkingAbility());
				count++;
			}
			if (count != 0) {
				workingAbility /= count;
				generalWorkingAbility = (generalWorkingAbility + workingAbility) / 2;
			}

			DecimalFormat df = new DecimalFormat("0.0");
			return df.format(generalWorkingAbility);
		}

		// 计算workingAttitude总分
		@Override
		public String getEmployeeGeneralWorkingAttitude(String emId) {
			ArrayList<EmployeePerf> employeePerfs = employeeMapper.selectScorePerformance(emId);
			int perfCount = employeePerfs.size();
			double workingAttitude = 0;
			double generalWorkingAttitude = 0;

			int count = 0;
			for (int i = 0; i < perfCount / 2; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				workingAttitude = Double.valueOf(employeePerf.getWorkingAttitude());
				count++;
			}
			if (count != 0) {
				workingAttitude /= count;
				generalWorkingAttitude = workingAttitude;
			}

			count = 0;
			workingAttitude = 0;
			for (int i = perfCount / 2; i < perfCount * 3 / 4; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				workingAttitude = Double.valueOf(employeePerf.getWorkingAttitude());
				count++;
			}
			if (count != 0) {
				workingAttitude /= count;
				generalWorkingAttitude = (generalWorkingAttitude + workingAttitude) / 2;
			}

			count = 0;
			workingAttitude = 0;
			for (int i = perfCount * 3 / 4; i < perfCount; i++) {
				EmployeePerf employeePerf = employeePerfs.get(i);
				workingAttitude = Double.valueOf(employeePerf.getWorkingAttitude());
				count++;
			}
			if (count != 0) {
				workingAttitude /= count;
				generalWorkingAttitude = (generalWorkingAttitude + workingAttitude) / 2;
			}

			DecimalFormat df = new DecimalFormat("0.0");
			return df.format(generalWorkingAttitude);
		}
		
		// 计算attendance总分
			@Override
			public String getEmployeeGeneralAttendance(String emId) {
				ArrayList<EmployeePerf> employeePerfs = employeeMapper.selectScorePerformance(emId);
				int perfCount = employeePerfs.size();
				double attendance = 0;
				double generalAttendance = 0;

				int count = 0;
				for (int i = 0; i < perfCount / 2; i++) {
					EmployeePerf employeePerf = employeePerfs.get(i);
					attendance = Double.valueOf(employeePerf.getAttendance());
					count++;
				}
				if (count != 0) {
					attendance /= count;
					generalAttendance = attendance;
				}

				count = 0;
				attendance = 0;
				for (int i = perfCount / 2; i < perfCount * 3 / 4; i++) {
					EmployeePerf employeePerf = employeePerfs.get(i);
					attendance = Double.valueOf(employeePerf.getAttendance());
					count++;
				}
				if (count != 0) {
					attendance /= count;
					generalAttendance = (generalAttendance + attendance) / 2;
				}

				count = 0;
				attendance = 0;
				for (int i = perfCount * 3 / 4; i < perfCount; i++) {
					EmployeePerf employeePerf = employeePerfs.get(i);
					attendance = Double.valueOf(employeePerf.getAttendance());
					count++;
				}
				if (count != 0) {
					attendance /= count;
					generalAttendance = (generalAttendance + attendance) / 2;
				}

				DecimalFormat df = new DecimalFormat("0.0");
				return df.format(generalAttendance);
			}
	
			// 计算该月Perf总分后插入
			@Override
			public String addEmployeePerf(EmployeePerf employeePerf) {
				double perfScore = 0;

				perfScore += Double.valueOf(employeePerf.getAchievement());
				perfScore += Double.valueOf(employeePerf.getWorkingAbility());
				perfScore += Double.valueOf(employeePerf.getWorkingAttitude());
				perfScore += Double.valueOf(employeePerf.getAttendance());
				if (employeePerf.getReward() != null) {
					perfScore += 0.2 * Double.valueOf(employeePerf.getReward());
				}
				if (employeePerf.getPunishment() != null) {
					perfScore -= 0.2 * Double.valueOf(employeePerf.getPunishment());
				}
				if (perfScore > 5) {
					perfScore = 5;
				} else if (perfScore < 0) {
					perfScore = 0;
				}

				perfScore /= 4;
				DecimalFormat df = new DecimalFormat("0.00");
				String monthlyComScore=df.format(perfScore);
				return  monthlyComScore;
		
			}

			@Override
			public  boolean insertEmployeePerf(EmployeePerf employeePerf) {
				int mark = employeeMapper.insertEmployeePerf(employeePerf);
 				return mark == 1 ? true : false;
			}
			
			

			
}
