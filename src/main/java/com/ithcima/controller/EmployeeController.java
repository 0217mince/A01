package com.ithcima.controller;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithcima.domain.Approval;
import com.ithcima.domain.ComAdmin;
import com.ithcima.domain.Employee;
import com.ithcima.domain.EmployeeLogin;
import com.ithcima.domain.EmployeePerf;
import com.ithcima.domain.RespCommon;
import com.ithcima.service.ComAdminService;
import com.ithcima.service.EmployeeService;
import com.ithcima.utils.Aes;

@Controller
public class EmployeeController {
	RespCommon respComm;
	String jacksonConvertFailJsonStr = "{'status':400,'msg':'jackson'}";

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ComAdminService comAdminService;
	/**
	 * 员工登录
	 */
	@RequestMapping(value = "/employeeLogin", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object employeeLogin(@RequestParam(name = "uid") String aesEmPh,
			@RequestParam(name = "pwd") String aesEmPw,@RequestParam(name = "vrc_img") String aesPicCode,
			 HttpServletRequest request, HttpServletResponse response) {
//          String emPh="17367119893";
//          String emPw="123456";
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		EmployeeLogin employeeLogin = new EmployeeLogin();
		String emPh=Aes.aesDecrypt(aesEmPh);
	    String emPw=Aes.aesDecrypt(aesEmPw);
	    String picCode = Aes.aesDecrypt(aesPicCode);
	    HttpSession session = request.getSession();
	    String sePicCode = (String) session.getAttribute("comAdminRegPicCode");
		session.removeAttribute("comAdminRegPicCode");
		employeeLogin.setEmPh(emPh);
		employeeLogin.setEmPw(emPw);
		
		try {
		EmployeeLogin employeeLogin2 = employeeService.selectEmployeeLogin(emPh);
		System.out.println(employeeLogin2);
		  System.out.println(employeeLogin2.getEmPw());
		  System.out.println(employeeLogin2.getEmDep());
		if(employeeLogin2==null){
			respComm.status = 401;
			respComm.msg = "账号不存在";
		}
		if(!emPw.equals(employeeLogin2.getEmPw())){
			respComm.status = 402;
			respComm.msg = "密码错误";
		}
		if (!StringUtils.equalsIgnoreCase(picCode, sePicCode)) {
			respComm.status = 405;
			respComm.msg = " 图形验证码过期错误！";
		}
		if(emPw.equals(employeeLogin2.getEmPw())){
			respComm.status = 600;
			respComm.msg = "success";
			if((employeeLogin2.getEmDep()).equalsIgnoreCase("人事部")){
				System.out.println("hr");
				respComm.data="hr";
			}
			if(!(employeeLogin2.getEmDep()).equalsIgnoreCase("人事部")&&(employeeLogin2.getEmJob()).equalsIgnoreCase("部门主管")){
				respComm.data="head";
			}
			if(!(employeeLogin2.getEmDep()).equalsIgnoreCase("人事部")&&(employeeLogin2.getEmJob()).equalsIgnoreCase("员工")){
				respComm.data="normal";
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 403;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
   /*没有审核的员工
   */ 
	@RequestMapping(value = "/NoAuditEmployee", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object NoAuditEmployee(@RequestParam(name = "comAdminId") String aesComAdminId, HttpServletRequest request,
			HttpServletResponse response) {
		
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String comAdminId = Aes.aesDecrypt(aesComAdminId);
	    ComAdmin comAdmin=employeeService.selectfindComId(comAdminId);
	             System.out.println(comAdmin.getComId());
	             String comId=comAdmin.getComId();
		try {
			
		ArrayList<EmployeePerf> employeePerfs=	employeeService.selectNoAuditEmployee(comId);
		     System.out.println(employeePerfs);
			respComm.data = employeePerfs;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	/*
	 * 部门管理员审核
	*/
	@RequestMapping(value = "/employeeAudit", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object employeeAudit(@RequestParam(name = "emId") String aesEmId, HttpServletRequest request,
			HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emId = Aes.aesDecrypt(aesEmId);
		
		
		try {
			EmployeePerf employeePerf =employeeService.selectAllEmployeePerfs(emId);
			respComm.data = employeePerf;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	
	}
	/*
	存评星
	*/
	@RequestMapping(value = "/employeeGrade", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object employeeGrade(@RequestParam(name = "emId") String aesEmId, @RequestParam(name = "comAdminId") String aesComAdminId,
			@RequestParam(name = "achievement") String aesAchievement,@RequestParam(name = "workingAttitude") String aesWorkingAttitude,
			@RequestParam(name = "workingAbility") String aesWorkingAbility,
			@RequestParam(name = "attendance") String aesAttendance,@RequestParam(name = "punishment") String aesPunishment,
			@RequestParam(name = "reward") String aesReward,@RequestParam(name = "emText") String aesEmText,
						HttpServletRequest request,HttpServletResponse response) {

		     respComm = new RespCommon();
		     ObjectMapper mapper = new ObjectMapper();
//		String emId="99";
//		String comAdminId="17367119893";
//		String achievement="3";
//		String workingAttitude="3";
//		String workingAbility="3";
//		String attendance="3";
//		String punishment="3";
//		String reward="3";
//		String emText="3";
		String emId = Aes.aesDecrypt(aesEmId);
		String comAdminId = Aes.aesDecrypt(aesComAdminId);
	     System.out.println(comAdminId);
		String achievement = Aes.aesDecrypt(aesAchievement);
		String workingAttitude = Aes.aesDecrypt(aesWorkingAttitude);
		String workingAbility = Aes.aesDecrypt(aesWorkingAbility);
		String attendance = Aes.aesDecrypt(aesAttendance);
		String punishment = Aes.aesDecrypt(aesPunishment);
		String reward = Aes.aesDecrypt(aesReward);
		String emText = Aes.aesDecrypt(aesEmText);
    	String assessor=employeeService.selectfindName(comAdminId);
		   System.out.println(assessor);
		Employee employee=employeeService.selectIdByEmId(emId);
		  String comId=employee.getComId();
		  System.out.println(comId);
		  
		EmployeePerf  employeePerf=new EmployeePerf();
		employeePerf.setEmId(emId);
		employeePerf.setAchievement(achievement);
		employeePerf.setAttendance(attendance);
		employeePerf.setComId(comId);
		employeePerf.setWorkingAttitude(workingAttitude);
		employeePerf.setWorkingAbility(workingAbility);
		employeePerf.setPunishment(punishment);
		employeePerf.setReward(reward);
		employeePerf.setEmText(emText);
		employeePerf.setAssessor(assessor);
		String monthlyComScore=employeeService.addEmployeePerf(employeePerf);
		employeePerf.setMonthlyComScore(monthlyComScore);
		Date time=new Date();
		Timestamp ctime=new java.sql.Timestamp(time.getTime());
		time= ctime;		
		String sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
		employeePerf.setAssessmentDate(sdf);
		Calendar cal =Calendar.getInstance();
		int Month=cal.get(Calendar.MONTH);
		System.out.println(Month);
		 if(Month==1){
				int  year1=cal.get(Calendar.YEAR);
				int  year2=year1-1;
				String year=String.valueOf(year2);
				System.out.println(year);
		 }else{
			 String year=String.valueOf(cal.get(Calendar.YEAR));
		
		 employeePerf.setYear(year);
		String month=Month+"月";
		System.out.println(month);
		employeePerf.setMonth(month);
		 System.out.println(employeePerf.toString());
		 
		try {
			employeeService.updatePointsByComId(comId);
			 employeeService.updateEmployeePerf(employeePerf);
			
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}

	}
	
	/*
	查看历史审核
	*/
	@RequestMapping(value = "/historyAuditEmployee", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object historyAuditEmployee(@RequestParam(name = "emId") String aesEmId, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emId = Aes.aesDecrypt(aesEmId);
		
		try {
			ArrayList<EmployeePerf> employeePerf= employeeService.selectHistoryEmployee(emId);			
			respComm.data =employeePerf;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	/*
	详细审核记录
	*/
	@RequestMapping(value = "/historyAudit", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object historyAudit(@RequestParam(name = "emId") String aesEmId, 
			@RequestParam(name = "assessmentDate") String aesAssessmentDate,
			HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
//        String aesAssessmentDate="kQMFdUdlxQUin+sRi87wdDltO5NtER1mOqpFCIXQeEc=";
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
//          String emId="4";
//          String assessmentDate="2020-02-01 19:05:02";
		String emId = Aes.aesDecrypt(aesEmId);
		String assessmentDate = Aes.aesDecrypt(aesAssessmentDate);
		System.out.println(emId);
		System.out.println(assessmentDate);
        Date date=new SimpleDateFormat("yyyy-MM-dd").parse(assessmentDate);
        System.out.println(date);
          String sdf=new SimpleDateFormat("yyyy-MM-dd").format(date); 
          System.out.println(sdf);
		EmployeePerf  employeePerf=new EmployeePerf();
		employeePerf.setEmId(emId);
		employeePerf.setAssessmentDate(sdf);
		try {
			EmployeePerf  employee=	employeeService.selectHistory(employeePerf);
			respComm.data = employee;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	/**
	 * Hr 考评已审核的员工
	 */
	@RequestMapping(value = "/employeePerf", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object employeePerf(@RequestParam(name = "comAdminId") String aesComAdminId, 
			HttpServletRequest request,
			HttpServletResponse response) {
       
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();	
		String comAdminId = Aes.aesDecrypt(aesComAdminId);
		ComAdmin comAdmin=employeeService.selectfindComId(comAdminId);
               System.out.println(comAdmin.getComId());
		try {
			ArrayList<EmployeePerf> employeePerfs=employeeService.selectEmployeePerf(comAdmin.getComId());
			System.out.println(employeePerfs);
		  respComm.data = employeePerfs;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	
	/*
	hr查看详细审核信息
	*/
	@RequestMapping(value = "/detailEmployeePerf", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object detailEmployeePerf(@RequestParam(name = "emId") String aesEmId,HttpServletRequest request,
			HttpServletResponse response) {
     
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();	
		String emId = Aes.aesDecrypt(aesEmId);
		
		try {
			EmployeePerf employeePerfs=employeeService.selectEmployeePerfFor(emId);
		  respComm.data = employeePerfs;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
	
	}

	@RequestMapping(value = "/passAudit", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object passAudit(@RequestParam(name = "comAdminId") String aesComAdminId, @RequestParam(name = "emId") String aesEmId,
				@RequestParam(name = "achievement") String aesAchievement,	@RequestParam(name = "attendance") String aesAttendance,
			@RequestParam(name = "workingAttitude") String aesWorkingAttitude,@RequestParam(name = "punishment") String aesPunishment,
			@RequestParam(name = "reward") String aesReward,@RequestParam(name = "emText") String aesEmText,
			HttpServletRequest request,HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();	
		String comAdminId = Aes.aesDecrypt(aesComAdminId);
		String emId = Aes.aesDecrypt(aesEmId);
		String achievement = Aes.aesDecrypt(aesAchievement);
		String attendance = Aes.aesDecrypt(aesAttendance);
		String workingAttitude = Aes.aesDecrypt(aesWorkingAttitude);
		String punishment = Aes.aesDecrypt(aesPunishment);
		String reward = Aes.aesDecrypt(aesReward);
		String emText = Aes.aesDecrypt(aesEmText);
		String approver=employeeService.selectfindName(comAdminId);
		String comId=(employeeService.selectIdByEmId(emId)).getComId();
		
		EmployeePerf  employeePerf=new EmployeePerf();
		employeePerf.setAchievement(achievement);
		employeePerf.setAttendance(attendance);
		employeePerf.setComId(comId);
		employeePerf.setWorkingAttitude(workingAttitude);
		employeePerf.setPunishment(punishment);
		employeePerf.setReward(reward);
		employeePerf.setEmText(emText);
		employeePerf.setApprover(approver);
		employeePerf.setComId(comId);
		Date time=new Date();
		Timestamp ctime=new java.sql.Timestamp(time.getTime());
		time= ctime;
		String sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
		employeePerf.setApprovalDate(sdf);
		

		try {
		employeeService.updateEmployeePerfTwo(employeePerf);
		employeeService.updatePointsByComId(comId);
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
	}
	/*
	查看过去考评
	*/
	@RequestMapping(value = "/historyPassAuditEmployee", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object historyPassAuditEmployee(@RequestParam(name = "emId") String aesEmId, HttpServletRequest request,
			HttpServletResponse response) {
      
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emId = Aes.aesDecrypt(aesEmId);
		

		try {
			ArrayList<EmployeePerf> employeePerfs=employeeService.selectAllPassAudit(emId);
		   respComm.data = employeePerfs;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
}
	/*
	查看过去考评详细信息
	*/
	@RequestMapping(value = "/historyPassAuditEmployeePerfs", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object historyPassAuditEmployeePerfs(@RequestParam(name = "emId") String aesEmId, 
			@RequestParam(name = "approvalDate") String aesApprovalDate,
			HttpServletRequest request,	HttpServletResponse response) {
        	
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emId = Aes.aesDecrypt(aesEmId);
		String approvalDate = Aes.aesDecrypt(aesApprovalDate);
		EmployeePerf  employeePerf=new EmployeePerf();
		employeePerf.setEmId(emId);
		employeePerf.setApprovalDate(approvalDate);

		try {
			EmployeePerf employeePerfs=employeeService.selectPassAuditEmployeePerf(employeePerf);
		   respComm.data = employeePerfs;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
	}
	/*
   有多少年考评评记录
	*/
	@RequestMapping(value = "/haveYearEmployeePerf", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object haveYearEmployeePerf(@RequestParam(name = "emId") String aesEmId, @RequestParam(name = "evaluationYear") String aesYear,
			HttpServletRequest request,	HttpServletResponse response) {
//        	String emId="4";
//        	String approvalDate="2020";
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emId = Aes.aesDecrypt(aesEmId);
		String approvalDate = Aes.aesDecrypt(aesYear);
		EmployeePerf  employeePerf=new EmployeePerf();
		employeePerf.setEmId(emId);
		employeePerf.setApprovalDate(approvalDate);
		try {
			 ArrayList<EmployeePerf> employeePerfs=employeeService.selectEveryYearRecord(employeePerf);
		   respComm.data = employeePerfs;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
	}
	/*
	审批
	*/
	@RequestMapping(value = "/approvalRequest", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object approvalRequest(@RequestParam(name = "emPh") String aesEmPh, 
			@RequestParam(name = "emId") String aesEmId,@RequestParam(name = "comId") String aesComId,
			HttpServletRequest request,	HttpServletResponse response) {
        	
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emPh = Aes.aesDecrypt(aesEmPh);
		String emId = Aes.aesDecrypt(aesEmId);
		String comId = Aes.aesDecrypt(aesComId);
		Employee employee =employeeService.selectByEmPh(emPh);
		String  GmName=employee.getEmName();
		String  GmId=employee.getEmId();
		String  GmComId=employee.getComId();
		String  GmComName=(comAdminService.selectComInfo(GmComId)).getEpName();
		String emName=(employeeService.selectComEmployeeByEmId(emId)).getEmName();
		String type="背调";
		String state="审批中";
		String approverComName=(comAdminService.selectComInfo(comId)).getEpName();
		Date time=new Date();
		Timestamp ctime=new java.sql.Timestamp(time.getTime());
		time= ctime;
		String sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
		   Approval approval =new Approval();
		   approval.setApplicantName(emId);
		   approval.setApplicantId(emId);
		   approval.setApproverName(GmName);
		   approval.setApproverId(GmId);
		   approval.setApplicantComId(GmComId);
		   approval.setApplicantComName(GmComName);
		   approval.setType(type);
		   approval.setState(state);
		   approval.setApproverComId(comId);
		   approval.setApproverComName(approverComName);
		   approval.setApplicantDate(sdf);
		try {
			employeeService.insertApplicant(approval);
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
   }
  /*
	背调
	*/
	@RequestMapping(value = "/backTo", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object backTo(@RequestParam(name = "loginId") String aesEmPh,@RequestParam(name = "status") String aesStatus, 
			@RequestParam(name = "style") String aesStyle,
			HttpServletRequest request,	HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emPh = Aes.aesDecrypt(aesEmPh);
		System.out.println(emPh);
		String status = Aes.aesDecrypt(aesStatus);
		System.out.println(status);
		String style = Aes.aesDecrypt(aesStyle);
		System.out.println(style);
		String comId=(employeeService.selectByEmPh(emPh)).getComId();
		String emId=(employeeService.selectByEmPh(emPh)).getEmId();
		Approval approval=new Approval();
		if(status.equalsIgnoreCase("1")){
			approval.setApplicantComId(comId);
			if(style.equalsIgnoreCase("0")){
         	   ArrayList<Approval> approvals=employeeService.selectApplicantCom(comId);
         	       respComm.data = approvals;
         	      System.out.println(approvals);
				}
			if(style.equalsIgnoreCase("1")){
				String state="审批中";
				approval.setState(state);			
	      	   ArrayList<Approval> approvals=employeeService.selectApplicantCom2(approval);
	      	       respComm.data = approvals;
	      	     System.out.println(approvals);
			}
			if(style.equalsIgnoreCase("2")){
				String state="同意";			
				approval.setState(state);
			ArrayList<Approval> approvals=employeeService.selectApplicantCom2(approval);
			    respComm.data = approvals;
			    System.out.println(approvals);
			}
			if(style.equalsIgnoreCase("3")){
				String state="不同意";				
				approval.setState(state);
			ArrayList<Approval> approvals=employeeService.selectApplicantCom2(approval);
			    respComm.data = approvals;
			    System.out.println(approvals);
			}
			if(style.equalsIgnoreCase("4")){
				String state="已撤回";				
				approval.setState(state);
			ArrayList<Approval> approvals=employeeService.selectApplicantCom2(approval);
			    respComm.data = approvals;
			    System.out.println(approvals);
			}
			  respComm.status = 600;
			   respComm.msg = "success";   
		}
		else if(status.equalsIgnoreCase("0")){
			 approval.setApplicantId(emId);
			 if(style.equalsIgnoreCase("0")){
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm(emId);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("1")){
				 String state="审批中";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("2")){
				 String state="同意";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("3")){
				 String state="不同意";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("4")){
				 String state="已撤回";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			   respComm.status = 600;
			   respComm.msg = "success";
		}
			
		else {		
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
			try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	 
}
		
	
  /*	
	审批背调
	*/
	@RequestMapping(value = "/approverBackTo", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object approverBackTo(@RequestParam(name = "loginId") String aesEmPh,@RequestParam(name = "status") String aesStatus, 
			@RequestParam(name = "style") String aesStyle, 
			HttpServletRequest request,	HttpServletResponse response) { 
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emPh = Aes.aesDecrypt(aesEmPh);
		String status = Aes.aesDecrypt(aesStatus);
		String style = Aes.aesDecrypt(aesStyle);
		String comId=(employeeService.selectByEmPh(emPh)).getComId();
		Approval approval=new Approval();
		approval.setApproverComId(comId);
		if(status.equalsIgnoreCase("1")) {
			 String type="背调";
			 approval.setType(type);
			if(style.equalsIgnoreCase("0")){
				ArrayList<Approval> approvals=employeeService.selectBackToAppeal(approval);
				   respComm.data = approvals;
			}
			if(style.equalsIgnoreCase("1")){
				String state="审批中";
				approval.setState(state);
				ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
				   respComm.data = approvals;
			}
			if(style.equalsIgnoreCase("2")){
				String state="同意";
				approval.setState(state);
				ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
				   respComm.data = approvals;
			}
			if(style.equalsIgnoreCase("3")){
				String state="不同意";
				approval.setState(state);
				ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
				   respComm.data = approvals;
			}
			if(style.equalsIgnoreCase("4")){
				String state="已撤回";
				approval.setState(state);
				ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
				   respComm.data = approvals;
			}
			respComm.status = 600;
			respComm.msg = "success";
		}
		else if(status.equalsIgnoreCase("0")){
			 String type="申诉";
			 approval.setType(type);
			 if(style.equalsIgnoreCase("0")){
				 ArrayList<Approval> approvals=employeeService.selectBackToAppeal(approval);
				   respComm.data = approvals;
			 }
			 if(style.equalsIgnoreCase("1")){
				 String state="审批中";
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
				   respComm.data = approvals;
			 }
			 if(style.equalsIgnoreCase("2")){
					String state="同意";
					approval.setState(state);
					ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
					   respComm.data = approvals;
				}
				if(style.equalsIgnoreCase("3")){
					String state="不同意";
					approval.setState(state);
					ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
					   respComm.data = approvals;
				}
				if(style.equalsIgnoreCase("4")){
					String state="已撤回";
					approval.setState(state);
					ArrayList<Approval> approvals=employeeService.selectBackToAppeal2(approval);
					   respComm.data = approvals;
				}
				respComm.status = 600;
				respComm.msg = "success"; 
	}
		else{
			
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}	
	}
	/*
	查找员工
	*/
	@RequestMapping(value = "/findEmployee", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object findEmployee(@RequestParam(name = "stPhone",required=false) String aesEmPh, 
			@RequestParam(name = "stIdName",required=false) String aseEmName,
			HttpServletRequest request,	HttpServletResponse response) {
//	   String emPh="17367110004";
//	   String emName="周一";
		respComm = new RespCommon(); 
		ObjectMapper mapper = new ObjectMapper();
		Employee employee =new Employee();
		
		if(aesEmPh != null){
			String emPh = Aes.aesDecrypt(aesEmPh);
			employee.setEmPh(emPh);
		}
		if(aseEmName != null){
		String emName =Aes.aesDecrypt(aseEmName);
		employee.setEmName(emName);
		}
		try {
			ArrayList<Employee> employees=employeeService.selectComEmployee(employee);
		  respComm.data = employees;
			respComm.status = 600;
			respComm.msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	
	
	/*
	部门管理，普通员工申诉
	*/
	@RequestMapping(value = "/appeal", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object appeal(@RequestParam(name = "loginId") String aesEmPh,@RequestParam(name = "style") String aesStyle, 
			HttpServletRequest request,	HttpServletResponse response) {
      
		respComm = new RespCommon(); 
		ObjectMapper mapper = new ObjectMapper();
		String emPh = Aes.aesDecrypt(aesEmPh);
		String style = Aes.aesDecrypt(aesStyle);
		String emId=(employeeService.selectByEmPh(emPh)).getEmId();
		   Approval approval=new Approval();
			 approval.setApplicantId(emId);
			 try{
			 if(style.equalsIgnoreCase("0")){
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm(emId);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("1")){
				 String state="审批中";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("2")){
				 String state="同意";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("3")){
				 String state="不同意";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			 if(style.equalsIgnoreCase("4")){
				 String state="已撤回";	
					approval.setState(state);
				 ArrayList<Approval> approvals=employeeService.selectApplicantEm2(approval);
				   respComm.data = approvals;
				   System.out.println(approvals);
				  
			 }
			   respComm.status = 600;
			   respComm.msg = "success";
		}			
			 catch (Exception e) {
					e.printStackTrace();
					respComm.status = 401;
					respComm.msg = "操作异常";
				}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	
//	@RequestMapping(value = "/Grade", method = { RequestMethod.POST,
//			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
//	@ResponseBody
//	public Object Grade(
//						HttpServletRequest request,HttpServletResponse response) {
//
//		     respComm = new RespCommon();
//		     ObjectMapper mapper = new ObjectMapper();
//		
//		
//		String achievement="0";
//		String workingAttitude="0";
//		String workingAbility="0";
//		String attendance="0";
//		String punishment="0";
//		String reward="5";
//		EmployeePerf employeePerf=new EmployeePerf();
//		employeePerf.setAchievement(achievement);
//		employeePerf.setWorkingAbility(workingAbility);
//		employeePerf.setWorkingAttitude(workingAttitude);
//		employeePerf.setAttendance(attendance);
//		employeePerf.setPunishment(punishment);
//		employeePerf.setReward(reward);
//		try {
//			String grade=employeeService.addEmployeePerf(employeePerf);
//			   System.out.println(grade);
//				respComm.status = 600;
//				respComm.msg = "success";
//			} catch (Exception e) {
//				e.printStackTrace();
//				respComm.status = 401;
//				respComm.msg = "操作异常";
//			}
//		     try {
//					return mapper.writeValueAsString(respComm);
//				} catch (JsonProcessingException e) {
//					// TODO Auto-generated catch block
//					return jacksonConvertFailJsonStr;
//				}
//	}
//	
	
	
	
	
	
}