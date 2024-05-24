package com.ithcima.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ithcima.domain.*;
import com.ithcima.mapper.ComAdminMapper;
import com.ithcima.service.ComAdminService;
import com.ithcima.utils.Aes;
import com.ithcima.utils.imageUtil;
import com.ithcima.utils.randomComId;

@Service
public class ComAdminServiceImpl implements ComAdminService {

	@Autowired
	private ComAdminMapper comAdminMapper;

	@Override
	public int insertComAdmin(ComAdmin comAdmin) {
		comAdminMapper.insertComAdmin(comAdmin);
		comAdminMapper.insertComAdminInfo(comAdmin);
		comAdminMapper.insertComInfo(comAdmin);

		String comId = comAdmin.getComId();

		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		ComAudit comAudit = new ComAudit();
		comAudit.setEpId(comId);
		comAudit.setAuditReason(null);
		comAudit.setAuditStatus("1");
		comAudit.setAuditTime(time);
		comAudit.setAuditFile(null);

		ComMod comMod = new ComMod();
		comMod.setEpId(comId);
		comMod.setModReason(null);
		comMod.setModStatus("1");
		comMod.setModTime(time);

		int mark1 = comAdminMapper.insertComMod(comMod);
		int mark2 = comAdminMapper.insertComAudit(comAudit);

		return mark1&mark2;
	}

	@Override
	public ComAdmin selectComAdmin(String id) {
		return comAdminMapper.selectComAdmin(id);
	}

	@Override
	public Com selectComInfo(String comId) {
		Com com = comAdminMapper.selectComInfo(comId);

		// Logo地址转图像
		String defaultLogo = "C:/picSource/logo/addPic.png";
		if (com.getEpLogo() != null) {
			defaultLogo = "C:/picSource/logo/" + com.getEpLogo();
		}
		String logo = "data:image/png;base64," + imageUtil.getImageStr(defaultLogo);
		com.setEpLogo(logo);

		// epTime、epTime_sp分割
		String epTime = com.getEpTime();
		String time1 = "";
		String time2 = "";
		if (com.epTime != null) {
			time1 = epTime.substring(0, epTime.indexOf("-"));
			time2 = epTime.substring(epTime.indexOf("-") + 1, epTime.length());
			com.setEpTime(time1);
			com.setEpTime_sp(time2);
		}

		// epIndustry、epIndustry_sp分割
		String epIndustry = com.getEpIndustry();
		String industry1 = "";
		String industry2 = "";
		if (epIndustry != null) {
			industry1 = epIndustry.substring(0, epIndustry.indexOf("-"));
			industry2 = epIndustry.substring(epIndustry.indexOf("-") + 1, epIndustry.length());
			com.setEpIndustry(industry1);
			com.setEpIndustry_sp(industry2);
		}

		// epRegion、epRegion_sp分割
		String epRegion = com.getEpRegion();
		String region1 = "";
		String region2 = "";
		if (epRegion != null) {
			region1 = epRegion.substring(0, epRegion.indexOf("-"));
			region2 = epRegion.substring(epRegion.indexOf("-") + 1, epRegion.length());
			com.setEpRegion(region1);
			com.setEpRegion_sp(region2);
		}

		return com;
	}

	@Override
	public int updateComInfo(Com com) {
		return comAdminMapper.updateComInfo(com);
	}

	@Override
	public String selectComAuditStatus(String comId) {
		Com com = comAdminMapper.selectComInfo(comId);
		return com.getAuditStatus();
	}

	@Override
	public ArrayList<SimpleCom> selectAuditedSimpleComs() {
		String auditStatus = "3";
		String modStatus = "1";
		ArrayList<SimpleCom> coms = comAdminMapper.selectSimpleComs(auditStatus, modStatus);

		for (SimpleCom com : coms) {
			String indus = com.getEpIndustry();
			if (indus != null) {
				com.setEpIndustry(indus.substring(0, indus.indexOf("-")));
				com.setEpIndustry_sp(indus.substring(indus.indexOf("-") + 1, indus.length()));
			}

			String loc = com.getEpRegion();
			if (loc != null) {
				com.setEpRegion(loc.substring(0, loc.indexOf("-")));
				com.setEpRegion_sp(loc.substring(loc.indexOf("-") + 1, loc.length()));
			}
		}
		return coms;
	}

