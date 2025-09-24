package org.recap;

import org.recap.graph.Graph;
import org.recap.graph.TextRank;
import org.recap.graph.WeightedGraph;
import org.recap.sentence.Extractor;
import org.recap.sentence.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Summarizer {
    public List<String> summarize(String text, Graph.SimilarityMethods similarityMethods, Integer maxSentenseSize) {  // maxSentenseSize == null 가능.
        List<String> sentences = Splitter.split(text);  // 문단을 문장으로 분리

        // // 함수식 반올림으로 : y = (1/20)x + 2  //Desmos 그래프 복붙하면 : y=\frac{1}{20}x+2
        // // 값을 딱 맞춰야 되면 올림으로 : y=x^{0.3}+\frac{1}{50}x-0.2
        // int sentencesSize = sentences.size();
        // int recapSize = Math.round((1/20f)*sentencesSize + 2);  // 몇줄로 줄일 건지

        int sentencesSize = sentences.size();
        int minRecapSize = 1, maxRecapSize = sentencesSize;
        int recapSize = 0;  // 몇줄로 줄일 건지
        if(maxSentenseSize == null) {
            // 함수식 반올림으로 : y = (1/20)x + 2  //Desmos 그래프 복붙하면 : y=\frac{1}{20}x+2
            // 값을 딱 맞춰야 되면 올림으로 : y=x^{0.3}+\frac{1}{50}x-0.2
            recapSize = Math.round((1/20f) * sentencesSize + 2);  // 기본 계산식 적용.
            recapSize = Math.max(minRecapSize, Math.min(recapSize, maxRecapSize));  // recapSize = 1(minRecapSize) ~ recapSize ~ maxRecapSize
        }
        else {
            recapSize = Math.max(minRecapSize, Math.min(sentencesSize, maxSentenseSize));  // recapSize = 1(minRecapSize) ~ sentencesSize ~ maxSentenseSize
            if(recapSize == sentencesSize && sentencesSize-1 > 0) {
                recapSize = sentencesSize - 1;
            }
        }

        Map<String, List<String>> wordsWithSentences = Extractor.extract(sentences);  // 명사추출, 문장 내 부사제거

        // 그래프 생성
        WeightedGraph graph = Graph.makeWeightedGraph(wordsWithSentences, similarityMethods);  // 그래프 및 가중치 생성
        return TextRank.calculateTextRank(graph, recapSize).stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<String> summarizeByTextLen(String text, Graph.SimilarityMethods similarityMethods, Integer maxTextLen) {  // maxTextLen == null 가능.
        if(maxTextLen == null) {
            return summarize(text, similarityMethods, null);
        }
        List<String> sentences = Splitter.split(text);  // 문단을 문장으로 분리

        int sentencesSize = sentences.size();
        Map<String, List<String>> wordsWithSentences = Extractor.extract(sentences);  // 명사추출, 문장 내 부사제거

        WeightedGraph graph = Graph.makeWeightedGraph(wordsWithSentences, similarityMethods);  // 그래프 및 가중치 생성
        List<Map.Entry<String, Double>> rankedSentences = TextRank.calculateTextRank(graph, sentencesSize);  // TextRank 계산

        List<String> summarizedSentences = new ArrayList<>();
        int sumLen = 0;
        for(Map.Entry<String, Double> entry : rankedSentences) {
            String rankedSentence = entry.getKey();
            int len = rankedSentence.length();

            if(sumLen + len <= maxTextLen) {
                summarizedSentences.add(rankedSentence);
                sumLen += len;
            }
            else break;
        }

        // 최소 1문장은 반드시 포함할것.
        // (혹여 1문장 정도는 최대글자수를 넘어도 무방하며, 오히려 비어있는 요약이 더 치명적임.)
        if(summarizedSentences.isEmpty() && !rankedSentences.isEmpty()) {
            String firstRankedSentence = rankedSentences.get(0).getKey();
            summarizedSentences.add(firstRankedSentence);
        }

        return summarizedSentences;
    }
}
