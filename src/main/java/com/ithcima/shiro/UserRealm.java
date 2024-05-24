package com.ithcima.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;


import com.ithcima.domain.User;
import com.ithcima.service.UserService;


public class UserRealm extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {	
		System.out.println("ִ111");
		
		SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
		
		//info.addStringPermission("user:add");
		
		Subject  subject=SecurityUtils.getSubject();
		User user= (User) subject.getPrincipal();
		User dbuser=userService.findById(user.getId());
		 info.addStringPermission(dbuser.getPerms());
		return info;
	
	}

	@Autowired
	private UserService userService;
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
			System.out.println("222");
		
	        UsernamePasswordToken token=(UsernamePasswordToken)arg0;
	        User user= userService.findByName(token.getUsername());
          
	      if(user==null){
	    	  	return null;
	      }
	      	Subject currentsubject =SecurityUtils.getSubject();
			Session session=currentsubject.getSession();
	        session.setAttribute("loginUser", user);
	    
	        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
	     
	}
}
