package com.ithcima.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Approval {
  private String applicantName;
  private String applicantId;
  private String applicantDate;
  private String applicantReason;
  private String approverName;
  private String approverId;
  private String approverDate;
  private String approverResult;
  private String type;
  private String applicantComId;
  private String applicantComName;
  private String approverComId;
  private String approverComName;
  private String state;
  private String target;
  private String targetId;
}
