package com.ithcima.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ComAudit{
	
	private String epId;
	private String auditStatus;
	private String auditReason;
	private String auditFile;
	private Date auditTime;
}
