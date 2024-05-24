package com.ithcima.test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ithcima.domain.ComAudit;
import com.ithcima.utils.Aes;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// 
//import java.util.HashMap;
//import java.util.Map;
//
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
// 
//import com.ithcima.controller.RespCommon;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class test {
	public static void main(String[] args) throws Exception {  
        Date date=new Date();
        System.out.println(date);
        
        java.sql.Date date123 = new java.sql.Date(0, 0, 0);
        System.out.println(date123);
        
        Timestamp ctime = new java.sql.Timestamp(date.getTime());
        date = ctime;
        System.out.println(date+"");
	
	SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
	String nowTime = sdf.format(date);
	date= sdf.parse( nowTime );
	 System.out.println(date);
	 
	 String s = new String();
	 ComAudit comAudit = new ComAudit();
	 if(s.equals("")){
	 System.out.println(comAudit);}
	 
	 Object abc = new Object();
//	 if(abc==null){
		 System.out.println(abc);
//	 }
	 Integer integer = null;
	 Integer integer2 = 1;
	 System.out.println(integer);
	 
	 String emDep = "1";
		switch (emDep) {
		case "1":
			emDep = "技术部";
			break;
		case "2":
			emDep = "人事部";
			break;
		case "3":
			emDep = "销售部";
			break;
		case "4":
			emDep = "总裁办";
			break;
		}
		System.out.println(emDep);
	}
//	@Test
//	public void contextLoads() {
//	}
// 
//	private MockMvc mockMvc; // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。  
//	  
//    @Autowired  
//    private WebApplicationContext wac; // 注入WebApplicationContext  
//  
//    @Autowired  
//    private MockHttpSession session;// 注入模拟的http session  
//      
//    @Autowired  
//    private MockHttpServletRequest request;// 注入模拟的http request\  
  
//    @Before // 在测试开始前初始化工作  
//    public void setup() {  
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();  
//    }  
  
//    @Test  
//    public void testQ1() throws Exception {  
//    	Map<String, Object> map = new HashMap<>();
//    	map.put("address", "合肥");
//    	
//        MvcResult result = mockMvc.perform(post("/q1?address=合肥").content(JSONObject.toJSONString(map)))
//        		.andExpect(status().isOk())// 模拟向testRest发送get请求  
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))// 预期返回值的媒体类型text/plain;charset=UTF-8  
//                .andReturn();// 返回执行请求的结果  
//          
//        System.out.println(result.getResponse().getContentAsString());  
//    }  
//    @Test  
//    public void testPage() throws Exception {  
//    	MvcResult result = mockMvc.perform(post("/sendSMS").param("pageNo", "1").param("pageSize", "2"))
//    			.andExpect(status().isOk())// 模拟向testRest发送get请求  
//    			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
//    			.andReturn();// 返回执行请求的结果  
//    	
//    	System.out.println(result.getResponse().getContentAsString());  
//    }  
 

}