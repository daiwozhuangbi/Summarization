package edu.ecnu.dase.sc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;
/** 
 * 通过GLPK找出最小cover全部关键词的句子集合
 * @author Iris
 */

public class LinearProgramming {

	//minimize Z=X1+X2+...+Xm
	//subject to 
	//1>=Xi>=0
	//X1Yi1+X2Yi2+...+XmYim>=1  i=1,2,...n
    //(X1Z11+X2Z12+...+XmZ1m)+(X1Z21+X2Z22+...+XmZ2m)+...+(X1Zn1+X2Zn2+...+XmZnm)>=1
	static {
		try {
			// try to load Linux library
			System.loadLibrary("glpk_java");
		} catch (UnsatisfiedLinkError e) {
			// try to load Windows library
			System.loadLibrary("glpk_4_60_java");
		}
	}
	/**
	 * 调用glpk包解决线性规划问题.
	 * 计算出最小能cover所有关键词的句子
	 * @param Y 关键词与句子关系的数组
	 * @param Z 用户自定义关键词与句子关系的数组
	 * @param sentence 所有句子
	 * @param KeyWordCount 关键词个数
	 * @return xmap 每个句子的得分结果
	 */
	public Map<Integer, Map<Integer, Double>> GLPK(int[][] Y,int[][]Z,List<String>sentence,int KeyWordCount){
		
		int m=sentence.size();
		int[] temp = new int[m];
		for(int j=0;j<Z[0].length;j++)
			for(int i=0;i<Z.length;i++)
			     temp[j]+=Z[i][j];
		glp_prob lp;
		glp_smcp parm;
		SWIGTYPE_p_int ind;
		SWIGTYPE_p_double val;
		int ret;
		// Create problem
		lp = GLPK.glp_create_prob();
		System.out.println("Problem created");
		GLPK.glp_set_prob_name(lp, "myProblem");
		
		// Define columns
		//  0<=xi<=1
		int count=sentence.size();
		GLPK.glp_add_cols(lp, count);//number of structural variables
		for(int h=1;h<=count;h++){
		GLPK.glp_set_col_name(lp, h, "x"+h);
		GLPK.glp_set_col_kind(lp, h, GLPKConstants.GLP_CV);
		GLPK.glp_set_col_bnds(lp, h, GLPKConstants.GLP_DB, 0,1);
		}
		 		
		// Create constraints	
		GLPK.glp_add_rows(lp, KeyWordCount+1);//number of constraint
		   //条件1 
		for(int i=1;i<=KeyWordCount;i++){
		   GLPK.glp_set_row_name(lp, i, "c"+i);
		   GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_LO,1, 0);
		
			ind = GLPK.new_intArray(count+1);
			for(int j=1;j<=count;j++)
		       GLPK.intArray_setitem(ind, j, j);
			
			val = GLPK.new_doubleArray(count+1);
			for(int j=1;j<=count;j++)
			   GLPK.doubleArray_setitem(val, j, Y[i-1][j-1]);
					    		     
			GLPK.glp_set_mat_row(lp, i, count, ind, val);		
		}
		  //条件2
        int tmp=KeyWordCount+1;
		GLPK.glp_set_row_name(lp, tmp, "c"+tmp);
		GLPK.glp_set_row_bnds(lp,tmp, GLPKConstants.GLP_LO,1, 0);
		ind = GLPK.new_intArray(count+1);
		for(int j=1;j<=count;j++)
		       GLPK.intArray_setitem(ind, j, j);
		
		val = GLPK.new_doubleArray(count+1);
		for(int j=1;j<=count;j++)
		   GLPK.doubleArray_setitem(val, j, temp[j-1]);
		GLPK.glp_set_mat_row(lp, tmp, count, ind, val);	
		
//		//条件3   senlong[i]*xi<=25
//		 
//		for(int k=1;k<=count;k++){			
//			GLPK.glp_set_row_name(lp, tmp+k, "c"+tmp+k);
//			GLPK.glp_set_row_bnds(lp,tmp+k, GLPKConstants.GLP_UP,25,25);
//			ind = GLPK.new_intArray(1);
//			GLPK.intArray_setitem(ind, 1, k);
//			
//			val = GLPK.new_doubleArray(1);
//			GLPK.doubleArray_setitem(val, 1, senlong[k-1]);
//			GLPK.glp_set_mat_row(lp, tmp+k, 1, ind, val);				
//		}
		
		// Define objective
		GLPK.glp_set_obj_name(lp, "z");
		GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
	    for(int i=1;i<=count;i++){
		   GLPK.glp_set_obj_coef(lp, i, 1);
	    }
		// solve model
		parm = new glp_smcp();
		GLPK.glp_init_smcp(parm);
		ret = GLPK.glp_simplex(lp, parm);
		
		int flag= GLPK.glp_get_prim_stat(lp);
		
		// Retrieve solution
		Map<Integer, Double> xmap=new TreeMap<Integer, Double>();
		if (ret == 0) {
		  xmap=write_lp_solution(lp);		  
		}
		else {
		System.out.println("The problem could not be solved");
		};
		// free memory
		GLPK.glp_delete_prob(lp);
		
		Map<Integer,Map<Integer, Double>> result = new HashMap<Integer, Map<Integer,Double>>();
		result.put(flag, xmap);
		return result;
		}
	
	
	/**
	 * write simplex solution
	 * 
	 * @param lp
	 *            problem
	 */
	static Map<Integer, Double> write_lp_solution(glp_prob lp)
	{
		int l;
		int n;
		double val;
		Map<Integer,Double> xmap=new TreeMap<Integer,Double>();
		val = GLPK.glp_get_obj_val(lp);
		n = GLPK.glp_get_num_cols(lp);
		for(l=1; l <= n; l++)
		{
			val = GLPK.glp_get_col_prim(lp, l);
			xmap.put(l, val);
		}
		return xmap;
	}
}
