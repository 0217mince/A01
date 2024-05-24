package com.ithcima.utils;

import java.io.Serializable;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

/**
* @ClassName MySessionManager
* @Description 自定义的session获取
* @Author Innocence
**/
public class MySessionManager extends DefaultWebSessionManager {
  private static final String AUTHORIZATION = "Authorization";
  private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";
 
public MySessionManager(){
  super();
  }

@Override

protected Serializable getSessionId(ServletRequest request, ServletResponse response){
   String id = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
//如果请求头中有AUTHORIZATION，其值为sessionid
   if (!StringUtils.isBlank(id)){
   request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
   request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
   request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
   return id;
}
//否则按默认规则从cookie取sessionId
return super.getSessionId(request, response);

}

}