package edu.ecnu.dase.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * 根据需要抽取的句子数截止
 * @author iris
 *
 */
public class SubModular {
	
	/**
	 * 次模函数，抽取句子
	 * @param keywords
	 * @param userkeywords
	 * @param sentence
	 * @param tfidf
	 * @return
	 */
	public List<String> subModular(List<String> keywords, List<String> userkeywords, List<String> sentence,
			Map<String, Double> tfidf) {
		List<String> summary = new ArrayList<String>();
		String maxsentence;
		while (keywords.size() > 0) {
			if (userkeywords.size() > 0) {
				maxsentence = countMaxSen(keywords, userkeywords, sentence, tfidf);
				summary.add(maxsentence);

				List<String> newkeywords = new ArrayList<>();
				List<String> newuserkeywords = new ArrayList<>();
				newkeywords.addAll(upDate(keywords, maxsentence));
				newuserkeywords.addAll(upDate(userkeywords, maxsentence));

				keywords.clear();
				userkeywords.clear();

				keywords.addAll(newkeywords);
				userkeywords.addAll(newuserkeywords);
			} else {//当user-keywords全被包含后
				maxsentence = countMaxSen(keywords, sentence, tfidf);
				summary.add(maxsentence);

				List<String> newkeywords = new ArrayList<>();
				newkeywords.addAll(upDate(keywords, maxsentence));

				keywords.clear();

				keywords.addAll(newkeywords);
			}

		}
		return summary;
	}

	/**
	 * 计算所有句子的权值
	 * @param keywords
	 * @param userkeywords
	 * @param sentence
	 * @param tfidf
	 * @return
	 */
	public Map<String, Double> countSen(List<String> keywords, List<String> userkeywords, List<String> sentence,
			Map<String, Double> tfidf) {
		Map<String, Double> senscore = new TreeMap<String, Double>();
		for (String sen : sentence) {
			double score = 0;
			double ti = 0;
			// 存储已经包含在句子集合中的关键词

			for (String kw : keywords) {
				if (sen.contains(kw)) {
					for (String word : tfidf.keySet()) {
						ti = tfidf.get(word);
					}
					score += ti;
				}
			}
			for (String ukw : userkeywords) {
				if (sen.contains(ukw))
					score += 1;
			}
			senscore.put(sen, score / sen.length());
		}
		return senscore;
	}
	/**
	 * 计算所有句子的权值（当没有user-keywords时）
	 * @param keywords
	 * @param sentence
	 * @param tfidf
	 * @return
	 */
	public Map<String, Double> countSen(List<String> keywords, List<String> sentence, Map<String, Double> tfidf) {
		Map<String, Double> senscore = new TreeMap<String, Double>();
		for (String sen : sentence) {
			double score = 0;
			double ti = 0;
			// 存储已经包含在句子集合中的关键词

			for (String kw : keywords) {
				if (sen.contains(kw)) {
					for (String word : tfidf.keySet()) {
						ti = tfidf.get(word);
					}
					score += ti;
				}
			}
			senscore.put(sen, score / sen.length());
		}
		return senscore;
	}
	/**
	 * 计算权值最大的句子
	 * @param keywords
	 * @param userkeywords
	 * @param sentence
	 * @param tfidf
	 * @return
	 */
	public String countMaxSen(List<String> keywords, List<String> userkeywords, List<String> sentence,
			Map<String, Double> tfidf) {
		Map<String, Double> senscore = new TreeMap<String, Double>();

		senscore = countSen(keywords, userkeywords, sentence, tfidf);
		double temp = 0;
		double maxscore = 0;
		String maxsentence = null;
		for (String sen : senscore.keySet()) {
			temp = senscore.get(sen);
			if (temp > maxscore) {
				maxscore = temp;
				maxsentence = sen;
			}
		}
		return maxsentence;
	}

	/**
	 * 计算权值最大的句子（当没有user-keywords时）
	 * 
	 * @param keywords
	 * @param sentence
	 * @param tfidf
	 * @return
	 */
	public String countMaxSen(List<String> keywords, List<String> sentence, Map<String, Double> tfidf) {
		Map<String, Double> senscore = new TreeMap<String, Double>();

		senscore = countSen(keywords, sentence, tfidf);
		double temp = 0;
		double maxscore = 0;
		String maxsentence = null;
		for (String sen : senscore.keySet()) {
			temp = senscore.get(sen);
			if (temp > maxscore) {
				maxscore = temp;
				maxsentence = sen;
			}
		}
		return maxsentence;
	}



	/*
	 * 更新keyword userkeyword
	 */
	public List<String> upDate(List<String> keywords, String maxsentence) {
		List<String> newkeyword = new ArrayList<>();
		for (String word : keywords) {
			if (maxsentence.contains(word))
				continue;
			newkeyword.add(word);
		}
		return newkeyword;
	}

}
