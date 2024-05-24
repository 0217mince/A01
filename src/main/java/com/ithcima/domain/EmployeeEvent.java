package com.ithcima.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEvent {
	   private String emId;
	   private String comId;
	   private String comName;
	   private String emName;
	   private String emDep;
	   private String emJob;
	   private String type;
	   private String content;
	   private String result;
	   private String notekeeper;
	   private String notekeeperOpinion;
	   private String recordDate;
	   private String approver;
	   private String approverOpinion;
	   private String approvalDate;
	   private String finalScore;
}