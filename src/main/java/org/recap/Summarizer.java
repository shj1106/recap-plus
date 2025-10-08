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

    // 텍스트 요약 (기본 옵션)
    public List<String> summarize(String text, Graph.SimilarityMethods similarityMethods) {
        List<String> sentences = Splitter.split(text);  // 문단을 문장으로 분리
        int sentenceSize = sentences.size();
        int minRecapSize = 1, maxRecapSize = sentenceSize;
        int recapSize = 0;  // 몇 줄(문장)로 줄일건지 (= recapSentenseSize 의미)

        // 함수식 반올림으로 : y = (1/20)x + 2
        // Desmos 그래프 복붙하면 : y=\frac{1}{20}x+2
        // 값을 딱 맞춰야 되면 올림으로 : y=x^{0.3}+\frac{1}{50}x-0.2
        recapSize = Math.round((1/20f) * sentenceSize + 2);  // 기본 계산식 적용.
        recapSize = Math.max(minRecapSize, Math.min(recapSize, maxRecapSize));
        // ==> recapSize 값 범위 : 1(minRecapSize) ~ recapSize ~ maxRecapSize

        return getRankedSentences(sentences, similarityMethods, recapSize, null);
    }

    // 텍스트 요약 (최대 문장수 기준)
    public List<String> summarizeBySentenseSize(String text, Graph.SimilarityMethods similarityMethods, int maxSentenseSize) {
        List<String> sentences = Splitter.split(text);  // 문단을 문장으로 분리
        int sentenceSize = sentences.size();
        int minRecapSize = 1, maxRecapSize = sentenceSize;
        int recapSize = 0;  // 몇 줄(문장)로 줄일건지 (= recapSentenseSize 의미)

        recapSize = Math.max(minRecapSize, Math.min(sentenceSize, maxSentenseSize));
        if(recapSize == sentenceSize && sentenceSize-1 > 0) {
            recapSize = sentenceSize - 1;
        }
        // ==> recapSize 값 범위 : 1(minRecapSize) ~ sentenceSize ~ maxSentenseSize

        return getRankedSentences(sentences, similarityMethods, recapSize, null);
    }

    // 텍스트 요약 (최대 글자수 기준)
    public List<String> summarizeByTextLen(String text, Graph.SimilarityMethods similarityMethods, int maxTextLen) {
        List<String> sentences = Splitter.split(text);  // 문단을 문장으로 분리
        int sentenceSize = sentences.size();
        int recapSize = sentenceSize;  // (= recapSentenseSize 의미)
        // ==> recapSize 값 범위 : sentenceSize

        return getRankedSentences(sentences, similarityMethods, recapSize, maxTextLen);
    }

    // 요약된 문장들 하나로 합치기
    public String joinText(List<String> summarizedSentences, String delimiter) {
        if(summarizedSentences == null || summarizedSentences.isEmpty()) return "";
        if(delimiter == null) delimiter = "\n";

        StringBuilder summarizedStb = new StringBuilder();  // 또는 'String.join(delimiter, summarizedSentences);'
        int summarizedLen = summarizedSentences.size();
        for(int i=0; i<summarizedLen-1; i++) {
            summarizedStb.append(summarizedSentences.get(i)).append(delimiter);
        }
        if(summarizedLen > 0) summarizedStb.append(summarizedSentences.get(summarizedLen-1));

        return summarizedStb.toString();
    }


    // ========== 유틸성 메소드 (private) ========== //

    // 텍스트 요약 내 공통 로직
    private List<String> getRankedSentences(List<String> sentences, Graph.SimilarityMethods similarityMethods, int recapSentenseSize, Integer maxTextLen) {  // maxTextLen == null 가능.
        Map<String, List<String>> wordsWithSentences = Extractor.extract(sentences);  // 명사추출, 문장 내 부사제거
        WeightedGraph graph = Graph.makeWeightedGraph(wordsWithSentences, similarityMethods);  // 그래프 및 가중치 생성
        List<Map.Entry<String, Double>> rankedSentenceEntries = TextRank.calculateTextRank(graph, recapSentenseSize, maxTextLen);  // TextRank 계산

        return rankedSentenceEntries.stream()
                .map(Map.Entry::getKey)  // 문장만 추출
                .collect(Collectors.toList());
    }
}