	@Override
	public ArrayList<Com> selectAuditedComs() {
		String auditStatus = "3";
		String modStatus = "1";
		ArrayList<Com> coms = comAdminMapper.selectComs(auditStatus, modStatus);

		return coms;
	}

	@Override
	public ArrayList<SimpleCom> selectToAuditSimpleComs() {
		String modStatus = "1";
		ArrayList<String> list = new ArrayList<String>();
		list.add("0");
		list.add("-1");
		list.add("2");
		ArrayList<SimpleCom> coms = new ArrayList<SimpleCom>();
		for (String auditStatus : list) {
			coms.addAll(comAdminMapper.selectSimpleComs(auditStatus, modStatus));
		}
		for (SimpleCom com : coms) {
			String indus = com.getEpIndustry();
			if (indus != null) {
				com.setEpIndustry(indus.substring(0, indus.indexOf("-")));
				com.setEpIndustry_sp(indus.substring(indus.indexOf("-") + 1, indus.length()));
			}

			String loc = com.getEpRegion();
			if (loc != null) {
				com.setEpRegion(loc.substring(0, loc.indexOf("-")));
				com.setEpRegion_sp(loc.substring(loc.indexOf("-") + 1, loc.length()));
			}
		}
		return coms;
	}

	@Override
	public int deleteCom(String comId, String deleteReason) {
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		ComMod comMod = new ComMod();
		comMod.setEpId(comId);
		comMod.setModReason(deleteReason);
		comMod.setModStatus("-1");
		comMod.setModTime(time);

		int mark1 = comAdminMapper.updateComInfoMod(comMod);
		int mark2 = comAdminMapper.insertComMod(comMod);

		return mark1 & mark2;
	}

	@Override
	public ArrayList<ComMod> selectComMods(String comId) {
		String modStatus = null;
		return comAdminMapper.selectComMods(comId, modStatus);
	}

	@Override
	public ComMod selectComMod(String comId) {
		String modStatus = "-1";
		ArrayList<ComMod> comMods = comAdminMapper.selectComMods(comId, modStatus);
		ComMod comMod = new ComMod();
		if (comMods.size() > 0) {
			comMod = comMods.get(0);
			for (int i = 0; i + 1 < comMods.size(); i++) {
				
				Date time = comMod.getModTime();
				Timestamp ctime = new Timestamp(time.getTime());
				time = ctime;
				comMod.setModTime(time);
				comMods.set(i, comMod);
				
				if (comMods.get(i).getModTime().getTime() < comMods.get(i + 1).getModTime().getTime()) {
					comMod = comMods.get(i + 1);
				}
			}
		}
		return comMod;
	}

	@Override
	public int auditCom(String comId) throws ParseException {
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		ComAudit comAudit = new ComAudit();
		comAudit.setEpId(comId);
		comAudit.setAuditReason(null);
		comAudit.setAuditStatus("3");
		comAudit.setAuditTime(time);
		comAudit.setAuditFile(null);

		int mark1 = comAdminMapper.updateComInfoAudit(comAudit);
		int mark2 = comAdminMapper.insertComAudit(comAudit);

		return mark1 & mark2;
	}

	@Override
	public int auditFailCom(String comId, String failReason) {
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		ComAudit comAudit = new ComAudit();
		comAudit.setEpId(comId);
		comAudit.setAuditReason(failReason);
		comAudit.setAuditStatus("-2");
		comAudit.setAuditTime(time);
		comAudit.setAuditFile(null);

		int mark1 = comAdminMapper.updateComInfoAudit(comAudit);
		int mark2 = comAdminMapper.insertComAudit(comAudit);

		return mark1 & mark2;
	}

	@Override
	public int applyComAudit(String comId, String auditFile) {
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		String auditStatus = "-1";
		String lastAuditStatus = selectComAuditStatus(comId);
		if (lastAuditStatus.equals("1")) {
			auditStatus = "0";
		}

		String file = null;
		if (auditFile != null) {
			String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);// 精确到秒
			file = comId + timeStamp + ".png";
			auditFile = auditFile.substring(auditFile.indexOf(",") + 1, auditFile.length());
			imageUtil.generateImage(auditFile, "C:/picSource/audit/" + file);
		}

