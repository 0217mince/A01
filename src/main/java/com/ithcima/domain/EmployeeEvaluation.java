package com.ithcima.domain;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEvaluation {
	private String emId;
	private String comId;
	private String evaluator;
	private String evaluatorDep;
	private String evaluatorJob;
	private String emName;
	private String emDep;
	private String emJob;
	private Date date;
	private String potentialCapability;
	private String apparentCapability;
	private String experientialAbility;
	private String knowledge;
	private String interpersonalRelationship;
	private String comprehensiveEvaluation;
}
