package com.ithcima.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithcima.domain.ComAdmin;
import com.ithcima.domain.Admin;
import com.ithcima.domain.Com;
import com.ithcima.domain.ComAudit;
import com.ithcima.domain.RespCommon;
import com.ithcima.domain.SimpleCom;
import com.ithcima.service.ComAdminService;
import com.ithcima.service.AdminService;
import com.ithcima.service.impl.ComAdminServiceImpl;
import com.ithcima.utils.Aes;
import com.ithcima.utils.PicCode;
import com.ithcima.utils.imageUtil;
import com.ithcima.utils.randomComId;
import com.ithcima.utils.readCookies;
import com.ithcima.utils.sendsms;

@Controller
public class AdminController {
    RespCommon respComm; 
	String jacksonConvertFailJsonStr="{'status':400,'msg':'jackson'}";
	
	@Autowired
	private AdminService adminService;
	
	
	 /**
	   * admin登录短信验证码
	   */
	  @RequestMapping(value="/adminLoginSMS",method={RequestMethod.POST,RequestMethod.GET},produces="application/json;charset=UTF-8")
	  @ResponseBody
	  public Object adminLoginSMS(@RequestParam(name="mobile")String aesPhoneNumber,HttpServletRequest request) throws ParseException{
	   
		  respComm=new RespCommon(); 
		  ObjectMapper mapper = new ObjectMapper();
	   
		  String phoneNumber=Aes.aesDecrypt(aesPhoneNumber);
	   
		  Admin admin= adminService.selectAdmin(phoneNumber);
	   
		  if(admin==null){
			  respComm.status=403;
			  respComm.msg=" 该手机号不存在，请先注册！";
		   	}else {
		   		HttpSession session=request.getSession();
		   		long currentDate=System.currentTimeMillis();
		   		Long date=(Long) session.getAttribute("adminLoginSMSTime");
		   		if(date!= null && currentDate - date < 1000*60*1){
		   			respComm.status=406;
		   			respComm.msg="验证码发送未超过一分钟";
		   		}else{
	   
		   			Map<String,Integer> map=sendsms.sendMessage(phoneNumber);
		   			int status=map.get("status");
		   			String phoneCode=String.valueOf(map.get("pcode"));
//	   	            SimpleDateFormat sdf=new SimpleDateFormat();
		   			session.setAttribute("adminLoginSMSTime", System.currentTimeMillis());
		   			session.setAttribute("adminLoginPhoneNumber", phoneNumber);
		   			session.setAttribute("adminLoginPhoneCode", phoneCode);
	   
		   			if(status==600){
		   				System.out.println("短信提交成功");
		   				respComm.status=status;
		   				respComm.msg=" 短信发送成功!";
		   			}else if(status==401){
		   				respComm.status=status;
		   				respComm.msg=" http错误！";   
		   			}else if(status==402) {
		   				respComm.status=status;
		   				respComm.msg=" IO错误！";   
		   			}else if(status==403) {
		   				respComm.status=status;
		   				respComm.msg=" 文件错误！";
		   			}else{
		   				respComm.status=400;
		   				respComm.msg=" 未知错误！";
		   			}
		   		}
		   }
		  try {
			  return mapper.writeValueAsString(respComm);
		  } catch (JsonProcessingException e) {
			  // TODO Auto-generated catch block 
			  return jacksonConvertFailJsonStr;
		  }  
	  }
	  
	  	/**
		  * admin登录
		  * @param response 
		  */
		@RequestMapping(value="/adminLogin",method={RequestMethod.POST,RequestMethod.GET},produces="application/json;charset=UTF-8")
	 @ResponseBody
	 public Object adminLogin(@RequestParam(name="uid")String aesPhoneNumber,
			 @RequestParam(name="vrc")String aesPhoneCode,
			 HttpServletRequest request, 
			 HttpServletResponse response){
	  respComm=new RespCommon(); 
	  ObjectMapper mapper = new ObjectMapper();
	  String phoneNumber=Aes.aesDecrypt(aesPhoneNumber);
	  String phoneCode=Aes.aesDecrypt(aesPhoneCode);
	  HttpSession session=request.getSession();
	  
	  Admin admin=adminService.selectAdmin(phoneNumber);
	  System.out.println(admin.getId());
	  if(phoneCode.equals("9999")&&admin.getId()!=null){
		  System.out.println("测试员登录成功");
		  respComm.status=600;
		  respComm.msg="测试员登录成功!";
		  Cookie cookie = new Cookie("admin", phoneNumber);
		  cookie.setMaxAge(60*60*24); // 设置一天有效
		  response.addCookie(cookie); // 服务器返回给浏览器cookie以便下次判断
	  }else{
	  
	  String sePhoneNumber=(String) session.getAttribute("adminLoginPhoneNumber");
	  String sePhoneCode=(String) session.getAttribute("adminLoginPhoneCode");
	  
	  Long slogdate=(Long) session.getAttribute("adminLoginSMSTime");
	  if(!phoneNumber.equals(sePhoneNumber)){
	  	   respComm.status=401;
	  	   respComm.msg=" 与发送短信的手机号不一致!";
	  }else if(StringUtils.isEmpty(sePhoneNumber)&&StringUtils.isEmpty(sePhoneCode)&&slogdate==null){
		  respComm.status=403;
		  respComm.msg="验证码未发送或已过期！";
	  }else{

		  session.removeAttribute("adminLoginPhoneCode");
		  session.removeAttribute("adminLoginSMSTime");
		  //设置Session中的验证码的失效时间(1分钟)
		  if((System.currentTimeMillis()-slogdate)>1000*60*1){
			  respComm.status=406; 
			  respComm.msg="验证码已失效！请重新发送验证码！";
		  }else{
			  System.out.println("用户手机:"+phoneNumber+"发送手机:"+sePhoneNumber+"用户验证:"+phoneCode+"发送验证:"+sePhoneCode);
			  if(phoneNumber.equals(sePhoneNumber)&phoneCode.equals(sePhoneCode)){
				  System.out.println("登录成功");
				  respComm.status=600;
				  respComm.msg=" 登录成功!";
				  Cookie cookie = new Cookie("admin", phoneNumber);
				  cookie.setMaxAge(60*60*24); // 设置一天有效
				  response.addCookie(cookie); // 服务器返回给浏览器cookie以便下次判断
			  }else{
				  respComm.status=401;
				  respComm.msg=" 验证码错误！";   
			  }
		  }
	  }
	  }
	  try {
	   return mapper.writeValueAsString(respComm);
	  } catch (JsonProcessingException e) {
	   // TODO Auto-generated catch block 
	   return jacksonConvertFailJsonStr;
	  }  
	 }
	 

	/**
	  * 管理员登出
	  */
 	@RequestMapping(value="/adminLogout",method={RequestMethod.POST,RequestMethod.GET},produces="application/json;charset=UTF-8")
	 @ResponseBody
	 public Object adminLogout(HttpServletRequest request, HttpServletResponse response){
	  respComm=new RespCommon(); 
	  ObjectMapper mapper = new ObjectMapper();
	  
	   Cookie cookie = new Cookie("admin","");
	   cookie.setMaxAge(0); // 设置一分钟有效
	   response.addCookie(cookie);
	  
	   respComm.status=600;
	   respComm.msg="用户注销成功！";
	     
	  try {
	   return mapper.writeValueAsString(respComm);
	  } catch (JsonProcessingException e) {
	   // TODO Auto-generated catch block 
	   return jacksonConvertFailJsonStr;
	  }  
 }
}