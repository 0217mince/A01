package com.ithcima.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ithcima.utils.MySessionManager;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

@Configuration
public class ShiroConfig555 {
	@Value("${spring.redis.shiro.host}")
	private String host;
	@Value("${spring.redis.shiro.port}")
	private int port;
	@Value("${spring.redis.shiro.timeout}")
	private int timeout;
	// @Value("${spring.redis.shiro.password}")
	// private String password;
	private Logger logger = LoggerFactory.getLogger(this.getClass());//生成日志文件信息
	//3.ShiroFilterFactoryBean
	@Bean
	   public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager")DefaultWebSecurityManager securityManager  ){
		   ShiroFilterFactoryBean shiroFilterFactoryBean= new ShiroFilterFactoryBean();
		   //设置安全管理器
		   shiroFilterFactoryBean.setSecurityManager(securityManager);
		/**常用shiro 权限
		   anon:无需认证就可以访问
		   authc:必须认证才能访问
		   user:必须拥有记住我功能才能使用
		   perms:拥有对某个功能的权限才能访问
		   role:拥有某个角色权限才能访问
		  */ 
		 //过滤链定定义  ，从上到下依次执行，/**通常放最后
		   Map<String,String> filterMap=new LinkedHashMap<String,String>();
		   
		   filterMap.put("/A01-Test/User_comanner/login", "anon");
		   filterMap.put("/A01-Test/User_comanner/enterpriseCertification", "anon");
		      
		   //拦截
		   
//		   filterMap.put("/add", "perms[user:add]");
//		   filterMap.put("/update", "perms[user:update]");
		   filterMap.put("/**", "authc");
		   
		   //设置登录请求
	   shiroFilterFactoryBean.setLoginUrl("/A01-Test/User_comanner/login");
		   //设置拦截跳转未授权
//		   shiroFilterFactoryBean.setUnauthorizedUrl("/noAuth");		   
		   shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
		   return shiroFilterFactoryBean;
		   
	   }
	
	//2.DefaultWebSecurityManager
	   @Bean(name="securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
		DefaultWebSecurityManager securityManager =new DefaultWebSecurityManager();
		//注入rememberme对象
		securityManager.setRememberMeManager(cookieRememberMeManager());
		//注入session
	    securityManager.setSessionManager(sessionManager());
		// //注入缓存管理对象
		securityManager.setCacheManager(redisCacheManager());
		//将Realm注入SecurityManager
		securityManager.setRealm(userRealm);
		return securityManager;
		
	}
	
	//3.创建real对象，需要自定义类
	@Bean(name="userRealm")
	public UserRealm getRealm(){
    	UserRealm userRealm = new UserRealm();
//		customerRealm.setCredentialsMatcher(credentialsMatcher());
		return new UserRealm();
	}
	
	//整合shiroDialect  整合thymeleaf shiro
	@Bean
	public  ShiroDialect getShiroDialect(){
		return new ShiroDialect();	
	}
	/*
	* z自定义sessionmanager
	* @author Innocence
	* @date 
	* @param []
	* @return org.apache.shiro.session.mgt.SessionManager
	*/
	@Bean(name = "sessionManager")
	public SessionManager sessionManager(){

	MySessionManager mySessionManager = new MySessionManager();
	mySessionManager.setSessionIdUrlRewritingEnabled(false); //取消登陆跳转URL后面的jsessionid参数
	mySessionManager.setSessionDAO(redisSessionDAO());
	mySessionManager.setGlobalSessionTimeout(-1);//不过期
	return mySessionManager;		

}
	/*
     * 配置shiro redisManager
     * 使用shiro-redis开源插件
     * @author Innocence
     * @date 
     * @param []
     * @return org.crazycake.shiro.RedisManager
     */
    @Bean(name = "redisManager")
    public RedisManager redisManager(){
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host+":"+port);
        redisManager.setTimeout(timeout);
//        redisManager.setPassword(password);
        return redisManager;
    }
 
    /*
     * cacheManager的Redis实现
     * @author Innocence
     * @date 
     * @param []
     * @return org.crazycake.shiro.RedisCacheManager
     */
    @Bean(name = "redisCacheManager")
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }
 
    /*
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用shiro-redis开源插件
     * @author Innocence
     * @date 
     * @param []
     * @return org.crazycake.shiro.RedisSessionDAO
     */
    @Bean(name = "redisSessionDAO")
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }
    /*
    * 开启shiro 的aop注解支持
    * @author Innocence
    * @date 
    * @param [securityManager]
    * @return org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
    */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    /*
    * cookie对象
    * @author Innocence
    * @date 
    * @return org.apache.shiro.web.servlet.SimpleCookie
    */
    @Bean
    public SimpleCookie rememberMeCookie() {
        logger.info("ShiroConfiguration.rememberMeCookie()=============");
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        simpleCookie.setMaxAge(259200);
        return simpleCookie;
    }
 
    /*
    * cookie管理对象
    * @author Innocence
    * @date 
    * @return org.apache.shiro.web.mgt.CookieRememberMeManager
    */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        logger.info("ShiroConfiguration.rememberMeManager()========");
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCookie(rememberMeCookie());
        return manager;
    }
 
 
    /*
    * 加入下面两个bean，shiro才会执行授权逻辑
    * @author Innocence
    * @date 
    * @param []
    * @return org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
    */
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
 
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }
}

	