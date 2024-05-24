package com.ithcima.utils;

public class randomComId {

		 private static byte[] lock = new byte[0];

		  // 位数，默认是8位

		  private final static long w = 100000000;

		  public static String createID() {

		   long r = 0;

		   synchronized (lock) {

		    r = (long) ((Math.random() + 1) * w);

		   }

		   String Companyid = System.currentTimeMillis()+ String.valueOf(r).substring(1);
		    return Companyid;

		  }
		  
		  /**
		     * 测试
		     */
		    public static void main(String[] args) throws Exception {  
		    	String a=createID();
		    	System.out.println(a);
		    	
		    }
		}

