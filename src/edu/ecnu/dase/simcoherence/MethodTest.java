package edu.ecnu.dase.simcoherence;
//package edu.ecnu.dase.conherence;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.ansj.domain.Result;
//import org.ansj.domain.Term;
//import org.ansj.splitWord.analysis.ToAnalysis;
//
//
//public class MethodTest {
//	/**
//	 * 将语料库切分成单独的句子，存入List<br>
//	 * 过滤掉长度超过50的句子
//	 * 
//	 * @param filename
//	 *            语料库路径
//	 * @param resulttemp
//	 *            随机抽取的文献的行数
//	 * @return list 返回句子存储结果
//	 */
//	public static List<String> getSentence(String str) {
//
//		// 制造一个数组 存储拆分完成后所有的数据
//		List<String> list = new ArrayList<String>();
//		String temp = null;
//
//		temp = str;
//		// 从控制台接收一个字符串
//		if (temp.length() > 0) {
//			temp = temp.trim();
//
//			// 接收的字符串按 , 拆分
//			String inputStrings[] = temp.split(",");
//			// 准备要写入的文件地址 创建一个输出流
//
//			// 循环读取按要求拆分后的数据
//			for (int i = 0; i < inputStrings.length; i++) {
//				// 第二次 按。拆分循环
//				String inputStringx[] = inputStrings[i].split("。");
//
//				for (int ii = 0; ii < inputStringx.length; ii++) {
//					// 第三次 按拆;分循环
//					String inputStringy[] = inputStringx[ii].split("；");
//
//					for (int iii = 0; iii < inputStringy.length; iii++) {
//						// 第四次 按拆！分循环
//						String inputStringz[] = inputStringy[iii].split("！");
//
//						for (int iv = 0; iv < inputStringz.length; iv++) {
//							// 第五次 按拆？分循环
//							String inputStringa[] = inputStringz[iv].split("？");
//
//							for (int v = 0; v < inputStringa.length; v++) {
//
//								list.add(inputStringa[v].trim());
//								System.out.println(inputStringa[v]);
//
//							}
//						}
//					}
//				}
//			}
//		}
//
//		return list;
//	}
//
//	/**
//	 * 读TXT文件内容
//	 * @param fileName
//	 * @return
//	 */
//	public static String readTxtFile(String filePath) throws Exception {
//		File fileName = new File(filePath);
//		String result = "";
//		FileReader fileReader = null;
//		BufferedReader bufferedReader = null;
//		try {
//			fileReader = new FileReader(fileName);
//			bufferedReader = new BufferedReader(fileReader);
//			try {
//				String read;
//				while ((read = bufferedReader.readLine()) != null) {
//					result += read + "\r\n";
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (bufferedReader != null) {
//				bufferedReader.close();
//			}
//			if (fileReader != null) {
//				fileReader.close();
//			}
//		}
////		System.out.println("读取出来的文件内容是：" + "\r\n" + result);
//		return result;
//	}	
//	/**
//	 * 计算句子间jaccard相似度
//	 * @return 
//	 */
//	public static float jaccard(String str1, String str2){
//		List<String> list1 = new ArrayList<String>();
//		List<String> list2 = new ArrayList<String>();
//		Result result = ToAnalysis.parse(str1);
//		for (Term term : result) {
//			String item = term.getName().trim();
//			item = item.replaceAll("[\\pP\\pS\\pZ]", "");
//			if (item.length() > 0 && !list1.contains(item) && !item.equals("")) {				
//				list1.add(item);
//			}
//	    }
//	    result = ToAnalysis.parse(str2);
//		for (Term term : result) {
//			String item = term.getName().trim();
//			item = item.replaceAll("[\\pP\\pS\\pZ]", "");
//			if (item.length() > 0 && !list2.contains(item) && !item.equals("")) {				
//				list2.add(item);
//			}
//	    }
//		/** 计算分子 **/
//		int count = 0;
//		for(String s1 : list1){
//			for(String s2 : list2){
//				if(s1.equals(s2))
//					count++;
//			}				
//		}
//		
//		/** 计算句子的jaccard相似度 **/
//		float sim = (float)count/(list1.size()+list2.size()-count);
//		
//		return sim;		
//	}
//	/**
//	 * 计算相似度矩阵
//	 * @param filepath
//	 * @return
//	 * @throws Exception
//	 */
//	public static float[][] countJaccardSim(String filepath) throws Exception{
//		
//		String txt = readTxtFile(filepath);
//		List<String> list = getSentence(txt);
//		float[][] sim = new float[list.size()][list.size()];
//		int i = 0;
//		int j = 0;
//		for(String s1 : list){
//			for(String s2 : list){
//				if(i==j)
//					sim[i][j++] = 1;
//				else
//					sim[i][j++] = jaccard(s1, s2);	
//			}
//			j = 0;
//			i++;
//		}
//		return sim;
//	}
//	
//	/**
//	 * 
//	* @Title: calculateCos
//	* @Description: 计算余弦相似性
//	* @param @param first
//	* @param @param second
//	* @param @return    
//	* @return Double   
//	* @throws
//	 */
//	private static Double calculateCos(int[] v1, int[] v2){
//		
//		//计算相似度  
//        double vectorFirstModulo = 0.00;//向量1的模  
//        double vectorSecondModulo = 0.00;//向量2的模  
//        double vectorProduct = 0.00; //向量积  
//        int secondSize=v2.length;
//		for(int i=0;i<v1.length;i++){
//			if(i<secondSize){
//				vectorSecondModulo+=v2[i]*v2[i];
//				vectorProduct+=v1[i]*v2[i];
//			}
//			vectorFirstModulo+=v1[i]*v1[i];
//		}
//	   return vectorProduct/(Math.sqrt(vectorFirstModulo)*Math.sqrt(vectorSecondModulo));
//	}
//	/**
//	 * 一元模型相似度
//	 * @param str1
//	 * @param str2
//	 * @return
//	 */
//	public static double ngram(String str1 , String str2){
//		List<String> list1 = new ArrayList<String>();
//		List<String> list2 = new ArrayList<String>();
//		Result result = ToAnalysis.parse(str1);
//		for (Term term : result) {
//			String item = term.getName().trim();
//			item = item.replaceAll("[\\pP\\pS\\pZ]", "");
//			if (item.length() > 0 && !list1.contains(item) && !item.equals("")) {				
//				list1.add(item);
//			}
//	    }
//	    result = ToAnalysis.parse(str2);
//		for (Term term : result) {
//			String item = term.getName().trim();
//			item = item.replaceAll("[\\pP\\pS\\pZ]", "");
//			if (item.length() > 0 && !list2.contains(item) && !item.equals("")) {				
//				list2.add(item);
//			}
//	    }
//		/** 存储两个句子的所有词（不重复）**/
//		List<String> list = new ArrayList<String>();
//		for(String temp : list1){
//			if(!list.contains(temp)){
//				list.add(temp);
//			}
//		}
//		for(String temp : list2){
//			if(!list.contains(temp)){
//				list.add(temp);
//			}
//		}
//		int[] vector1 = new int[list.size()];
//		int[] vector2 = new int[list.size()];
//		int i = 0;
//		for(String temp : list){
//			if(list1.contains(temp))
//				vector1[i++] = 1;
//			else
//				i++;
//		}
//		i = 0;
//		for(String temp : list){
//			if(list2.contains(temp))
//				vector2[i++] = 1;
//			else
//				i++;
//		}
//		
//		double sim = calculateCos(vector1, vector2);
//		return sim;
//			
//	}
//	public static double[][] countNgramSim(String filepath) throws Exception{
//
//		String txt = readTxtFile(filepath);
//		List<String> list = getSentence(txt);
//		double[][] sim = new double[list.size()][list.size()];
//		int i = 0;
//		int j = 0;
//		for(String s1 : list){
//			for(String s2 : list){
//				if(i==j)
//					sim[i][j++] = 1;
//				else
//					sim[i][j++] = ngram(s1, s2);	
//			}
//			j = 0;
//			i++;
//		}
//		return sim;
//	}
//	public static void main(String[] args) throws Exception {
//		String filepath = "/Users/iris/Documents/LDA/2.txt";
//		float[][] sim = countJaccardSim(filepath);
//		for(int i = 0;i<sim.length;i++){
//			for(int j = 0;j<sim.length;j++)
//				System.out.printf("%10s",sim[i][j]+"\t");
//			System.out.println();
//		}
//	}
//
//}