		ComAudit comAudit = new ComAudit();
		comAudit.setEpId(comId);
		comAudit.setAuditReason(null);
		comAudit.setAuditStatus(auditStatus);
		comAudit.setAuditTime(time);
		comAudit.setAuditFile(file);

		int mark1 = comAdminMapper.updateComInfoAudit(comAudit);
		int mark2 = comAdminMapper.insertComAudit(comAudit);

		return mark1 & mark2;
	}

	@Override
	public int comAuditWarningConfirm(String comId) {
		String auditStatus = selectComAuditStatus(comId);

		if (!auditStatus.equals("-2")) {
			return 0;
		}

		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		ComAudit comAudit = new ComAudit();
		comAudit.setEpId(comId);
		comAudit.setAuditReason(null);
		comAudit.setAuditStatus("-3");
		comAudit.setAuditTime(time);
		comAudit.setAuditFile(null);

		int mark1 = comAdminMapper.updateComInfoAudit(comAudit);
		int mark2 = comAdminMapper.insertComAudit(comAudit);

		return mark1 & mark2;
	}

	@Override
	public int alterAuditedCom(String comId, String auditFile) {
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		String file = null;
		if (auditFile != null) {
			String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);// 精确到秒
			file = comId + timeStamp + ".png";
			auditFile = auditFile.substring(auditFile.indexOf(",") + 1, auditFile.length());
			imageUtil.generateImage(auditFile, "C:/picSource/audit/" + file);
		}

		ComAudit comAudit = new ComAudit();
		comAudit.setEpId(comId);
		comAudit.setAuditReason(null);
		comAudit.setAuditStatus("2");
		comAudit.setAuditTime(time);
		comAudit.setAuditFile(file);

		int mark1 = comAdminMapper.updateComInfoAudit(comAudit);
		int mark2 = comAdminMapper.insertComAudit(comAudit);

		return mark1 & mark2;
	}

	@Override
	public ArrayList<SimpleCom> selectDeletedSimpleComs() {
		String auditStatus = null;
		String modStatus = "-1";
		ArrayList<SimpleCom> coms = comAdminMapper.selectSimpleComs(auditStatus, modStatus);

		for (SimpleCom com : coms) {

			String indus = com.getEpIndustry();
			if (indus != null) {
				com.setEpIndustry(indus.substring(0, indus.indexOf("-")));
				com.setEpIndustry_sp(indus.substring(indus.indexOf("-") + 1, indus.length()));
			}

			String loc = com.getEpRegion();
			if (loc != null) {
				com.setEpRegion(loc.substring(0, loc.indexOf("-")));
				com.setEpRegion_sp(loc.substring(loc.indexOf("-") + 1, loc.length()));
			}
		}
		return coms;
	}

	@Override
	public int restoreDeletedCom(String comId) {
		Date time = new Date();// 获得系统时间.
		Timestamp ctime = new java.sql.Timestamp(time.getTime());
		time = ctime;

		ComMod comMod = new ComMod();
		comMod.setEpId(comId);
		comMod.setModReason(null);
		comMod.setModStatus("1");
		comMod.setModTime(time);

		int mark1 = comAdminMapper.updateComInfoMod(comMod);
		int mark2 = comAdminMapper.insertComMod(comMod);

		return mark1 & mark2;
	}

	@Override
	public ArrayList<ComAudit> selectComAudits(String comId) {
		ArrayList<ComAudit> comAudits = comAdminMapper.selectComAudits(comId);
		for (ComAudit comAudit : comAudits) {

			Date time = comAudit.getAuditTime();
			Timestamp ctime = new Timestamp(time.getTime());
			time = ctime;
			comAudit.setAuditTime(time);

			String defaultFile = "C:/picSource/audit/addPic.png";
			if (comAudit.getAuditFile() != null) {
				defaultFile = "C:/picSource/audit/" + comAudit.getAuditFile();
			}
			String file = "data:image/png;base64," + imageUtil.getImageStr(defaultFile);
			comAudit.setAuditFile(file);
		}
		return comAudits;
	}

	@Override
	public ComAudit selectComAudit(String comId) {
		ArrayList<ComAudit> comAudits = comAdminMapper.selectComAudits(comId);

		ComAudit comAudit = new ComAudit();
		if (comAudits.size() > 0) {
			comAudit = comAudits.get(0);

			for (ComAudit audit:comAudits) {
				Date time = audit.getAuditTime();
				Timestamp ctime = new Timestamp(time.getTime());
				time = ctime;
				audit.setAuditTime(time);
			}

			for (int i = 0; i + 1 < comAudits.size(); i++) {
				if (comAudits.get(i).getAuditTime().getTime() < comAudits.get(i + 1).getAuditTime().getTime()){
				comAudit = comAudits.get(i + 1);
				}
			}
		}

		if (comAudit.getEpId() != null){
			String defaultFile = "C:/picSource/audit/addPic.png";
			if (comAudit.getAuditFile() == null) {
				for (int i = 0; i < comAudits.size(); i++) {
					if(comAudits.get(i).getAuditFile() == null){
						comAudits.remove(i);
					}
				}
				if(comAudits.size() != 0){
					defaultFile = "C:/picSource/audit/" + comAudits.get(0).getAuditFile();
					for (int i = 0; i + 1 < comAudits.size(); i++) {
						if (comAudits.get(i).getAuditTime().getTime() < comAudits.get(i + 1).getAuditTime().getTime()){
						defaultFile = "C:/picSource/audit/" + comAudits.get(i + 1).getAuditFile();
						}
					}
				}
			}else{
				defaultFile = "C:/picSource/audit/" + comAudit.getAuditFile();
			}
			String file = "data:image/png;base64," + imageUtil.getImageStr(defaultFile);
			comAudit.setAuditFile(file);
		}

		return comAudit;
	}
	
	@Override
	public boolean insertComDepartment(ComDepartment dep) {
		int mark = comAdminMapper.insertComDepartment(dep);
		return mark == 1? true : false;
	}
	
	@Override
	public boolean updateComDepartment(ComDepartment dep) {
		int mark = comAdminMapper.updateComDepartment(dep);
		return mark == 1? true : false;
	}
	
	@Override
	public boolean deleteComDepartment(String comId,String dName) {
		String isDelete = "1";
		int mark = comAdminMapper.updateComDepartmentIsDelete(comId,dName,isDelete);
		return mark == 1? true : false;
	}
	
	@Override
	public boolean restoreComDepartment(String comId,String dName) {
		String isDelete = "0";
		int mark = comAdminMapper.updateComDepartmentIsDelete(comId,dName,isDelete);
		return mark == 1? true : false;
	}
	
	@Override
	public ArrayList<ComDepartment> selectComDepartments(String comId) {
		String isDelete = "0";
		ArrayList<ComDepartment> deps= comAdminMapper.selectComDepartment(comId,isDelete);
		return deps;
	}
	
	@Override
	public ArrayList<ComDepartment> selectDeletedComDepartments(String comId) {
		String isDelete = "1";
		ArrayList<ComDepartment> deps= comAdminMapper.selectComDepartment(comId,isDelete);
		return deps;
	}

	@Override
	public boolean updateComEmployee(Employee employee) {
		int mark;
		if (comAdminMapper.selectComEmployeeByEmPh(employee.getEmPh()) != null) {
			employee.setEmId(comAdminMapper.selectComEmployeeByEmPh(employee.getEmPh()).getEmId());
			mark = comAdminMapper.updateComEmployee(employee);
		} else {
			employee.setEmId(randomComId.createID());
			mark = comAdminMapper.insertComEmployee(employee);
		}
		return mark == 1 ? true : false;
	}
	
	@Override
	public boolean deleteEmployee(String emId) {
		int mark = comAdminMapper.updateComEmployeeIsDelete(emId);
		return mark == 1?true :false;
	}
	
	@Override
	public ArrayList<Employee> selectComEmployees(Employee employee) {
		return comAdminMapper.selectComEmployees(employee);
	}
}
