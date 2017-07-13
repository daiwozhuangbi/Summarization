package edu.ecnu.dase.simcoherence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import core.algorithm.lda.LDA;
import core.algorithm.lda.LDACmdOption;
import edu.ecnu.dase.sm.FileOperation;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class KMeans {
	
	/**
	 * 将待预测文本分词、去停用词
	 * @param sen
	 * @throws Exception
	 */
		public void genpredoc(List<String> sen) throws Exception {

			// 将分词后的文本信息存入输出文件
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(new FileOutputStream(new File("models/casestudy-china/ESpredictDoc.txt"), false), true);
				pw.print(sen.size());
				pw.println();
				@SuppressWarnings("resource")
				BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File("stop_words_ch.txt"))));
				Set<String> stopWordSet = new HashSet<String>();
				// 初始化停用词集
				String stopWord = null;
				try {
					for (; (stopWord = StopWordFileBr.readLine()) != null;) {
							stopWordSet.add(stopWord);
					}
				} catch (IOException e) {
					throw new Exception("停用词文本读取出现问题！");
				}
				for (int i = 0;i<sen.size();i++) {
					Result result = ToAnalysis.parse(sen.get(i));
					for (Term term : result) {
						String item = term.getName().trim();
						if ((item.length() > 0) && (!stopWordSet.contains(item))) {
						    pw.print(item + " ");
						}
					}
					pw.println();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
/**
 * 聚类算法
 * @param filePath
 * @return
 * @throws Exception
 */
	public Map<Integer, Integer> Kmeans(String filePath) throws Exception {
		Instances ins = null;
		SimpleKMeans KM = null;
		File file = new File(filePath);
		ArffLoader loader = new ArffLoader();
		Map<Integer, Integer> clustermap = new HashMap<Integer, Integer>();
			
		try {
			loader.setFile(file);

			ins = loader.getDataSet();

			KM = new SimpleKMeans();
			KM.setNumClusters(4);
			KM.buildClusterer(ins);// 开始进行聚类

			for (int i = 0; i < ins.numInstances(); i++) {
			
				clustermap.put(i, KM.clusterInstance(ins.instance(i))+1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return clustermap;
	}
	/**
	 * 获取分类结果
	 * @return clustertreemap 返回分类结果
	 * @throws Exception 

	 */
	public Map<Integer, List<Integer>> getClusterResult(List<String> sen,int k) throws Exception{
		/** 获得句子主题分布 **/
		LDACmdOption option = new LDACmdOption();
		option.inf = true;
		option.K = 30;
		option.savestep = 1000;
		genpredoc(sen);
		LDA lda = new LDA();
		lda.run();
		
		/** 对句子聚类 **/
		String file = "models/casestudy-china/ESpredictDoc.txt.model-final.theta";		
		Map<Integer, Integer> clustermap = new HashMap<Integer, Integer>();
		Map<Integer, List<Integer>> cluster = new HashMap<Integer, List<Integer>>();
	
		int ldatopic = option.K;
		
		//修改predictDoc.txt格式
		 FileOperation f = new FileOperation();
		 String result = f.readTxtFile(new File(file));
		 f.writeTxtFile("@RELATION predictDoc.txt.model-final.theta",new File(file));
		 for(int i =1;i<=ldatopic;i++){
			 f.contentToTxt(file, "@ATTRIBUTE "+i+" REAL");
		 }
		 f.contentToTxt(file, "@DATA");
		 f.contentToTxt(file,result);
		
		KMeans km = new KMeans();
		clustermap =km.Kmeans(file);
		
		for (int i = 1; i <= k; i++) {
			List<Integer> topicnum = new ArrayList<Integer>();
			Set<Integer> senNo = clustermap.keySet();
			for (Integer No : senNo) {
				int tmp = clustermap.get(No);
				if (tmp == i) {
					topicnum.add(No);
				}
			}
			cluster.put(i, topicnum);
		}

		return cluster;
	}
}
