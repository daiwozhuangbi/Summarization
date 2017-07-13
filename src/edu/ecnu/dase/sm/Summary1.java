package edu.ecnu.dase.sm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.ecnu.dase.simcoherence.KMeans;
import edu.ecnu.dase.simcoherence.Similarity;
/**
 * 加入连贯性，相似度计算方法：使用LDA主题分布求句子的主题分布向量
 * 相似的句子放在相邻位置
 * 
 * @author iris
 */
public class Summary1 {
	/** 每次取GET_NUM_DOCS篇文献 **/
	public static final int GET_NUM_DOCS = 10;
	/** 自定义关键词的个数 一篇文本5个关键词 **/
	public static final int KeyWordCount = 3;
	/** 分段数 **/
	public static final int NUM_PARA = 3;

	public void getSummary(String filePath, String outfilePath, int n) throws Exception {

		for (int i = 0; i < n; i++) {
			int[] rowofdocs = new int[GET_NUM_DOCS];
			for (int j = 0; j < GET_NUM_DOCS; j++)
				rowofdocs[j] = j + 1;

			/** keyword个数 **/
			int keywordnumber = KeyWordCount * GET_NUM_DOCS;

			CountKeyWords countkeyword = new CountKeyWords();
			/** 计算tfidf **/
			Map<Integer, Map<String, Double>> tfIdfMap = new HashMap<Integer, Map<String, Double>>();
			tfIdfMap = countkeyword.tfidf(filePath, rowofdocs, keywordnumber);

			/** merge 后 每个单词的tfidf ->存在resultmap **/
			Map<String, Double> resultmap = countkeyword.mergeMap(tfIdfMap);

			/** 求keywords **/
			List<String> keywords = new ArrayList<String>();
			keywords = countkeyword.countKeyWords(filePath, rowofdocs, keywordnumber, resultmap);

			/** 对用户自定义的keyword列分词,结果存入userKeywords，去重 **/
			List<String> userKeywords = new ArrayList<String>();
			GetUserKeyWords usk = new GetUserKeyWords();
			userKeywords = usk.cutKeywords(filePath, rowofdocs);

			/** 切分文本成sentence **/
			CountPara c = new CountPara();
			List<String> allsentence = new ArrayList<String>();
			allsentence = c.getSentence(filePath, rowofdocs);
			if (allsentence.size() == 0) {
				throw new Exception("请先确认您传入的文本至少含有一句话（由。；！？结尾的作为一句）");
			}
            /** 文本抽取 **/
			SubModular s = new SubModular();
			List<String> summary = new ArrayList<String>();
			summary = s.subModular(keywords, userKeywords, allsentence, resultmap);
		
			/** 连贯性 **/
			//计算每个句子的LDA主题分布（聚类算法没用上，但是里面的预测算法可用）
			KMeans km = new KMeans();
			km.getClusterResult(summary, 3);
			
			Similarity sim = new Similarity();
			@SuppressWarnings("unchecked")
			Vector<Double>[]z = new Vector[summary.size()];
			z = sim.getVectorZ(summary);
			
			/** 构造一个句子序号列表,因为CosSimMatrix第一个参数传入的是int型列表 **/
			List<Integer> senclass = new ArrayList<>();
			for(int k = 0 ;k<summary.size();k++)
				senclass.add(k)	;
			/** 计算句子相似度矩阵 **/
			float[][] similar = sim.CosSimMatrix(senclass,z);
			
			if(summary.size()>2){
				List<Integer> seq = sim.coherentMatrix2seq(similar);
				/** 需切分段落的句子位置 **/
				int[] flag = sim.cutParaLoc(similar, seq);
				
				for(int j=0;j<NUM_PARA;j++){
					List<String>para = new ArrayList<>();
					/** 取出每个段落的句子 **/
					for(int k=flag[j];k<flag[j+1];k++){						 
						para.add(summary.get(seq.get(k)-1));
					}
					inputTxt(outfilePath, para, i);
				}
			}
			else
				inputTxt(outfilePath, summary, i);
				
		}
			
		
	}
//			Map<Integer, List<Integer>> clustermap = new HashMap<Integer, List<Integer>>();
//			KMeans km = new KMeans();
//			clustermap = km.getClusterResult(summary, 3);
////			
//			Similarity sim = new Similarity();
//			@SuppressWarnings("unchecked")
//			Vector<Double>[]z = new Vector[summary.size()];
//			z = sim.getVectorZ(summary);
//			for(Integer cl : clustermap.keySet()){
//				/** 取出同一类的句子 **/
//				List<String> sencl = new ArrayList<String>();
//				List<Integer> temp = clustermap.get(cl);
//				for(Integer seni : temp)
//					sencl.add(summary.get(seni));				
//				
//				if(clustermap.get(cl).size()>2){
//					sim.coherentMatrix(clustermap.get(cl),z);
//				    
//				}
//				else
//				   inputTxt(outfilePath, sencl, i);
//					
//			}
	/**
	 * 将List中的句子写入txt
	 * 
	 * @param outfilePath
	 *            输出路径
	 * @param minscover
	 *            待写入的List
	 * @param i
	 *            txt名称
	 * @throws IOException
	 *             IO异常
	 */
	public void inputTxt(String outfilePath, List<String> summary, int i) throws IOException {
		// 存储到i.txt文档中
		File file = new File(outfilePath + "/ouput");
		if (!file.exists() && !file.isDirectory())
			file.mkdir();

		String temp = i + ".txt";
		File filename = new File(file + "/" + temp);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename, true), 100);
			bw.write("    ");
			for (String str : summary) {
				if (str.matches("[0-9、.]+.*"))
					str = str.substring(2);
				if (str.matches("第[一二三四五六七八九十]+.*"))
					str = str.substring(3);
				bw.write(str + "。");
			}
			bw.newLine();
		} catch (IOException e) {
			System.out.println("写入文件出错，输入文件路径不正确！");
			e.printStackTrace();
		} finally {
			if (bw != null) {
				bw.flush();
				bw.close();
			}
		}
	}
}
