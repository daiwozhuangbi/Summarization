package edu.ecnu.dase.sc;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/** 
 * 取出最终句子集合存入List中，返回
 * @author Iris
 */
public class MinSentenceCover {

	/**
	 * 取出最小句子集合中的句子，并存入List中
	 * @param sentence 所有句子
	 * @param xmap 所有词语得分结果
	 * @return mincover 存储最终句子
	 */
	public List<String> minSCover(List<String> sentence, Map<Integer, Double> xmap) {
		Iterator<Entry<Integer, Double>> iter = xmap.entrySet().iterator();
		double temp;
		List<String> mincover = new ArrayList<String>();
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = iter.next();
			temp = (double) entry.getValue();
			if (temp > 0.8) {
				mincover.add(sentence.get((int) entry.getKey() - 1));
			}
		}
		return mincover;
		
	}
	

}
