package com.ithcima.utils;

import java.io.IOException;
import java.util.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import org.dom4j.Document;   
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;   
import org.dom4j.Element;

public class sendsms {
	
	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
	
	public static Map<String,Integer> sendMessage(String phoneNumber) {
		
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		HttpClient client = new HttpClient(); 
		PostMethod method = new PostMethod(Url);

		client.getParams().setContentCharset("GBK");
		method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");

		int mobile_code = (int)((Math.random()*9+1 )*100000);
		map.put("pcode", mobile_code);

	    String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");

		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", "cf_blty"), //查看用户名是登录用户中心->验证码短信->产品总览->APIID
			    new NameValuePair("password", "c50cbced4cd5d27667179a70ac1a4172"),  //查看密码请登录用户中心->验证码短信->产品总览->APIKEY
			    //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
			    new NameValuePair("mobile", phoneNumber), 
			    new NameValuePair("content", content),
		};
		method.setRequestBody(data);

		try {
			client.executeMethod(method);
			
			String SubmitResult = method.getResponseBodyAsString();

			//System.out.println(SubmitResult);

			Document doc = DocumentHelper.parseText(SubmitResult);
			Element root = doc.getRootElement();

			String code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid");

			System.out.println(code);
			System.out.println(msg);
			System.out.println(smsid);

			 if("2".equals(code)){
				System.out.println("短信提交成功");
				map.put("status",600);
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("status",401);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("status",402);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("status",403);
		}
		return map;
		
	}
	
}