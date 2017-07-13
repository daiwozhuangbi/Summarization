package edu.ecnu.dase.lp;

import java.util.Map;
import java.util.TreeMap;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;
/**
 * 求解矩阵（混合线性规划）
 * @author iris
 *
 */
public class Coherence {

		//max Z=sim[1,1]r11+sim[1,2]r12+...+sim[n,n]rnn
		//subject to 
		//1>=rij>=0
		//
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
		public Map<Integer, Double> GLPK(double[][]sim){
 		
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
			//  0<=rij<=1
			int count=sim.length*sim.length+2*sim.length;
			GLPK.glp_add_cols(lp, count);//number of structural variables
			
			for(int h=1;h<=count;h++){
				GLPK.glp_set_col_name(lp, h, "x"+h);
				GLPK.glp_set_col_kind(lp, h, GLPKConstants.GLP_BV);
				GLPK.glp_set_col_bnds(lp, h, GLPKConstants.GLP_DB, 0,1);
			}
			 		
			// Create constraints	
			GLPK.glp_add_rows(lp, 2*sim.length+2+(sim.length*sim.length-sim.length)/2+sim.length);//number of constraint
		
			//条件1
			GLPK.glp_set_row_name(lp, 1, "c1");
			GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_FX,1, 1);//初始化第i个约束条件
				
			ind = GLPK.new_intArray(sim.length+1);
			for(int j=1;j<=sim.length;j++)
			    GLPK.intArray_setitem(ind, j, j);//设置条件公式变量
				
			val = GLPK.new_doubleArray(sim.length+1);
			for(int j=1;j<=sim.length;j++)
				GLPK.doubleArray_setitem(val, j, 1);//设置变量前参数
						    		     
			GLPK.glp_set_mat_row(lp, 1, sim.length, ind, val);		//2:第i个公式  3:val[len]中len 
			
			//条件2
			GLPK.glp_set_row_name(lp, 2, "c2");
			GLPK.glp_set_row_bnds(lp, 2, GLPKConstants.GLP_FX,1, 1);//初始化第i个约束条件
				
			ind = GLPK.new_intArray(sim.length+1);
			for(int j=1;j<=sim.length;j++)
			    GLPK.intArray_setitem(ind, j, sim.length+j);//设置条件公式变量
				
			val = GLPK.new_doubleArray(sim.length+1);
			for(int j=1;j<=sim.length;j++)
				GLPK.doubleArray_setitem(val, j, 1);//设置变量前参数
						    		     
			GLPK.glp_set_mat_row(lp, 2, sim.length, ind, val);		//2:第i个公式  3:val[len]中len 
			
			//条件
			int h = 3;
			for(int j = 1;j<=sim.length;j++,h++){
				GLPK.glp_set_row_name(lp, h, "c"+h);
				GLPK.glp_set_row_bnds(lp, h, GLPKConstants.GLP_FX,1, 1);//初始化第i个约束条件
	/*******************************************************************************************************/			
				ind = GLPK.new_intArray(sim.length+2);
				int tmp_ind = 1;
				for(int i = 1;i<=sim.length;i++){
					GLPK.intArray_setitem(ind, tmp_ind++, 2*sim.length+(i-1)*sim.length+j);//设置条件公式变量
				}
				GLPK.intArray_setitem(ind, tmp_ind++, j);
//				System.out.println(tmp_ind);			
	/*******************************************************************************************************/
				val = GLPK.new_doubleArray(sim.length+2);
			    int tmp_val = 1;
				for(int i = 1;i<=sim.length;i++){
					if(i==j)
						GLPK.doubleArray_setitem(val, tmp_val++, 0);
					else
						GLPK.doubleArray_setitem(val, tmp_val++, 1);//设置条件公式变量
				}
				GLPK.doubleArray_setitem(val, tmp_val++, 1);
//				System.out.println(tmp_val);	
				
				GLPK.glp_set_mat_row(lp,h, sim.length+1, ind, val);		//2:第i个公式  3:val[len]中len 				
			}
			//条件

