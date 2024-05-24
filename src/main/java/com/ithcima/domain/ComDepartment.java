package com.ithcima.domain;

import lombok.Data;

@Data
public class ComDepartment {
      public String comId;  //公司企业号
      public String dSuperior; //部门管理者 
      public String dName;   //部门名字
      public String dDescri;  // 部门描述
      public String dRank;    //部门排序
      public String isDelete;  //是否删除，0代表未删除，1代表已删除
	
}