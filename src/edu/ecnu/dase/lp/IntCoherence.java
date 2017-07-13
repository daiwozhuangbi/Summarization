package edu.ecnu.dase.lp;

import java.util.Map;
import java.util.TreeMap;

import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverGLPK;
import net.sf.javailp.VarType;
/**
 * 求解矩阵（整数现性规划）
 * @author iris
 *
 */
public class IntCoherence {
	/**
	  * @param args 
	 * @return 
	  */ 
	 public Map<Integer, Integer> entrance(float[][] sim) { 
		 Map<Integer, Integer> ret = new TreeMap<Integer, Integer>();
		 ret = GLPK(new SolverGLPK(),sim); 
		 return ret;
	 } 
	 
	 public Map<Integer, Integer> GLPK(Solver solver,float[][] sim) { 
     
		 
	  solver.setParameter(Solver.VERBOSE, 0); 
	  solver.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds 
	  solver.setParameter(Solver.METHOD, solver.getInternalValueForID(Solver.METHOD_ID_AUTOMATIC)); 

	  // for one problem 
	  Problem problem = solver.getProblem("GLPK"); 
	  
	  int count=sim.length*sim.length+2*sim.length;
     /** 设置变量 **/
	  for(int h=1;h<=count;h++){
		  problem.addVariable("x"+h, VarType.INT, 0, 1); 
	  }
	  
	  /** 设置目标函数 **/
	  Linear linear = new Linear(); 
	  for(int i=1;i<=2*sim.length;i++)
		linear.add(0,"x"+i);
	  int temp;
	  for(int i=0;i<sim.length;i++)
	        for(int j =0;j<sim.length;j++){
	        	temp = +2*sim.length+sim.length*i+(j+1);
	        	linear.add( sim[i][j], "x"+temp); 
	        }
	 
	  problem.setObjective(linear, OptType.MAX); 
	 
	  /** 设置限制条件 **/
	  //条件1
	  linear = new Linear(); 
	  for(int j=1;j<=sim.length;j++)
		  linear.add(1, "x"+j); 
	 
	  problem.addConstraint("c1", linear, Operator.EQ, 1); 
	  /***********************************************************/
	  //条件2
	  linear = new Linear(); 
	  for(int j=1;j<=sim.length;j++){
		  temp =sim.length+j;
		  linear.add(1, "x"+temp); 
	  }
			 
	  problem.addConstraint("c2", linear, Operator.EQ, 1); 
	  /***********************************************************/
	  //条件3
	  int h = 3;
	  for(int j = 1;j<=sim.length;j++,h++){
		  linear = new Linear();
		  for(int i = 1;i<=sim.length;i++){
			  if(i==j){
				  temp = 2*sim.length+(i-1)*sim.length+j;
				  linear.add(0, "x"+temp); 
			  }
			  else{
				  temp = 2*sim.length+(i-1)*sim.length+j;
				  linear.add(1, "x"+temp); 
			  }
		  } 
		  linear.add(1,"x"+j);
		  problem.addConstraint("c"+h, linear, Operator.EQ, 1);   
	  }
	  
	  /***********************************************************/
	  //条件4	  
	  for(int j = 1;j<=sim.length;j++,h++){
		  linear = new Linear();
		  for(int i = 1;i<=sim.length;i++){
			  if(i==j){
				  temp = 2*sim.length+(j-1)*sim.length+i;
				  linear.add(0, "x"+temp); 
			  }
			  else{
				  temp = 2*sim.length+(j-1)*sim.length+i;
				  linear.add(1, "x"+temp); 
			  }
		  } 
		  temp = j+sim.length;
		  linear.add(1,"x"+temp);
		  problem.addConstraint("c"+h, linear, Operator.EQ, 1);   
	  }
	  /***********************************************************/
	  //条件5
	  for(int i = 1;i<=sim.length;i++){	 
		  for(int j = 1;j<=sim.length;j++){
			  linear = new Linear();
			  if(i==j)
				  continue;
			  temp = 2*sim.length+(i-1)*sim.length+j;
		      linear.add(1, "x"+temp); 
		      temp = 2*sim.length+(j-1)*sim.length+i;
		      linear.add(1, "x"+temp);  
			  problem.addConstraint("c"+h, linear, Operator.LE, 1); 
			  h++;
		  }
	  }
	  /***********************************************************/
	  //条件6
	  for(int i = 1;i<=sim.length;i++){
		  	linear = new Linear();
			linear.add(1, "x"+i); 
			temp = i+sim.length;
		    linear.add(1, "x"+temp);  
			problem.addConstraint("c"+h, linear, Operator.LE, 1); 
			h++;
	  }
	  /***********************************************************/

	  
	 /** 求解 **/
	  Result result = solver.solve(problem); 
	 
//	  System.out.println(result.getPrimalValue("x"));
	  Map<Integer, Integer> ret = new TreeMap<Integer, Integer>();
	  for(int i = 1;i<=count;i++)
		  ret.put(i, (Integer)result.getPrimalValue("x"+i));
//	  System.out.println(result); 
	 
	  return ret;
	 
	 } 

}
