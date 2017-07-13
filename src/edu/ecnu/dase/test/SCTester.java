package edu.ecnu.dase.test;

import java.io.IOException;

import edu.ecnu.dase.sc.GetSummarization1;


public class SCTester {


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {		
		String inputfilePath="/Users/iris/Documents/训练预测数据（分开）/1.餐饮管理/餐饮管理.xls";
		String outfilePath="/study/eclipse/iris-output/Summary/";
		GetSummarization1 gets=new GetSummarization1();
		try {
        gets.getSummarization(inputfilePath,outfilePath,1);//自定义生成篇数
		} catch (IOException e) {
			throw e;
		}
       
	}

}
