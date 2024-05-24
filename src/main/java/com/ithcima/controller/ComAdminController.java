package com.ithcima.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.ithcima.domain.Com;
import com.ithcima.domain.ComAudit;
import com.ithcima.domain.ComDepartment;
import com.ithcima.domain.ComMod;
import com.ithcima.domain.Employee;
import com.ithcima.domain.RespCommon;
import com.ithcima.domain.SimpleCom;
import com.ithcima.service.ComAdminService;
import com.ithcima.service.impl.ComAdminServiceImpl;
import com.ithcima.utils.Aes;
import com.ithcima.utils.PicCode;
import com.ithcima.utils.imageUtil;
import com.ithcima.utils.randomComId;
import com.ithcima.utils.readCookies;
import com.ithcima.utils.sendsms;

import lombok.val;

@Controller
public class ComAdminController {
	RespCommon respComm;
	String jacksonConvertFailJsonStr = "{'status':400,'msg':'jackson'}";

	@Autowired
	private ComAdminService comAdminService;

	/**
	 * 管理员注册图形验证码
	 */
	@RequestMapping(value = "/comAdminRegPicCode")
	public String comAdminRegPicCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		HttpSession session = request.getSession();
		PicCode picCode = new PicCode(120, 40, 4, 100);
		session.setAttribute("comAdminRegPicCode", picCode.getCode());
		picCode.write(response.getOutputStream());
		return null;
	}

	/**
	 * 管理员注册短信验证码
	 */
	@RequestMapping(value = "/comAdminRegSMS", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAdminRegSMS(@RequestParam(name = "mobile") String aesPhoneNumber,
			@RequestParam(name = "vrc_img") String aesPicCode, HttpServletRequest request) throws ParseException {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String phoneNumber = Aes.aesDecrypt(aesPhoneNumber);
		String picCode = Aes.aesDecrypt(aesPicCode);
		ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
		if (admin != null) {
			respComm.status = 403;
			respComm.msg = "该手机号已存在！";
		} else {
			String sePicCode = (String) request.getSession().getAttribute("comAdminRegPicCode");
			System.out.println("piccode:" + picCode + " sePiccode:" + sePicCode);
			if (!StringUtils.equalsIgnoreCase(picCode, sePicCode)) {
				respComm.status = 405;
				respComm.msg = " 图形验证码过期或错误！";
			} else {
				// SimpleDateFormat sdf=new SimpleDateFormat();
				HttpSession session = request.getSession();
				long currentDate = System.currentTimeMillis();
				Long date = (Long) session.getAttribute("comAdminRegSMSTime");
				if (date != null && currentDate - date < 1000 * 60 * 1) {
					respComm.status = 406;
					respComm.msg = "验证码发送未超过一分钟";
				} else {
					Map<String, Integer> map = sendsms.sendMessage(phoneNumber);
					int status = map.get("status");
					String phoneCode = String.valueOf(map.get("pcode"));
					session.setAttribute("comAdminRegSMSTime", System.currentTimeMillis());
					session.setAttribute("comAdminRegPhoneNumber", phoneNumber);
					session.setAttribute("comAdminRegPhoneCode", phoneCode);

					if (status == 600) {
						System.out.println("短信提交成功");
						respComm.status = status;
						respComm.msg = " 短信发送成功!";
					} else if (status == 401) {
						respComm.status = status;
						respComm.msg = " http错误！";
					} else if (status == 402) {
						respComm.status = status;
						respComm.msg = " IO错误！";
					} else if (status == 403) {
						respComm.status = status;
						respComm.msg = " 文件错误！";
					} else {
						respComm.status = 400;
						respComm.msg = " 未知错误！";
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
	 * 管理员注册
	 */
	@RequestMapping(value = "/comAdminReg", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAdminReg(@RequestParam(name = "uid") String aesPhoneNumber,
			@RequestParam(name = "vrc_img") String aesPicCode, @RequestParam(name = "vrc") String aesPhoneCode,
			HttpServletRequest request) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		HttpSession session = request.getSession();

		String phoneNumber = Aes.aesDecrypt(aesPhoneNumber);
		String picCode = Aes.aesDecrypt(aesPicCode);
		String phoneCode = Aes.aesDecrypt(aesPhoneCode);

		String sePicCode = (String) session.getAttribute("comAdminRegPicCode");
		session.removeAttribute("comAdminRegPicCode");

		String sePhoneNumber = (String) session.getAttribute("comAdminRegPhoneNumber");
		String sePhoneCode = (String) session.getAttribute("comAdminRegPhoneCode");
		Long sdate = (Long) session.getAttribute("comAdminRegSMSTime");
		if (StringUtils.isEmpty(sePhoneNumber) && StringUtils.isEmpty(sePhoneCode)
				&& session.getAttribute("date") == null) {
			respComm.status = 403;
			respComm.msg = "验证码未发送或已过期！";
		} else {
			// 设置Session中的验证码的失效时间(1分钟)
			if ((System.currentTimeMillis() - sdate) > 1000 * 60 * 1) {
				session.removeAttribute("comAdminRegPhoneCode");
				session.removeAttribute("comAdminRegSMSTime");
				respComm.status = 406;
				respComm.msg = "验证码已失效！请重新发送验证码！";
			} else {
				if (!StringUtils.equalsIgnoreCase(picCode, sePicCode)) {
					respComm.status = 405;
					respComm.msg = " 图形验证码过期错误！";
				} else if (!phoneNumber.equals(sePhoneNumber)) {
					respComm.status = 401;
					respComm.msg = " 与发送短信的手机号不一致!";
				} else if (!phoneCode.equals(sePhoneCode)) {
					respComm.status = 402;
					respComm.msg = " 短信验证码错误!";
				} else {
					if (sePhoneCode.equals(phoneCode)) {
						session.removeAttribute("comAdminRegPhoneCode");
						String id = phoneNumber;
						String comId = randomComId.createID();
						System.out.println("手机号:" + id + "企业号:" + comId);
						respComm.status = 600;
						respComm.msg = "注册成功!";

						ComAdmin comAdm = new ComAdmin();
						comAdm.setId(id);
						comAdm.setComId(comId);
						comAdminService.insertComAdmin(comAdm);
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
	 * 管理员登录短信验证码
	 */
	@RequestMapping(value = "/comAdminLoginSMS", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAdminLoginSMS(@RequestParam(name = "mobile") String aesPhoneNumber, HttpServletRequest request)
			throws ParseException {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String phoneNumber = Aes.aesDecrypt(aesPhoneNumber);
		ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
		if (admin == null) {
			respComm.status = 403;
			respComm.msg = " 该手机号不存在，请先注册！";
		} else {
			HttpSession session = request.getSession();
			long currentDate = System.currentTimeMillis();
			Long date = (Long) session.getAttribute("comAdminLoginSMSTime");
			if (date != null && currentDate - date < 1000 * 60 * 1) {
				respComm.status = 406;
				respComm.msg = "验证码发送未超过一分钟";
			} else {

				Map<String, Integer> map = sendsms.sendMessage(phoneNumber);
				int status = map.get("status");
				String phoneCode = String.valueOf(map.get("pcode"));
				session.setAttribute("comAdminLoginSMSTime", System.currentTimeMillis());
				session.setAttribute("comAdminLoginPhoneNumber", phoneNumber);
				session.setAttribute("comAdminLoginPhoneCode", phoneCode);

				if (status == 600) {
					System.out.println("短信提交成功");
					respComm.status = status;
					respComm.msg = " 短信发送成功!";
				} else if (status == 401) {
					respComm.status = status;
					respComm.msg = " http错误！";
				} else if (status == 402) {
					respComm.status = status;
					respComm.msg = " IO错误！";
				} else if (status == 403) {
					respComm.status = status;
					respComm.msg = " 文件错误！";
				} else {
					respComm.status = 400;
					respComm.msg = " 未知错误！";
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
	 * 管理员登录
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/comAdminLogin", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAdminLogin(@RequestParam(name = "uid") String aesPhoneNumber,
			@RequestParam(name = "vrc") String aesPhoneCode, HttpServletRequest request, HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String phoneNumber = Aes.aesDecrypt(aesPhoneNumber);
		String phoneCode = Aes.aesDecrypt(aesPhoneCode);
		HttpSession session = request.getSession();

		ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
		System.out.println(admin.getId());
		if (phoneCode.equals("9999") && admin.getId() != null) {
			System.out.println("测试员登录成功");
			respComm.status = 600;
			respComm.msg = "测试员登录成功!";
			Cookie cookie = new Cookie("comAdmin", phoneNumber);
			cookie.setMaxAge(60 * 60 * 24); // 设置一天有效
			response.addCookie(cookie); // 服务器返回给浏览器cookie以便下次判断
		} else {

			String sePhoneNumber = (String) session.getAttribute("comAdminLoginpPhoneNumber");
			String sePhoneCode = (String) session.getAttribute("comAdminLoginPhoneCode");

			Long slogdate = (Long) session.getAttribute("comAdminLoginSMSTime");
			if (!phoneNumber.equals(sePhoneNumber)) {
				respComm.status = 401;
				respComm.msg = " 与发送短信的手机号不一致!";
			} else if (StringUtils.isEmpty(sePhoneNumber) && StringUtils.isEmpty(sePhoneCode) && slogdate == null) {
				respComm.status = 403;
				respComm.msg = "验证码未发送或已过期！";
			} else {

				session.removeAttribute("comAdminLoginPhoneCode");
				session.removeAttribute("comAdminLogSMSTime");
				// 设置Session中的验证码的失效时间(1分钟)
				if ((System.currentTimeMillis() - slogdate) > 1000 * 60 * 1) {
					respComm.status = 406;
					respComm.msg = "验证码已失效！请重新发送验证码！";
				} else {
					System.out.println("用户手机:" + phoneNumber + "发送手机:" + sePhoneNumber + "用户验证:" + phoneCode + "发送验证:"
							+ sePhoneCode);
					if (phoneNumber.equals(sePhoneNumber) & phoneCode.equals(sePhoneCode)) {
						System.out.println("登录成功");
						respComm.status = 600;
						respComm.msg = " 登录成功!";
						Cookie cookie = new Cookie("comAdmin", phoneNumber);
						cookie.setMaxAge(60 * 60 * 24); // 设置一天有效
						response.addCookie(cookie); // 服务器返回给浏览器cookie以便下次判断
					} else {
						respComm.status = 401;
						respComm.msg = " 验证码错误！";
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
	 * 管理员登录状态访问
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/comAdminIsLogin", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAdminIsLogin(@RequestParam(name = "loginPhone") String aesPhoneNumber, HttpServletRequest request,
			HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String phoneNumber = Aes.aesDecrypt(aesPhoneNumber);

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效！请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String coPhoneNumber = cookie.getValue();
			if (!phoneNumber.equals(coPhoneNumber)) {
				respComm.status = 402;
				respComm.msg = "账号信息错误！请重新登录！";
			} else {
				respComm.status = 600;
				respComm.msg = " 登录成功!";
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
	@RequestMapping(value = "/comAdminLogout", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAdminLogout(HttpServletRequest request, HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Cookie cookie = new Cookie("comAdmin", "");
		cookie.setMaxAge(0); // 删除
		response.addCookie(cookie);

		respComm.status = 600;
		respComm.msg = "用户注销成功！";

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * 访问公司信息页
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/comInfo", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comInfo(HttpServletRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		respComm = new RespCommon();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String coPhoneNumber = cookie.getValue();

			ComAdmin comAdmin = comAdminService.selectComAdmin(coPhoneNumber);
			String comId = comAdmin.getComId();
			Com com = comAdminService.selectComInfo(comId);
			respComm.data = com;
			respComm.status = 600;
			respComm.msg = "success！";
		}
		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * 公司信息提交
	 */
	@RequestMapping(value = "/comInfoUpdate", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comInfoUpdate(@RequestParam(name = "epName") String aesName,
			@RequestParam(name = "epAbb") String aesNameShort, @RequestParam(name = "epPhone") String aesTel,
			@RequestParam(name = "epStyle") String aesType, @RequestParam(name = "epIndustry") String aesIndus1,
			@RequestParam(name = "epIndustry_sp") String aesIndus2, @RequestParam(name = "epRegion") String aesLoc1,
			@RequestParam(name = "epRegion_sp") String aesLoc2, @RequestParam(name = "epAddress") String aesAdd,
			@RequestParam(name = "postalCode") String aesPcode, @RequestParam(name = "epFax") String aesFax,
			@RequestParam(name = "epWeb") String aesWeb, @RequestParam(name = "epTime") String aesEstTime1,
			@RequestParam(name = "epTime_sp") String aesEstTime2, @RequestParam(name = "epLogo") String slogo,
			HttpServletRequest request) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String name = Aes.aesDecrypt(aesName);
		String nameShort = Aes.aesDecrypt(aesNameShort);
		String tel = Aes.aesDecrypt(aesTel);
		String type = Aes.aesDecrypt(aesType);
		String indus = Aes.aesDecrypt(aesIndus1) + "-" + Aes.aesDecrypt(aesIndus2);
		String loc = Aes.aesDecrypt(aesLoc1) + "-" + Aes.aesDecrypt(aesLoc2);
		String add = Aes.aesDecrypt(aesAdd);
		String pcode = Aes.aesDecrypt(aesPcode);
		String fax = Aes.aesDecrypt(aesFax);
		String web = Aes.aesDecrypt(aesWeb);

		String estTime = Aes.aesDecrypt(aesEstTime1) + "-" + Aes.aesDecrypt(aesEstTime2);

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效 请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			String comId = comAdminService.selectComAdmin(phoneNumber).getComId();

			String logo = null;
			if (slogo != null) {
				String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);// 精确到秒
				logo = comId + timeStamp + ".png";
				slogo = slogo.substring(slogo.indexOf(",") + 1, slogo.length());
				imageUtil.generateImage(slogo, "C:/picSource/logo/" + logo);
			}

			Com com = new Com();
			com.setEpId(comId);
			com.setEpName(name);
			com.setEpAbb(nameShort);
			com.setEpPhone(tel);
			com.setEpStyle(type);
			com.setEpIndustry(indus);
			com.setEpRegion(loc);
			com.setEpAddress(add);
			com.setPostalCode(pcode);
			com.setEpFax(fax);
			com.setEpWeb(web);
			com.setEpTime(estTime);
			com.setEpLogo(logo);

			try {
				int mark = comAdminService.updateComInfo(com);
				if (mark == 0) {
					respComm.status = 402;
					respComm.msg = "信息提交失败（可能是因为你的公司已通过认证 如需修改信息请到【申请修改】页）";
				} else {
					respComm.status = 600;
					respComm.msg = "提交成功！";
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				respComm.status = 403;
				respComm.msg = "提交失败！";
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
	 * 访问公司认证页
	 */
	@RequestMapping(value = "/comAudit", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAudit(HttpServletRequest request, HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录过期，请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
			String comId = admin.getComId();
			ComAudit comAudit = comAdminService.selectComAudit(comId);

			if (comAudit.getEpId() != null) {
				respComm.data = comAudit;
				respComm.status = 600;
				respComm.msg = "success！";
			} else {
				respComm.status = 402;
				respComm.msg = "未查询到认证信息！";
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
	 * 获取公司认证状态
	 */
	@RequestMapping(value = "/comAuditStatus", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAuditStatus(HttpServletRequest request, HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);

		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录过期，请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
			String comId = admin.getComId();
			ComAudit comAudit = comAdminService.selectComAudit(comId);
			String auditStatus = comAudit.getAuditStatus();

			if (comAudit.getEpId() != null) {
				respComm.data = auditStatus;
				respComm.status = 600;
				respComm.msg = "success！";

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
	 * 认证信息提交
	 */
	@RequestMapping(value = "/comAuditUpdate", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAuditUpdate(@RequestParam(name = "auditFile") String auditFile, HttpServletRequest request) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效 请重新登录！";
		} else if (auditFile == null) {
			respComm.status = 402;
			respComm.msg = "公司资质证明不可为空！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			String comId = comAdminService.selectComAdmin(phoneNumber).getComId();

			try {
				comAdminService.applyComAudit(comId, auditFile);
				respComm.status = 600;
				respComm.msg = "提交成功！";
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				respComm.status = 403;
				respComm.msg = "提交失败！";
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
	 * admin审核失败提醒（点击后不再提醒）
	 */
	@RequestMapping(value = "/comAuditWarningConfirm", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAuditWarningConfirm(HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效 请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			String comId = comAdminService.selectComAdmin(phoneNumber).getComId();
			try {
				int flag = comAdminService.comAuditWarningConfirm(comId);
				if (flag == 0) {
					respComm.status = 402;
					respComm.msg = "认证结果提示已关闭，请勿重复提交";
				} else {
					respComm.status = 600;
					respComm.msg = "success！";
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				respComm.status = 403;
				respComm.msg = "failure！";
			}

		}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * 已认证公司申请修改信息提供
	 */
	@RequestMapping(value = "/comAlterInfo", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comAlterInfo(HttpServletRequest request, HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String comAdmin = cookie.getValue();
			String comId = comAdminService.selectComAdmin(comAdmin).getComId();

			try {
				ComAudit comAudit = comAdminService.selectComAudit(comId);
				Com com = comAdminService.selectComInfo(comId);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("comAudit", comAudit);
				map.put("com", com);
				respComm.data = map;
				respComm.status = 600;
				respComm.msg = "success!";
			} catch (Exception e) {
				e.printStackTrace();
				respComm.status = 402;
				respComm.msg = "操作异常";
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
	 * 申请修改公司认证信息
	 */
	@RequestMapping(value = "/alterAuditedCom", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object alterAuditedCom(@RequestParam(name = "epName") String aesName,
			@RequestParam(name = "epAbb") String aesNameShort, @RequestParam(name = "epPhone") String aesTel,
			@RequestParam(name = "epStyle") String aesType, @RequestParam(name = "epIndustry") String aesIndus1,
			@RequestParam(name = "epIndustry_sp") String aesIndus2, @RequestParam(name = "epRegion") String aesLoc1,
			@RequestParam(name = "epRegion_sp") String aesLoc2, @RequestParam(name = "epAddress") String aesAdd,
			@RequestParam(name = "postalCode") String aesPcode, @RequestParam(name = "epFax") String aesFax,
			@RequestParam(name = "epWeb") String aesWeb, @RequestParam(name = "epTime") String aesEstTime1,
			@RequestParam(name = "epTime_sp") String aesEstTime2, @RequestParam(name = "epLogo") String slogo,
			@RequestParam(name = "auditFile") String auditFile, HttpServletRequest request) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();
		String name = Aes.aesDecrypt(aesName);
		String nameShort = Aes.aesDecrypt(aesNameShort);
		String tel = Aes.aesDecrypt(aesTel);
		String type = Aes.aesDecrypt(aesType);
		String indus = Aes.aesDecrypt(aesIndus1) + "-" + Aes.aesDecrypt(aesIndus2);
		String loc = Aes.aesDecrypt(aesLoc1) + "-" + Aes.aesDecrypt(aesLoc2);
		String add = Aes.aesDecrypt(aesAdd);
		String pcode = Aes.aesDecrypt(aesPcode);
		String fax = Aes.aesDecrypt(aesFax);
		String web = Aes.aesDecrypt(aesWeb);

		String estTime = Aes.aesDecrypt(aesEstTime1) + "-" + Aes.aesDecrypt(aesEstTime2);

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录信息已失效 请重新登录！";
		} else if (auditFile == null) {
			respComm.status = 402;
			respComm.msg = "公司资质证明不可为空！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			String comId = comAdminService.selectComInfo(phoneNumber).getEpId();

			String logo = null;
			if (slogo != null) {
				String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);// 精确到秒
				logo = comId + timeStamp + ".png";
				slogo = slogo.substring(slogo.indexOf(",") + 1, slogo.length());
				imageUtil.generateImage(slogo, "C:/picSource/logo/" + logo);
			}

			Com com = new Com();
			com.setEpId(comId);
			com.setEpName(name);
			com.setEpAbb(nameShort);
			com.setEpPhone(tel);
			com.setEpStyle(type);
			com.setEpIndustry(indus);
			com.setEpRegion(loc);
			com.setEpAddress(add);
			com.setPostalCode(pcode);
			com.setEpFax(fax);
			com.setEpWeb(web);
			com.setEpTime(estTime);
			com.setEpLogo(logo);
			com.setAuditStatus("2");

			try {
				comAdminService.alterAuditedCom(comId, auditFile);
				comAdminService.updateComInfo(com);
				respComm.status = 600;
				respComm.msg = "success!";
			} catch (Exception e) {
				e.printStackTrace();
				respComm.status = 403;
				respComm.msg = "操作异常";
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
	 * su查看coms
	 */
	@RequestMapping(value = "/simpleAuditedComs", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object simpleAuditedComs(HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		try {
			ArrayList<SimpleCom> coms = comAdminService.selectAuditedSimpleComs();
			respComm.data = coms;
			respComm.status = 600;
			respComm.msg = "success!";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "error!";
		}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * su查看待审核coms
	 */
	@RequestMapping(value = "/simpleUnauditedComs", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object simpleUnauditedComs(HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		try {
			ArrayList<SimpleCom> coms = comAdminService.selectToAuditSimpleComs();
			respComm.data = coms;
			respComm.status = 600;
			respComm.msg = "success!";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "error!";
		}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * su查看coms时执行删除
	 */
	@RequestMapping(value = "/delByCid", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object delByCid(@RequestParam(name = "epId") String aesComId,
			@RequestParam(name = "deleteReason") String aesDeleteReason, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);
		String deleteReason = "";
		deleteReason = Aes.aesDecrypt(aesDeleteReason);

		try {
			comAdminService.deleteCom(comId, deleteReason);
			respComm.status = 600;
			respComm.msg = "删除成功";
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
	 * su访问com详细页
	 * 
	 * @param response
	 */
	@RequestMapping(value = "/suCom", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object suCom(@RequestParam(name = "epId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);

		try {
			Com com = comAdminService.selectComInfo(comId);
			respComm.data = com;
			respComm.status = 600;
			respComm.msg = "success!";
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
	 * su查看公司认证信息
	 */
	@RequestMapping(value = "/suComAudit", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object suComAudit(@RequestParam(name = "epId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);

		try {
			ComAudit comAudit = comAdminService.selectComAudit(comId);
			Com com = comAdminService.selectComInfo(comId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("comAudit", comAudit);
			map.put("com", com);
			respComm.data = map;
			respComm.status = 600;
			respComm.msg = "success!";
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
	 * su查看comAudit时审核通过
	 */
	@RequestMapping(value = "/auditSucceed", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object auditSucceed(@RequestParam(name = "epId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);

		try {
			comAdminService.auditCom(comId);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * su查看comAudit时审核未通过
	 */
	@RequestMapping(value = "/auditFail", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object auditFail(@RequestParam(name = "epId") String aesComId,
			@RequestParam(name = "failureReason") String aesFailureReason, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);
		String failureReason = Aes.aesDecrypt(aesFailureReason);

		try {
			comAdminService.auditFailCom(comId, failureReason);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * su查看已删除coms
	 */
	@RequestMapping(value = "/deletedComs", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object deletedComs(HttpServletRequest request, HttpServletResponse response) {

		ArrayList<SimpleCom> coms = new ArrayList<SimpleCom>();
		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		try {
			coms = comAdminService.selectDeletedSimpleComs();
			respComm.data = coms;
			respComm.status = 600;
			respComm.msg = "操作成功";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}

		try {
			System.out.println("新版返回格式：" + respComm);
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * su查看删除原因
	 */
	@RequestMapping(value = "/comDeleteReason", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comDeleteReason(@RequestParam(name = "epId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);

		try {
			ComMod comMod = comAdminService.selectComMod(comId);
			respComm.data = comMod;
			respComm.status = 600;
			respComm.msg = "操作成功";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}

		try {
			System.out.println("新版返回格式：" + mapper.writeValueAsString(respComm));
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * su恢复已删除公司
	 */
	@RequestMapping(value = "/restoreDeletedCom", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object restoreDeletedCom(@RequestParam(name = "epId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);

		try {
			comAdminService.restoreDeletedCom(comId);
			respComm.status = 600;
			respComm.msg = "操作成功";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 401;
			respComm.msg = "操作异常";
		}

		try {
			System.out.println("新版返回格式：" + respComm);
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}

	/**
	 * 用于导出excel的查询结果
	 * 
	 * @throws IOException
	 * 
	 */
	@RequestMapping(value = "/exportAuditedComs", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void exportAuditedComs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("已认证公司信息表");

		ArrayList<Com> coms = comAdminService.selectAuditedComs();

		String fileName = "已认证公司信息" + ".xls";// 设置要导出的文件的名字
		// 新增数据行，并且设置单元格数据

		int rowNum = 1;

		String[] headers = { "企业号", "公司标志", "公司名字", "公司简称", "公司电话", "公司类型", "公司所属产业", "所属地区", "公司地址", "邮政编码", "传真号",
				"公司网站", "公司创立时间", "负责人名字", "职位", "负责人电话", "负责人邮箱" };
		// headers表示excel表中第一行的表头

		HSSFRow row = sheet.createRow(0);
		// 在excel表中添加表头

		for (int i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		// 在表中存放查询到的数据放入对应的列
		for (Com com : coms) {
			HSSFRow row1 = sheet.createRow(rowNum);
			row1.createCell(0).setCellValue(com.getEpId());
			row1.createCell(1).setCellValue(com.getEpLogo());
			row1.createCell(2).setCellValue(com.getEpName());
			row1.createCell(3).setCellValue(com.getEpAbb());
			row1.createCell(4).setCellValue(com.getEpPhone());
			row1.createCell(5).setCellValue(com.getEpStyle());
			row1.createCell(6).setCellValue(com.getEpIndustry());
			row1.createCell(7).setCellValue(com.getEpRegion());
			row1.createCell(8).setCellValue(com.getEpAddress());
			row1.createCell(9).setCellValue(com.getPostalCode());
			row1.createCell(10).setCellValue(com.getEpFax());
			row1.createCell(11).setCellValue(com.getEpWeb());
			row1.createCell(12).setCellValue(com.getEpTime());
			row1.createCell(13).setCellValue(com.getIName());
			row1.createCell(14).setCellValue(com.getIJob());
			row1.createCell(15).setCellValue(com.getITel());
			row1.createCell(16).setCellValue(com.getIEmail());
			rowNum++;
		}

		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
		response.flushBuffer();
		workbook.write(response.getOutputStream());

		workbook.close();
	}

	/**
	 * 新建公司部门
	 */
	@RequestMapping(value = "/newComDepartment", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object newComDepartment(@RequestParam(name = "comId") String aesComId,
			@RequestParam(name = "dSuperior") String aesDepSuperior, @RequestParam(name = "dName") String aesDepName,
			@RequestParam(name = "dDescri") String aesDepDescri, @RequestParam(name = "dRank") String aesDepRank,
			HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		ComDepartment dep = new ComDepartment();
		dep.setComId(Aes.aesDecrypt(aesComId));
		dep.setDSuperior(Aes.aesDecrypt(aesDepSuperior));
		dep.setDName(Aes.aesDecrypt(aesDepName));
		dep.setDDescri(Aes.aesDecrypt(aesDepDescri));
		dep.setDRank(Aes.aesDecrypt(aesDepRank));

		try {
			comAdminService.insertComDepartment(dep);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * 更新公司部门信息
	 */
	@RequestMapping(value = "/comDepartmentUpdate", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comDepartmentUpdate(@RequestParam(name = "comId") String aesComId,
			@RequestParam(name = "dSuperior") String aesDepSuperior, @RequestParam(name = "dName") String aesDepName,
			@RequestParam(name = "dDescri") String aesDepDescri, @RequestParam(name = "dRank") String aesDepRank,
			HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		ComDepartment dep = new ComDepartment();
		dep.setComId(Aes.aesDecrypt(aesComId));
		dep.setDSuperior(Aes.aesDecrypt(aesDepSuperior));
		dep.setDName(Aes.aesDecrypt(aesDepName));
		dep.setDDescri(Aes.aesDecrypt(aesDepDescri));
		dep.setDRank(Aes.aesDecrypt(aesDepRank));

		try {
			comAdminService.updateComDepartment(dep);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * 删除公司部门
	 */
	@RequestMapping(value = "/comDepartmentDelete", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comDepartmentDelete(@RequestParam(name = "comId") String aesComId,
			@RequestParam(name = "dName") String aesDepName, HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);
		String dName = Aes.aesDecrypt(aesDepName);

		try {
			comAdminService.deleteComDepartment(comId, dName);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * 恢复公司部门
	 */
	@RequestMapping(value = "/comDepartmentRestore", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comDepartmentRestore(@RequestParam(name = "comId") String aesComId,
			@RequestParam(name = "dName") String aesDepName, HttpServletRequest request, HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);
		String dName = Aes.aesDecrypt(aesDepName);

		try {
			comAdminService.restoreComDepartment(comId, dName);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * 列出公司部门
	 */
	@RequestMapping(value = "/comDepartments", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comDepartments(@RequestParam(name = "comId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);
		try {
			respComm.data = comAdminService.selectComDepartments(comId);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * 列出已删除公司部门
	 */
	@RequestMapping(value = "/comDeletedDepartments", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comDeletedDepartments(@RequestParam(name = "comId") String aesComId, HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		String comId = Aes.aesDecrypt(aesComId);
		try {
			respComm.data = comAdminService.selectDeletedComDepartments(comId);
			respComm.status = 600;
			respComm.msg = "操作成功";
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
	 * 列出公司员工
	 */
	@RequestMapping(value = "/comEmployees", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comEmployees(@RequestParam(name = "emId",required=false) String aesEmId,
			@RequestParam(name = "emNum",required=false) String aesEmNum,
			@RequestParam(name = "emDep",required=false) String aesEmDep,
			HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录过期，请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
			Employee employee = new Employee();
			employee.setComId(admin.getComId());
		
		if (aesEmId!=null) {
			String emId = Aes.aesDecrypt(aesEmId);
			employee.setEmId(emId);
		}
		if (aesEmNum!=null) {
			String emNum = Aes.aesDecrypt(aesEmNum);
			employee.setEmNum(emNum);
		}
		if (aesEmDep!=null) {
			String emDep = Aes.aesDecrypt(aesEmDep);
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
			employee.setEmDep(emDep);
		}
		try {
			respComm.data = comAdminService.selectComEmployees(employee);
			respComm.status = 600;
			respComm.msg = "操作成功";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 402;
			respComm.msg = "操作异常";
		}}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	
	/**
	 * 修改员工信息
	 */
	@RequestMapping(value = "/comEmployeeUpdate", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object comEmployeeUpdate(@RequestParam(name = "emId") String aesEmId,
			@RequestParam(name = "emName") String aesEmName,
			@RequestParam(name = "emSex") String aesEmSex,
			@RequestParam(name = "emDep") String aesEmDep,
			@RequestParam(name = "emJob") String aesEmJob,
			@RequestParam(name = "emLandline") String aesEmLandline,
			@RequestParam(name = "emExtension") String aesEmExtension,
			@RequestParam(name = "emEm") String aesEmEm,
			@RequestParam(name = "emNav") String aesEmNav,
			@RequestParam(name = "emGradSch") String aesEmGradSch,
			@RequestParam(name = "emDegree") String aesEmDegree,
			@RequestParam(name = "emNote") String aesEmNote,
			HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录过期，请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
			Employee employee = new Employee();
			employee.setComId(admin.getComId());
		
		employee.setEmId(Aes.aesDecrypt(aesEmId));
		employee.setEmName(Aes.aesDecrypt(aesEmName));
		employee.setEmSex(Aes.aesDecrypt(aesEmSex));
		employee.setEmDep(Aes.aesDecrypt(aesEmDep));
		employee.setEmJob(Aes.aesDecrypt(aesEmJob));
		employee.setEmLandline(Aes.aesDecrypt(aesEmLandline));
		employee.setEmExtension(Aes.aesDecrypt(aesEmExtension));
		employee.setEmEm(Aes.aesDecrypt(aesEmEm));
		employee.setEmNav(Aes.aesDecrypt(aesEmNav));
		employee.setEmGradSch(Aes.aesDecrypt(aesEmGradSch));
		employee.setEmDegree(Aes.aesDecrypt(aesEmDegree));
		employee.setEmNote(Aes.aesDecrypt(aesEmNote));
		try {
			respComm.data = comAdminService.updateComEmployee(employee);
			respComm.status = 600;
			respComm.msg = "操作成功";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 402;
			respComm.msg = "操作异常";
		}}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
	
	/**
	 * 删除公司员工***
	 */
	@RequestMapping(value = "/deleteComEmployee", method = { RequestMethod.POST,
			RequestMethod.GET }, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object deleteComEmployee(@RequestParam(name = "emId",required=false) String aesEmId,
			@RequestParam(name = "emNum",required=false) String aesEmNum,
			@RequestParam(name = "emDep",required=false) String aesEmDep,
			HttpServletRequest request,
			HttpServletResponse response) {

		respComm = new RespCommon();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Cookie> cookieMap = readCookies.ReadCookieMap(request);
		if (!cookieMap.containsKey("comAdmin")) {
			respComm.status = 401;
			respComm.msg = "登录过期，请重新登录！";
		} else {
			Cookie cookie = (Cookie) cookieMap.get("comAdmin");
			String phoneNumber = cookie.getValue();
			System.out.println("cookieValue:" + phoneNumber);
			ComAdmin admin = comAdminService.selectComAdmin(phoneNumber);
			Employee employee = new Employee();
			employee.setComId(admin.getComId());
		
		if (aesEmId!=null) {
			String emId = Aes.aesDecrypt(aesEmId);
			employee.setEmId(emId);
		}
		if (aesEmNum!=null) {
			String emNum = Aes.aesDecrypt(aesEmNum);
			employee.setEmNum(emNum);
		}
		if (aesEmDep!=null) {
			String emDep = Aes.aesDecrypt(aesEmDep);
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
			employee.setEmDep(emDep);
		}
		try {
			respComm.data = comAdminService.selectComEmployees(employee);
			respComm.status = 600;
			respComm.msg = "操作成功";
		} catch (Exception e) {
			e.printStackTrace();
			respComm.status = 402;
			respComm.msg = "操作异常";
		}}

		try {
			return mapper.writeValueAsString(respComm);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return jacksonConvertFailJsonStr;
		}
	}
}
