package edu.ecnu.dase.sc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/** 
 * 定义数组Y，每行代表一个单词，每列代表一个句子
 * @author Iris
 */
public class CountParaForLP {
	/** 设置需取出的语料库开头字段 **/
	public static final String TAG_START_CONTENT = "";
	/** 设置需取出的语料库结尾字段 **/
	public static final String TAG_END_CONTENT = "";
	/** 取出excel的GET_COLUM列 **/
	public static final int GET_COLUM = 4;
	
	/**
	 * 将语料库切分成单独的句子，存入List<br>
	 * 过滤掉长度超过50的句子
	 * @param filename 语料库路径
	 * @param resulttemp 随机抽取的文献的行数
	 * @return list 返回句子存储结果
	 */
	public List<String> getSentence(String filename,int[] resulttemp) {

		// 制造一个数组 存储拆分完成后所有的数据
		List<String> list = new ArrayList<String>();
		String temp = null;
		try {
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filename));
			for (int l = 0; l < wb.getNumberOfSheets(); l++) {// l张表格
				Row row = null;
				for (int k = 0; k<resulttemp.length; k++) {// k行
					row = wb.getSheetAt(l).getRow(resulttemp[k]);
					Cell ckj = row.getCell(GET_COLUM - 1);
					temp = ckj.getRichStringCellValue().getString();
					// 从控制台接收一个字符串
					if (temp.length() > 0) {
						temp = temp.trim();
						
						// 接收的字符串按 , 拆分
						String inputStrings[] = temp.split(",");
						// 准备要写入的文件地址 创建一个输出流

						// 循环读取按要求拆分后的数据
						for (int i = 0; i < inputStrings.length; i++) {
							// 第二次 按。拆分循环
							String inputStringx[] = inputStrings[i].split("。");

							for (int ii = 0; ii < inputStringx.length; ii++) {
								// 第三次 按拆;分循环
								String inputStringy[] = inputStringx[ii]
										.split("；");

								for (int iii = 0; iii < inputStringy.length; iii++) {
									// 第四次 按拆！分循环
									String inputStringz[] = inputStringy[iii]
											.split("！");

									for (int iv = 0; iv < inputStringz.length; iv++) {
										// 第五次 按拆？分循环
										String inputStringa[] = inputStringz[iv]
												.split("？");

										for (int v = 0; v < inputStringa.length; v++) {

											list.add(inputStringa[v].trim());
											// System.out.println(inputStringa[v]);

										}
									}
								}
							}
						}
					}
				}
			}
			wb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

//	/**
//	 * 情感分析，剔除负面情感的句子<br>
//	 * @param allsentence 切分后所有句子
//	 * @return list 返回句子存储结果
//	 * @throws Exception 
//	 * @throws IOException 
//	 */
//	public List<String> ridnegativeSentence(List<String> allsentence) throws IOException, Exception {
//		SentenceSentiAnalysis ssa = new SentenceSentiAnalysis();		 
//		List<String> positivesentence = new ArrayList<String>();
//		for (String temp : allsentence) {
//			if(ssa.judgeSentence(temp)!=-1)
//				positivesentence.add(temp);				
//		}
//		return positivesentence;
//	}
	
	/**
	 * 获得Y数组<br>
	 * 每一行是一个关键词，每一列由0（该句子不包含该词语）、1（该句子包含该词语）组成
	 * @param keyword 所有关键词
	 * @param sentence 所有句子
	 * @return Y 返回表示关键词与句子之间包含关系的数组
	 */
	public int[][] countYij(List<String> keyword, List<String> sentence) {
		int n = keyword.size();
		int m = sentence.size();
		int Y[][] = new int[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				Y[i][j] = 0;

		for (int j = 0; j < n; j++)
			for (String temp : sentence) {
				if (temp.toLowerCase().contains(keyword.get(j).toLowerCase())) {
					Y[j][sentence.indexOf(temp)] = 1;
				}
			}
		return Y;
	}
	/**
	 * 获得Z数组<br>
	 * 每一行是一个用户定义关键词，每一列由0（该句子不包含该词语）、1（该句子包含该词语）组成
	 * @param userkeyword 所有用户定义关键词
	 * @param sentence 所有句子
	 * @return Z返回表示用户关键词与句子之间包含关系的数组
	 */
	public int[][] countZij(List<String> userkeyword, List<String> sentence) {
	    int n =userkeyword.size();
	    int m =sentence.size();
	    int Z[][] = new int[n][m];
	    for(int i=0;i<n;i++)
	    	for(int j=0;j<m;j++)
	    		Z[i][j]=0;
	    
	    for (int i = 0; i < n; i++){ 
	    	for(String temp : sentence){
	    		if(temp.toLowerCase().contains(userkeyword.get(i))){
	    			Z[i][sentence.indexOf(temp)]=1;
	    		}
	    	}
	}
	    return Z;
	}
 
	public int[][] countSen_Key(List<String> keyword, List<String>sentence) {
		int n = keyword.size();
		int m = sentence.size();
		int a[][] = new int[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				a[i][j] = 0;
		
 
		for (int j = 0; j < n; j++){
			for (String temp : sentence) {
				int count = 0;
				Result seg = ToAnalysis.parse(temp);
				for(Term term : seg){
					String item = term.getName().trim();
					if (item.equals(keyword.get(j))) {
						count++;
					}					
				}
				a[j][sentence.indexOf(temp)] = count;
			}  
		}
		return a;
	}
		
		
	
	public int[] senLong(List<String> sentence){
		int count = sentence.size();
		int[] sen = new int[count];
		int i = 0;
		for(String temp : sentence){
			sen[i++]=temp.length();
		}
		return sen;
	}
	
	/**
	 * 计算所有句子的权值
	 * @param keywords
	 * @param userkeywords
	 * @param sentence
	 * @param tfidf
	 * @return
	 */
	public List<Float> countSen(List<String> keywords, List<String> userkeywords, List<String> sentence) {
		List<Float> senscore = new ArrayList<Float>();
		for (String sen : sentence) {
			float score = 0;
			// 存储已经包含在句子集合中的关键词

			for (String kw : keywords) {
				if (sen.contains(kw)) {
					score ++;
				}
			}
			for (String ukw : userkeywords) {
				if (sen.contains(ukw))
					score ++;
			}
			senscore.add(sen.length()/score );
		}
		return senscore;
	}

}