			for(int j = 1;j<=sim.length;j++,h++){
				GLPK.glp_set_row_name(lp, h, "c"+h);
				GLPK.glp_set_row_bnds(lp, h, GLPKConstants.GLP_FX,1, 1);//初始化第i个约束条件
	/*******************************************************************************************************/			
				ind = GLPK.new_intArray(sim.length+2);
				int tmp_ind = 1;
				for(int i = 1;i<=sim.length;i++){
					GLPK.intArray_setitem(ind, tmp_ind++, 2*sim.length+(j-1)*sim.length+i);//设置条件公式变量
				}
				GLPK.intArray_setitem(ind, tmp_ind++, j+sim.length);
//				System.out.println(tmp_ind);			
	/*******************************************************************************************************/
				val = GLPK.new_doubleArray(sim.length+2);
			    int tmp_val = 1;
				for(int i = 1;i<=sim.length;i++){
					if(i==j)
						GLPK.doubleArray_setitem(val, tmp_val++, 0);
					else
						GLPK.doubleArray_setitem(val, tmp_val++, 1);//设置条件公式变量
				}
				GLPK.doubleArray_setitem(val, tmp_val++, 1);
//				System.out.println(tmp_val);	
				
				GLPK.glp_set_mat_row(lp,h, sim.length+1, ind, val);		//2:第i个公式  3:val[len]中len 				
			}
			
			//条件 对角元素不相等

		for(int i = 1;i<=sim.length;i++){
			 for(int j = i;j<=sim.length;j++){
		            if(i==j)
		            	continue;		            
					GLPK.glp_set_row_name(lp, h, "c"+h);
					GLPK.glp_set_row_bnds(lp, h, GLPKConstants.GLP_UP,0, 1);//初始化第i个约束条件
		/*******************************************************************************************************/			
					ind = GLPK.new_intArray(3);
		            
					GLPK.intArray_setitem(ind, 1, 2*sim.length+(i-1)*sim.length+j);//设置条件公式变量
					GLPK.intArray_setitem(ind, 2, 2*sim.length+(j-1)*sim.length+i);//设置条件公式变量			
		/*******************************************************************************************************/
					val = GLPK.new_doubleArray(3);
	
					GLPK.doubleArray_setitem(val, 1, 1);
					GLPK.doubleArray_setitem(val, 2, 1);	
					
					GLPK.glp_set_mat_row(lp,h, 2, ind, val);		//2:第i个公式  3:val[len]中len 
					h++;
				}
			}
			//条件  0i ！ = it
		for(int i = 1;i<=sim.length;i++){
			GLPK.glp_set_row_name(lp, h, "c"+h);
			GLPK.glp_set_row_bnds(lp, h, GLPKConstants.GLP_UP,0, 1);//初始化第i个约束条件
				
			ind = GLPK.new_intArray(sim.length+1);
			GLPK.intArray_setitem(ind, 1, i);//设置条件公式变量
			GLPK.intArray_setitem(ind, 2, i+sim.length);
				
			val = GLPK.new_doubleArray(sim.length+1);
			GLPK.doubleArray_setitem(val, 1, 1);//设置变量前参数
			GLPK.doubleArray_setitem(val, 2, 1);
						    		     
			GLPK.glp_set_mat_row(lp, h++,2, ind, val);		//2:第i个公式  3:val[len]中len 
		}
			
		
		
			// Define objective
			GLPK.glp_set_obj_name(lp, "z");
			GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
			for(int i=1;i<=2*sim.length;i++)
				GLPK.glp_set_obj_coef(lp, i, 0);
			
		    for(int i=0;i<sim.length;i++)
		        for(int j =0;j<sim.length;j++)
			       GLPK.glp_set_obj_coef(lp, 2*sim.length+sim.length*i+(j+1), sim[i][j]);
		    
			// solve model
			parm = new glp_smcp();
			GLPK.glp_init_smcp(parm);
			ret = GLPK.glp_simplex(lp, parm);
			
//			int flag= GLPK.glp_get_prim_stat(lp);
			
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
//			
//			Map<Integer,Map<Integer, Double>> result = new HashMap<Integer, Map<Integer,Double>>();
//			result.put(flag, xmap);
//			return result;
			return xmap;
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
//				System.out.print(val+"\t");
				xmap.put(l, val);
			}
			
			return xmap;
		}
	}


