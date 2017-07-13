package edu.ecnu.dase.sm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * original summary
 * @author iris
 *
 */
public class Summary {
	/** 每次取GET_NUM_DOCS篇文献 **/
	public static final int GET_NUM_DOCS = 10;
	/** 自定义关键词的个数 一篇文本5个关键词 **/
	public static final int KeyWordCount = 3;

	public void getSummary(String filePath, String outfilePath, int n) throws Exception {

		for (int i = 0; i < n; i++) {
			int[] rowofdocs = new int[GET_NUM_DOCS];
			for (int j = 0; j < GET_NUM_DOCS; j++)
				rowofdocs[j] = j + 1;

			// keyword个数
			int keywordnumber = KeyWordCount * GET_NUM_DOCS;
			// //摘句子个数
			// int numsen =(int)(NumSen * GET_NUM_DOCS)+1;

			CountKeyWords countkeyword = new CountKeyWords();
			// 计算tfidf
			Map<Integer, Map<String, Double>> tfIdfMap = new HashMap<Integer, Map<String, Double>>();
			tfIdfMap = countkeyword.tfidf(filePath, rowofdocs, keywordnumber);

			// merge 后 每个单词的tfidf ->存在resultmap
			Map<String, Double> resultmap = countkeyword.mergeMap(tfIdfMap);

			// 求keywords

			List<String> keywords = new ArrayList<String>();
			keywords = countkeyword.countKeyWords(filePath, rowofdocs, keywordnumber, resultmap);
//公司, 引用, 医生, 主题, 修改, 句子, 注意事项, 在线, 医学论文, 代发, 主旨, 准确, 专题, 付款, 中文, 翻译, 企业, 技巧, sci, 期刊, 作者, 服务, 语言, 结构, 英语, 是什么, 原创, 专业性, 创新, 帮]
			// 对用户自定义的keyword列分词,结果存入userKeywords，去重
			List<String> userKeywords = new ArrayList<String>();
			GetUserKeyWords usk = new GetUserKeyWords();
			userKeywords = usk.cutKeywords(filePath, rowofdocs);
//进行, 修改, 医学, 防止, 的, 抄袭, 翻译, 公司, 英文, 代发, 技巧, 医生, 方法, 关键, 时, sci, 如何, 服务, 点, 论文, 发表]
			// 切分文本成 sentence
			CountPara c = new CountPara();
			List<String> allsentence = new ArrayList<String>();
			allsentence = c.getSentence(filePath, rowofdocs);
			if (allsentence.size() == 0) {
				throw new Exception("请先确认您传入的文本至少含有一句话（由。；！？结尾的作为一句）");
			}

			SubModular s = new SubModular();
			List<String> summary = new ArrayList<String>();
			summary = s.subModular(keywords, userKeywords, allsentence, resultmap);			
			inputTxt(outfilePath, summary, i);
			
		}
	}

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
