package com.ithcima.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithcima.domain.Employee;
import com.ithcima.domain.EmployeeEvent;
import com.ithcima.domain.EmployeePerf;
import com.ithcima.domain.RespCommon;
import com.ithcima.service.EmployeeService;
import com.ithcima.utils.Aes;

@Controller
public class EmployeeEventController {

	RespCommon respComm;
	String jacksonConvertFailJsonStr = "{'status':400,'msg':'jackson'}";
	@Autowired
	private EmployeeService employeeService;
	/**
	 * 获取员工在当前公司的事件记录
	 */
	@RequestMapping(value = "/comEmployeeEvent", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comEmployeeEvent(@RequestParam(name = "emId") String aesEmId, HttpServletRequest request,
			HttpServletResponse response) {
//        String emId="5";
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String emId = Aes.aesDecrypt(aesEmId);
         ArrayList<EmployeeEvent> employeeEvent=employeeService.selectComEmployeeEvent(emId);
         System.out.println(employeeEvent);
         ArrayList<EmployeeEvent> employeeEvent2= employeeService.selectOtherComEvent(emId);
         System.out.println(employeeEvent2);
         employeeEvent.addAll(employeeEvent2);
		try {
			respComm.data = employeeEvent ;
			
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
	 * 获取员工评价 
	 */
	@RequestMapping(value = "/comEmployeeEvaluation", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comEmployeeEvaluation(@RequestParam(name = "emId") String aesEmId, 
			@RequestParam(name = "from") String aesFrom, 
			HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String emId = Aes.aesDecrypt(aesEmId);
		String from = Aes.aesDecrypt(aesFrom);

		try {
			respComm.data = employeeService.selectComEmployeeEvaluation(emId, from);
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
	 * 记录员工重大事件
	 */
	@RequestMapping(value = "/comEmployeeEventUpdate", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comEmployeeEventUpdate(@RequestParam(name = "emId") String aesEmId, 
			@RequestParam(name = "comAdminId") String aesComAdminId, 
			@RequestParam(name = "emName") String aesEmName, 
			@RequestParam(name = "emDep") String aesEmDep, 
			@RequestParam(name = "emJob") String aesEmJob, 
			@RequestParam(name = "type") String aesType, 
			@RequestParam(name = "content") String aesContent, 
			@RequestParam(name = "result") String aesResult, 
			@RequestParam(name = "notekeeperOpinion") String aesnotekeeperOpinion, 
			HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comAdminId = Aes.aesDecrypt(aesComAdminId);
		
		EmployeeEvent employeeEvent = new EmployeeEvent();
		employeeEvent.setEmId(Aes.aesDecrypt(aesEmId));
		employeeEvent.setComId(employeeService.selectfindComId(comAdminId).getComId());
		employeeEvent.setEmName(Aes.aesDecrypt(aesEmName));
		employeeEvent.setEmDep(Aes.aesDecrypt(aesEmDep));
		employeeEvent.setEmJob(Aes.aesDecrypt(aesEmJob));
		employeeEvent.setType(Aes.aesDecrypt(aesType));
		employeeEvent.setContent(Aes.aesDecrypt(aesContent));
		employeeEvent.setResult(Aes.aesDecrypt(aesResult));
		employeeEvent.setNotekeeper(employeeService.selectfindName(comAdminId));
		employeeEvent.setNotekeeperOpinion(Aes.aesDecrypt(aesnotekeeperOpinion));

		try {
			respComm.data = employeeService.updateComEmployeeEvent(employeeEvent);
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
	考评重大事件
	*/
	
		@RequestMapping(value = "/auditComEmployeeEvent", method = { RequestMethod.POST,
				RequestMethod.GET }, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public Object AduitcomEmployeeEvent(@RequestParam(name = "comAdminId") String aesComAdminId,				
				HttpServletRequest request,HttpServletResponse response) {

			respComm = new RespCommon();
			ObjectMapper mapper = new ObjectMapper();
           
        	String comAdminId = Aes.aesDecrypt(aesComAdminId);
			String comId=(employeeService.selectfindComId(comAdminId)).getComId();

			try {
		ArrayList<EmployeeEvent> employeeEvents=employeeService.selectNoAuditEvent(comId);
				respComm.data =employeeEvents;
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
	考评成功
	*/
		@RequestMapping(value = "/passAuditComEmployeeEvent", method = { RequestMethod.POST,
				RequestMethod.GET }, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public Object passAduitcomEmployeeEvent(@RequestParam(name = "comAdminId") String aesComAdminId, 	
				@RequestParam(name = "emId") String aesEmId, @RequestParam(name = "emDep") String aesEmDep,
				@RequestParam(name = "emJob") String aesEmjob,@RequestParam(name = "approverOpinion") String aesApproverOpinion,
				HttpServletRequest request,HttpServletResponse response) {

			respComm = new RespCommon();
			ObjectMapper mapper = new ObjectMapper();
			String comAdminId = Aes.aesDecrypt(aesComAdminId);
			EmployeeEvent employeeEvent=new EmployeeEvent();
			employeeEvent.setEmId(Aes.aesDecrypt(aesEmId));
			employeeEvent.setEmDep(Aes.aesDecrypt(aesEmDep));
			employeeEvent.setEmJob(Aes.aesDecrypt(aesEmjob));
			employeeEvent.setApproverOpinion(Aes.aesDecrypt(aesApproverOpinion));
			String approver=employeeService.selectfindName(comAdminId);
			employeeEvent.setApprover(approver);
			Date time=new Date();
			Timestamp ctime=new java.sql.Timestamp(time.getTime());
			time= ctime;
			String sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
			employeeEvent.setApprovalDate(sdf);
			try {
				employeeService.updateEmployeeEvent(employeeEvent);
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
	折线图 
	*/

		@RequestMapping(value = "/monthScore", method = { RequestMethod.POST,
				RequestMethod.GET }, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public Object monthScore(@RequestParam(name = "emId") String aesEmId,@RequestParam(name = "month") String aesMonth,
				HttpServletRequest request,HttpServletResponse response) {
			respComm = new RespCommon();
			ObjectMapper mapper = new ObjectMapper();
			String emId = Aes.aesDecrypt(aesEmId);
			String month = Aes.aesDecrypt(aesMonth);
//			String finalScore=employeeService.getEmployeeGeneralScore(emId);  
			 EmployeePerf employeeScore=new EmployeePerf();
			 EmployeePerf employeeinfo=new EmployeePerf();
			 employeeinfo.setEmId(emId);
			 employeeinfo.setApprovalDate(month);
//			    employeeScore.setFinalScore(finalScore);
			String generalAchievement=employeeService.getEmployeeGeneralAchivement(emId);
			String generalWorkingAbility= employeeService.getEmployeeGeneralWorkingAbility(emId);
			String generalWorkingAttitude =employeeService.getEmployeeGeneralWorkingAttitude(emId);
			String generalAttendance    =employeeService.getEmployeeGeneralAttendance(emId);
			employeeScore.setAchievement(generalAchievement);
			employeeScore.setWorkingAbility(generalWorkingAbility);
			employeeScore.setWorkingAttitude(generalWorkingAttitude);
			employeeScore.setAttendance(generalAttendance);
			try {
           String employeeMonthScore=employeeService.addEmployeePerf(employeeScore);
              System.out.println(employeeMonthScore);
           employeeinfo.setMonthlyComScore(employeeMonthScore);
           System.out.println(employeeinfo);
                       employeeService.insertEmployeePerf(employeeinfo);
             EmployeePerf employeePerfScore= employeeService.selectMonthScore(employeeinfo);
             System.out.println(employeePerfScore);
				  respComm.data=employeePerfScore;
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
	普通员工查看自己的生涯
//	*/	
		@RequestMapping(value = "/checkCareer", method = { RequestMethod.POST,
				RequestMethod.GET }, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public Object checkCareer(@RequestParam(name = "loginPhone") String aesEmPh,
				HttpServletRequest request,HttpServletResponse response) {

			respComm = new RespCommon();
			ObjectMapper mapper = new ObjectMapper();
			String emPh = Aes.aesDecrypt(aesEmPh);
			String emId=(employeeService.selectByEmPh(emPh)).getEmId();
			System.out.println(emId);
			 ArrayList<EmployeeEvent> employeeEvent=employeeService.selectComEmployeeEvent(emId);
			 ArrayList<EmployeeEvent> employeeEvent2= employeeService.selectOtherComEvent(emId);
	         System.out.println(employeeEvent2);
	         employeeEvent.addAll(employeeEvent2);
			try {   
				  respComm.data=employeeEvent;
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
	查看公司	
	*/	
		@RequestMapping(value = "/checkCom", method = { RequestMethod.POST,
				RequestMethod.GET }, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public Object checkCom(@RequestParam(name = "epId") String aesComId,@RequestParam(name = "stId") String aesEmId, 
				HttpServletRequest request,HttpServletResponse response) {

			respComm = new RespCommon();
			ObjectMapper mapper = new ObjectMapper();
			String emId = Aes.aesDecrypt(aesEmId);
			String comId = Aes.aesDecrypt(aesComId);
			try {   
				  respComm.data=employeeService.selectComInfo(comId);
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
	
		
		
}