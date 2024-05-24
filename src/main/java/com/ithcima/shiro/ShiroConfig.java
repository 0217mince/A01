package com.ithcima.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;


public class ShiroConfig {
    
             
	@Bean
	   public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager  ){
		   ShiroFilterFactoryBean shiroFilterFactoryBean= new ShiroFilterFactoryBean();
		  
		   shiroFilterFactoryBean.setSecurityManager(securityManager);

		   Map<String,String> filterMap=new LinkedHashMap<String,String>();
		   
//		   filterMap.put("/sendSMS", "anon");
//		   filterMap.put("/verifySMS", "anon");
		   filterMap.put("/tologin", "anon");
		   filterMap.put("/testThymeleaf", "anon");
		   filterMap.put("/login", "anon");
		   
		   

//		   filterMap.put("/add", "perms[user:add]");
//		   filterMap.put("/update", "perms[user:update]");
//		   filterMap.put("/*", "authc");
		   

//		   shiroFilterFactoryBean.setLoginUrl("/login");

//		   shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");
		   
		   shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
		   return shiroFilterFactoryBean;
		   
	   }
	
	
	

	   @Bean(name="securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
		DefaultWebSecurityManager securityManager =new DefaultWebSecurityManager();
		securityManager.setRealm(userRealm);
		return securityManager;
		
	}
	
	
	

	@Bean(name="userRealm")
	public UserRealm getRealm(){
//		UserRealm customerRealm = new UserRealm();
//		customerRealm.setCredentialsMatcher(credentialsMatcher());
		return new UserRealm();
	}

	@Bean
	public  ShiroDialect getShiroDialect(){
		return new ShiroDialect();
		
	}
		

}
