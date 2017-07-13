package edu.ecnu.dase.simcoherence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import edu.ecnu.dase.lp.IntCoherence;


public class Similarity {
	public static final int NUM_PARA = 3;

	/**
	 * 获得句子主题分布
	 * @param sen
	 * @return 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Vector<Double>[] getVectorZ(List<String> sen) throws Exception{		
		
		/** 读取每个句子主题分布 **/
	    Vector<Double> []z = new Vector[sen.size()];
		String file = "models/casestudy-china/ESpredictDoc.txt.model-final.theta";	
		File f = new File(file);
		InputStreamReader read = new InputStreamReader(new FileInputStream(f), "utf-8");// 考虑到编码格式
		@SuppressWarnings("resource")
		BufferedReader bufferedReader = new BufferedReader(read);
		String lineTxt = null;
		String[] str = null;
		int count =0;//标记第几句
		//跳到@DATA下一行
		while((lineTxt = bufferedReader.readLine()).contains("@"))
			;
		while (lineTxt != null) {
			z[count] = new Vector<Double>();
			if (lineTxt.length() > 0) {
				  str = lineTxt.split(" ");
				 for(int i=0;i<str.length;i++){
					 z[count].add(Double.parseDouble(str[i]));
				 }
			}
			lineTxt = bufferedReader.readLine();
			count++;
		}
		return z;
	}
	/**
	 * 
	* @Title: calculateCos
	* @Description: 计算余弦相似性
	* @param @param first
	* @param @param second
	* @param @return    
	* @return Double   
	* @throws
	 */
	public float calculateCos(int indexi, int indexj,Vector<Double>[]z){
		//计算相似度  
        float vectorFirstModulo = 0.0f;//向量1的模  
        float vectorSecondModulo = 0.0f;//向量2的模  
        float vectorProduct = 0.0f; //向量积  

		for(int i=0;i<z[indexi].size();i++){
			vectorFirstModulo+=z[indexi].get(i)*z[indexi].get(i);
			vectorSecondModulo+=z[indexj].get(i)*z[indexj].get(i);
		    vectorProduct+=z[indexi].get(i)*z[indexj].get(i);
		}
	   return (float) (vectorProduct/(Math.sqrt(vectorFirstModulo)*Math.sqrt(vectorSecondModulo)));
	}
	/**
	 * 计算句子间余弦相似度矩阵
	 * @param senclass
	 * @return
	 */
	public float[][] CosSimMatrix(List<Integer> senclass,Vector<Double>[]z){
		float[][] sim = new float[senclass.size()][senclass.size()];
		for(int i =0;i<senclass.size();i++)
			for(int j =0;j<senclass.size();j++){
				if(i==j)
					sim[i][j]=0;
				else
				    sim[i][j] = calculateCos(senclass.get(i),senclass.get(j),z);
			}
		return sim;
	}


	/**
	 * 计算句子间jaccard相似度
	 * @return 
	 */
	public static float jaccard(String str1, String str2){
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		Result result = ToAnalysis.parse(str1);
		for (Term term : result) {
			String item = term.getName().trim();
			item = item.replaceAll("[\\pP\\pS\\pZ]", "");
			if (item.length() > 0 && !list1.contains(item) && !item.equals("")) {				
				list1.add(item);
			}
	    }
	    result = ToAnalysis.parse(str2);
		for (Term term : result) {
			String item = term.getName().trim();
			item = item.replaceAll("[\\pP\\pS\\pZ]", "");
			if (item.length() > 0 && !list2.contains(item) && !item.equals("")) {				
				list2.add(item);
			}
	    }
		/** 计算分子 **/
		int count = 0;
		for(String s1 : list1){
			for(String s2 : list2){
				if(s1.equals(s2))
					count++;
			}				
		}
		
		/** 计算句子的jaccard相似度 **/
		float sim = (float)count/(list1.size()+list2.size()-count);
		
		return sim;		
	}
	/**
	 * 计算句子间的jaccard相似度矩阵
	 * @param sentence
	 * @return
	 */
	public float[][] JaccardSimMatrix(List<String>sentence){
		int i = 0;
		int j = 0;
		float[][] sim =new float[sentence.size()][sentence.size()];
		for(String sen :sentence){
			i++;
			j = 0;
			for(String sen_:sentence){
				j++;
				if(i==j)
					continue;
				sim[i-1][j-1] = jaccard(sen,sen_);
			}
		}
		return sim;
	}
	
	
	/**
	 * 获得句子顺序
	 * @param sim
	 * @return
	 */
	public List<Integer> coherentMatrix2seq(float[][] sim){
		IntCoherence c = new IntCoherence();
		Map<Integer, Integer> rij = c.entrance(sim);
		int k = 1;
		@SuppressWarnings("unchecked")
		Vector<Integer> []location = new Vector[sim.length+2];
		for(int i = 0;i<sim.length+2;i++){
			location[i] = new Vector<Integer>();
			for(int j =0; j<sim.length;j++){
				location[i].add(rij.get(k));
				k++;
			}
		}
		List<Integer> seqsen = new ArrayList<>();
		int j;
		int temp;
		j = location[0].indexOf(1);
		seqsen.add(j+1);
		temp = j+2;
		int flag = 2;//若回路断了，从头开始
		int zero = 0;
		while(seqsen.size()<sim.length){
			/** 当行向量不含1 **/
			if(!location[temp].contains(1)){
			    zero = temp;//标记没有1的行向量位置
				temp = flag;
				/** 若从头开始的位置已经存在于seqsen中，继续向下寻找起始位置  **/
				while(seqsen.contains(location[temp].indexOf(1)+1)){					
					flag++;
					temp = flag;
				}
				if(!seqsen.contains(flag-2+1))
					seqsen.add(flag-2+1);//存放从头开始的位置
			}
			/** 当seqsen中已经含该位置 **/
			else if(seqsen.contains(location[temp].indexOf(1)+1)){
				
				flag++;
				temp = flag;
				/** 若从头开始的位置已经存在于seqsen中，继续向下寻找起始位置  **/
				while(seqsen.contains(location[temp].indexOf(1)+1)||(temp==zero)){					
					flag++;
					temp = flag;
				}
				if(!seqsen.contains(flag-2+1))
					seqsen.add(flag-2+1);//存放从头开始的位置
			}
			j = location[temp].indexOf(1);
			seqsen.add(j+1);
			temp = j+2;
		}	
		
		return seqsen;		
	}
	/**
	 * 传入句子间相似度列表，转换成数组，调用insertion_sort方法
	 * @param
	 * @return
	 */
	public float[] sort(List<Float> similar){
		float[] unsorted = new float[similar.size()];
    	int count=0;
    	for(float s: similar)
    		unsorted[count++]=s;
    	
    	float[] ret = insertion_sort_double(unsorted);
    	return ret;
	}
	/**
	 * 直接插入排序double
	 * @param similar
	 * @return 
	 */
    public float[] insertion_sort_double(float[] unsorted)
    {
   	
        for (int i = 1; i < unsorted.length; i++)
        {
            if (unsorted[i - 1] > unsorted[i])
            {
                float temp = unsorted[i];
                int j = i;
                while (j > 0 && unsorted[j - 1] > temp)
                {
                    unsorted[j] = unsorted[j - 1];
                    j--;
                }
                unsorted[j] = temp;
            }
        }
        return unsorted;
    }
	/**
	 * 直接插入排序 int 
	 * @param similar
	 * @return 
	 */
    public int[] insertion_sort_int(int[] unsorted)
    {
   	
        for (int i = 1; i < unsorted.length; i++)
        {
            if (unsorted[i - 1] > unsorted[i])
            {
                int temp = unsorted[i];
                int j = i;
                while (j > 0 && unsorted[j - 1] > temp)
                {
                    unsorted[j] = unsorted[j - 1];
                    j--;
                }
                unsorted[j] = temp;
            }
        }
        return unsorted;
    }
    /**
     * 句子分段
     * @param sim
     * @param seqsen
     * @return
     */
    public int[] cutParaLoc(float[][] sim,List<Integer>seqsen){
		/** 获得每两个相邻句子之间的相似度，取两个相似度最小的截断，达到分段效果 **/
		List<Float> similar = new ArrayList<>();
		for(int i=0;i<sim.length-1;i++){
			similar.add(sim[seqsen.get(i)-1][seqsen.get(i+1)-1]);
		}

		float[] s = sort(similar);
		/** 存放最小相似度的位置 **/
		int[] flag = new int[NUM_PARA+1]; 
		flag[0] = 0;
		int k = 1;
		for(int i =0;i<NUM_PARA-1;i++){
			/** 如果从前面匹配的第一个值已经存在flag则从后面匹配第一个，该方法只能分3段，分多段需要改进算法！！！！**/
			for(int tmp=1;tmp<NUM_PARA-1;tmp++)
				if(flag[tmp]==similar.indexOf(s[i])+1){
					flag[k++] = similar.lastIndexOf(s[i])+1;
					continue;
				}
			flag[k++] = similar.indexOf(s[i])+1;
		}
		flag[NUM_PARA]=sim.length;
		/** 从小到大排序 **/
		flag = insertion_sort_int(flag);
		
		return flag;
    }
}
