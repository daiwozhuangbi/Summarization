package edu.ecnu.dase.sc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;


/**
 * 自动生成文本文摘（加上句子权值）
 * 
 * @author Iris
 */
public class GetSummarization1 {
	/** 每次取GET_NUM_DOCS篇文献 **/
	public static final int GET_NUM_DOCS = 10;
	/** 自定义关键词的个数 一篇文本5个关键词 **/
	public static final int KeyWordCount = 3;

	/**
	 * 获得文本摘要（自定义生成篇数）<br>
	 * 1.计算关键词 2.获得用户自定义关键词 3.获得最小覆盖关键词的句子集合
	 * 
	 * @param filePath
	 *            语料库的存放路径
	 * @param outfilePath
	 *            输出文摘存放路径
	 * @param n
	 *            生成文摘的篇数
	 * @param kmtopic 需要聚类的个数，也就是需要的段数
	 * @throws Exception
	 *             关闭流出现异常
	 */
	public void getSummarization(String filePath, String outfilePath,int n) throws Exception {
		for (int i = 0; i < n; i++) {

			int[] rowofdocs = new int[GET_NUM_DOCS];
			// RandomNumofRows randomnumofrows=new RandomNumofRows();
			// rowofdocs=randomnumofrows.getRow0fContent(filePath,GET_NUM_DOCS);
			for (int j = 0; j < GET_NUM_DOCS; j++)
				rowofdocs[j] = j + 1;

  			// keyword个数
				int keywordnumber = KeyWordCount * GET_NUM_DOCS;
 
				// 求keywords
				CountKeyWords countkeyword = new CountKeyWords();
				List<String> keywords = new ArrayList<String>();
				keywords = countkeyword.countKeyWords(filePath, rowofdocs, keywordnumber);
//公司, 引用, 医生, 主题, 修改, 句子, 注意事项, 在线, 医学论文, 代发, 主旨, 准确, 专题, 付款, 中文, 翻译, 企业, 技巧, sci, 期刊, 作者, 服务, 语言, 结构, 英语, 是什么, 原创, 专业性, 创新, 帮]				
				List<String> userKeywords = new ArrayList<String>();
				GetUserKeyWords usk = new GetUserKeyWords();
				userKeywords = usk.cutKeywords(filePath, rowofdocs);
//进行, 修改, 医学, 防止, 的, 抄袭, 翻译, 公司, 英文, 代发, 技巧, 医生, 方法, 关键, 时, sci, 如何, 服务, 点, 论文, 发表]
				// 求sentence

				CountParaForLP c = new CountParaForLP();
				List<String> allsentence = new ArrayList<String>();
				allsentence = c.getSentence(filePath, rowofdocs);
			
				if(allsentence.size()==0){
					throw new Exception("请先确认您传入的文本至少含有一句话（由。；！？结尾的作为一句）");
				}				

				// 求Yij Zij
				int[][] Y;
				Y = c.countYij(keywords, allsentence);

				int[][] Z;
				Z = c.countZij(userKeywords, allsentence);

				//计算句子权值
				List<Float> sen = new ArrayList<Float>();
				sen = c.countSen(keywords, userKeywords, allsentence);
				
				// minimize ∑Xi
				/** 存储每个句子的得分 **/
				Map<Integer, Map<Integer, Double>> flagANDresultoflpmap = new TreeMap<Integer, Map<Integer, Double>>();
				LinearProgramming1 linearprogramsolver = new LinearProgramming1();
				flagANDresultoflpmap = linearprogramsolver.GLPK(Y, Z, allsentence, keywordnumber,sen);

				// 取出resultoflpmap 存储每个句子的得分
				Map<Integer, Double> resultoflpmap = null;
				for (Map<Integer, Double> v : flagANDresultoflpmap.values()) {
					resultoflpmap = v;
				}

				// 输出最小覆盖的句子集合
				/** 存储最小句子集合 **/
				List<String> minscover = new ArrayList<String>();
				MinSentenceCover msc = new MinSentenceCover();
				minscover = msc.minSCover(allsentence, resultoflpmap);

				inputTxt(outfilePath, minscover, i);
		}
	
	}
/**
 * 将List中的句子写入txt
 * @param outfilePath 输出路径	
 * @param minscover 待写入的List
 * @param i txt名称
 * @throws IOException IO异常
 */
	public void inputTxt(String outfilePath, List<String> minscover, int i) throws IOException {
		// 存储到i.txt文档中
		File file = new File(outfilePath+"/ouput");
		if(!file.exists() && !file.isDirectory())
			file.mkdir();
			
		String temp = i + ".txt";
		File filename = new File(file + "/" + temp);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filename, true), 100);
			bw.write("    ");
			for (String str : minscover) {
				if (str.matches("[0-9、.]+.*"))
					str = str.substring(2);
				if(str.matches("第[一二三四五六七八九十]+.*"))
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
