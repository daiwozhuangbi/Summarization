package edu.ecnu.dase.test;


import edu.ecnu.dase.sm.Summary2;
import edu.ecnu.dase.sm.Summary1;
import edu.ecnu.dase.sm.Summary;
public class SMTester {

	public static void main(String[] args) {
		String inputfilePath="/Users/iris/Documents/训练预测数据（分开）/1.餐饮管理/餐饮管理.xls";
		String outfilePath="/study/eclipse/iris-output/Summary/";

		Summary2 s = new Summary2();
		try {
			s.getSummary(inputfilePath, outfilePath, 1);
			System.out.println("sucess!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
