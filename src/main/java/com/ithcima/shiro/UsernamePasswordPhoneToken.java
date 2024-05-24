package com.ithcima.shiro;

import java.io.Serializable;
 
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
 
public class UsernamePasswordPhoneToken extends UsernamePasswordToken implements Serializable {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 4812793519945855483L;
 
	// 手机号码
	private String phoneNum;
 
	/**
	 * 重写getPrincipal方法
	 */
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		// 如果获取到用户名，则返回用户名，否则返回电话号码
		if (phoneNum == null) {
			return getUsername();
		} else {
			return getTelphoneNum();
		}
	}
 
	/**
	 * 重写getCredentials方法
	 */
	public Object getCredentials() {
		// TODO Auto-generated method stub
		// 如果获取到密码，则返回密码，否则返回null
		if (phoneNum == null) {
			return getPassword();
		} else {
			return "ok";
		}
	}
 
	public UsernamePasswordPhoneToken() {
		// TODO Auto-generated constructor stub
	}
 
	public UsernamePasswordPhoneToken(final String telphoneNum) {
		// TODO Auto-generated constructor stub
		this.phoneNum = telphoneNum;
	}
 
	public UsernamePasswordPhoneToken(final String username, final String password) {
		// TODO Auto-generated constructor stub
		super(username, password);
	}
 
	public String getTelphoneNum() {
		return phoneNum;
	}
 
	public void setTelphoneNum(String telphoneNum) {
		this.phoneNum = telphoneNum;
	}
 
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
 
	@Override
	public String toString() {
		return "TelphoneToken [telphoneNum=" + phoneNum + "]";
	}
 
}