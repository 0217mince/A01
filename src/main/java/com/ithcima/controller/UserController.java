package com.ithcima.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithcima.domain.RespCommon;
import com.ithcima.utils.Aes;
import com.ithcima.utils.PicCode;
import com.ithcima.utils.sendsms;



@Controller
public class UserController {

	 RespCommon respComm; 
	 String jacksonConvertFailJsonStr="{'status':400,'msg':'jackson'}";

	 /*
	  * 普通用户登录
	  */
	 @RequestMapping(value="/tologin",method={RequestMethod.POST,RequestMethod.GET},produces="application/json;charset=UTF-8")
	 @ResponseBody
	 public Object login(@RequestParam(name="uid")String uname,@RequestParam(name="pwd")String password,@RequestParam(name="vrc_img")String aesPicCode,HttpServletRequest request){
	  respComm=new RespCommon(); 
	  String name=Aes.aesDecrypt(uname);
	  String pwd=Aes.aesDecrypt(password);
	  String picCode=Aes.aesDecrypt(aesPicCode);
	  ObjectMapper mapper = new ObjectMapper();
	  
	  String sePicCode=(String) request.getSession().getAttribute("userLogPicCode");
	  System.out.println("piccode:"+picCode+" sePiccode:"+sePicCode);
	  if(!StringUtils.equalsIgnoreCase(picCode,sePicCode)){
	   respComm.status=405;
	   respComm.msg=" 图形验证码错误！";
	  }else{
	  
	  Subject subject =SecurityUtils.getSubject();
	  UsernamePasswordToken token=new UsernamePasswordToken(name,pwd);
	  try {
	   subject.login(token);
	   respComm.status=600;
	   respComm.msg=" 登录成功！";   
	  }catch (UnknownAccountException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	   respComm.status=401;
	   respComm.msg=" 账号错误！";   
	  }catch (IncorrectCredentialsException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	   respComm.status=402;
	   respComm.msg=" 密码错误！";   
	  }catch(Exception e) {
	   respComm.status=400;
	   respComm.msg=e.getMessage();
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
	    * 图形验证码发送
	    * 
	    */
	   @RequestMapping(value = "/userLogPicCode")
	  public String UserlogPicCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
	   // 设置响应的类型格式为图片格式
	   response.setContentType("image/jpeg");
	   // 禁止图像缓存。
	   response.setHeader("Pragma", "no-cache");
	   response.setHeader("Cache-Control", "no-cache");
	   response.setDateHeader("Expires", 0);
	   HttpSession session = request.getSession();
	   PicCode picCode = new PicCode (120, 40, 4, 100);
	   session.setAttribute("userLogPicCode", picCode.getCode());
	  
	   picCode.write(response.getOutputStream());
	   return null;
	 } 
	   @RequestMapping(value = "/userRegPicCode")
	 public String userRegPicCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		   // 设置响应的类型格式为图片格式
		   response.setContentType("image/jpeg");
		   // 禁止图像缓存。
		   response.setHeader("Pragma", "no-cache");
		   response.setHeader("Cache-Control", "no-cache");
		   response.setDateHeader("Expires", 0);
		   HttpSession session = request.getSession();
		   PicCode picCode = new PicCode (120, 40, 4, 100);
		   session.setAttribute("userRegPicCode", picCode.getCode());
		   picCode.write(response.getOutputStream());
		   return null;
		 }


	 /**
	  * 短信验证码发送
	  */
	 @RequestMapping(value="/sendSMS",method={RequestMethod.POST,RequestMethod.GET},produces="application/json;charset=UTF-8")
	 @ResponseBody
	  public Object sendSMS(@RequestParam(name="mobile")String aesPhoneNumber,HttpServletRequest request){
		 respComm=new RespCommon(); 
		 ObjectMapper mapper = new ObjectMapper();
		 String phoneNumber=Aes.aesDecrypt(aesPhoneNumber);

		 Map<String,Integer> map=sendsms.sendMessage(phoneNumber);
		 int status=map.get("status");
		 String phoneCode=String.valueOf(map.get("pcode"));

		 request.getSession().setAttribute("phoneNumber", phoneNumber);
		 request.getSession().setAttribute("phoneCode", phoneCode);
		 String sePhoneNumber=(String) request.getSession().getAttribute("phoneNumber");
		 String sePhoneCode=(String) request.getSession().getAttribute("phoneCode");
		 System.out.println("status:"+status+" phoneNumber:"+sePhoneNumber+" phoneCode:"+sePhoneCode);

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
	try {
		   return mapper.writeValueAsString(respComm);
		  } catch (JsonProcessingException e) {
		   // TODO Auto-generated catch block 
		   return jacksonConvertFailJsonStr;
		  }  
		 }


		 /**
		  * 短信验证码验证
		  */
		 @RequestMapping(value="/verifySMS",method={RequestMethod.POST,RequestMethod.GET},produces="application/json;charset=UTF-8")
		 @ResponseBody
		 public Object verifySMS(@RequestParam(name="uid")String aesPhoneNumber,@RequestParam(name="vrc")String aesPhoneCode,HttpServletRequest request){
		  respComm=new RespCommon(); 
		  ObjectMapper mapper = new ObjectMapper();
		  String phoneNumber=Aes.aesDecrypt(aesPhoneNumber);
		  String phoneCode=Aes.aesDecrypt(aesPhoneCode);

		  String sePhoneNumber=(String) request.getSession().getAttribute("phoneNumber");
		  String sePhoneCode=(String) request.getSession().getAttribute("phoneCode");
		     
		     System.out.println("用户手机:"+phoneNumber+"发送手机:"+sePhoneNumber+"用户验证:"+phoneCode+"发送验证:"+sePhoneCode);
		     
		  if(phoneNumber.equals(sePhoneNumber)&phoneCode.equals(sePhoneCode)){
		   System.out.println("登录成功");
		   respComm.status=600;
		   respComm.msg=" 登录成功!";
		  }else{
		   respComm.status=401;
		   respComm.msg=" 验证码错误！";   
		  }
		  try {
		   return mapper.writeValueAsString(respComm);
		  } catch (JsonProcessingException e) {
		   // TODO Auto-generated catch block 
		   return jacksonConvertFailJsonStr;
		  }  
		 }
	 
	 }
