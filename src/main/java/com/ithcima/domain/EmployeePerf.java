package com.ithcima.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePerf {
	private String emId;
	private String emName;
	private String comId;
	private String emDep;
	private String emJob;
	private String achievement;
	private String workingAbility;
	private String workingAttitude;
	private String emText;
	private String attendance;
	private String punishment;
	private String reward;
	private String assessor;
	private String assessmentDate;
	private String approver;
	private String approvalDate;
	private String month;
	private String monthlyComScore;
	private String year;
	
}
