# Recap &#8594; Recap Plus 💡

`Recap Plus`는 원작인 `Recap`을 기반으로 개선 · 확장한 라이브러리입니다.<br>
이미 훌륭한 라이브러리지만, 다음과 같은 이유로 추가 개발 후 재배포하게 되었습니다.

### 배경
1. &nbsp;프로덕션 서비스에 적용하기에, 타인이 GitHub + JitPack 배포한 모듈은 불안정합니다.
   - &#8594; 중간에 private로 전환되거나 삭제될 경우 치명적이므로, 별도 재배포해 유지가 필요한 상황.
2. &nbsp;재배포하며, 기존 코드의 잘못된 오류와 성능 누수를 개선하고자 했습니다.
   - `자카드 유사도` 파라미터를 넣었음에도 `calculateJaccardSimilarity()`가 아닌,<br>`코사인 유사도` 공식의 `calculateCosineSimilarity()`로 계산되던 휴먼 에러를 수정.
   - 정렬 과정에서 `stream().sorted()`가 2회 연속 체이닝된 성능 누수를 개선.
   - `"IllegalArgumentException: Comparison method violates its general contract"`<br>&#8594; 위 예외를 `Integer.compare()` 안전한 비교 구문으로 전환함으로써, 정렬 기준을 명확히해 해결.
3. &nbsp;서비스 내 AI 요청 전처리를 위해, 세부 조정이 가능한 커스텀 기능을 개발했습니다.
   - "사용 중인 OpenAI 모델 한도 : `TPM 분당 20만 토큰`, `Input $0.1당 100만 토큰`"<br>&#8594; 위 한도를 초과하지 않도록, 예상 비용에 따라 텍스트 길이를 유동적으로 조정해야 하는 상황.
   - 원하는 길이에 맞춰 텍스트를 요약할 수 있도록, 기존 TextRank 로직에 커스텀 파라미터를 도입.<br>&#8594; 최대 글자수(`maxTextLen`) 및 문장수(`maxSentenseSize`)를 통해 개인화 가능.

### 링크
>[ 출처 ]<br>
◦&nbsp;&nbsp;`Recap` Repository (origin) :&nbsp;&nbsp;https://github.com/team-recap/recap<br>
◦&nbsp;&nbsp;`Recap Plus` Repository (custom) :&nbsp;&nbsp;https://github.com/shj1106/recap-plus<br>
◦&nbsp;&nbsp;`Recap Plus` Developer :&nbsp;&nbsp;https://github.com/tkguswls1106 (shj1106 동일인)<br><br>
[ 실서비스 ]<br>
◦&nbsp;&nbsp;`Recap Plus` 적용된 서비스 :&nbsp;&nbsp;https://github.com/OnlineMemo<br>
◦&nbsp;&nbsp;`Recap Plus` 적용된 PR :&nbsp;&nbsp;[https://github.com/OnlineMemo/.../pull/11](https://github.com/OnlineMemo/backend/pull/11)<br>
◦&nbsp;&nbsp;`Recap Plus` 적용된 코드 :&nbsp;&nbsp;[https://github.com/OnlineMemo/.../MemoService#L256](https://github.com/OnlineMemo/backend/blob/5fbe9408ce3d06f1dea785e3fe0a855b1ce3d550/src/main/java/com/shj/onlinememospringproject/service/impl/MemoServiceImpl.java#L256)

<br>

---

<br>

# Recap&nbsp;&nbsp;[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/facebook/react/blob/main/LICENSE)
Recap은 텍스트 요약을 위한 Java 라이브러리입니다.

* **TextRank 알고리즘** :&nbsp;&nbsp;Recap은 TextRank 알고리즘을 사용하여 텍스트 요약을 빠르게 수행할 수 있습니다.
* **코사인/자카드 유사도 제공** :&nbsp;&nbsp;문장 간 유사도를 측정하는 계산법으로, 코사인과 자카드 유사도 방식을 선택할 수 있습니다.
* **순수 Java 라이브러리** :&nbsp;&nbsp;100% Java로 구현된 라이브러리로서 Java가 설치된 곳이라면 어디서든지 사용이 가능합니다.
* **낮은 의존도** :&nbsp;&nbsp;형태소 분석기 외에는 라이브러리에 대한 의존성이 없으므로 간편하게 텍스트 요약을 구현할 수 있습니다.

<br>

## 사용 방법
형태소 분석을 위한 `한나눔 라이브러리`와, `Recap 또는 Recap Plus` 라이브러리를 추가합니다.

### Gradle
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "kr.bydelta:koalanlp-hnn:2.1.4:assembly"
    // implementation 'com.github.team-recap:recap:0.0.6'  // (origin: Recap)
    implementation 'com.github.shj1106:recap-plus:v2.1.0'  // (custom: Recap Plus)
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>kr.bydelta</groupId>
    <artifactId>koalanlp-hnn</artifactId>
    <version>2.1.4</version>
</dependency>
<dependency>
    <!-- (origin: Recap) -->
    <!--
    <groupId>com.github.team-recap</groupId>
    <artifactId>recap</artifactId>
    <version>v0.0.6</version>
    -->
    <!-- (custom: Recap Plus) -->
    <groupId>com.github.shj1106</groupId>
    <artifactId>recap-plus</artifactId>
    <version>v2.1.0</version>
</dependency>
```

### 텍스트 요약
```java
// 요약할 텍스트
String text = "네. 제가 가져온 아이디어는 소셜 로그인을 쉽게 구축할 수 있는 라이브러리입니다. 웹 서비스를 제작해보신 분들은 알겠지만 소셜 로그인을 구현하는게 굉장히 어렵습니다. 소셜 플랫폼과의 연동뿐만아니라 해당 과정을 클라이언트와 연동하는 과정이 생각보다 많이 복잡합니다. 그래서 이 과정을 차라리 라이브러리화 해서 다양한 소셜 플랫폼을 지원할 뿐만아니라 쉽게 이용할 수 있도록 제작해보고 싶습니다.";


// (origin: Recap)

/*
Summarizer summarizer = new Summarizer();  // Summarizer 객체 생성
List<String> summarizedText = summarizer.summarize(
    text,  // 요약할 원본 텍스트
    Graph.SimilarityMethods.COSINE_SIMILARITY  // 문장 간 유사도 계산 및 측정법 (COSINE 또는 JACCARD)
);

System.out.println(summarizedText);
// ==> [ "웹 서비스를 제작해보신 분들은 알겠지만 소셜 로그인을 구현하는게 어렵습니다.",
//       "이 과정을 라이브러리화 해서 다양한 소셜 플랫폼을 지원할 쉽게 이용할 수 있도록 제작해보고 싶습니다." ]
*/


// (custom: Recap Plus)

// - 방법 1 (기본 옵션)
Summarizer summarizer = new Summarizer();  // Summarizer 객체 생성
List<String> summarizedText1 = summarizer.summarize(
    text,  // 요약할 원본 텍스트
    Graph.SimilarityMethods.COSINE_SIMILARITY  // 문장 간 유사도 계산 및 측정법 (COSINE 또는 JACCARD)
);

// - 방법 2 (최대 문장수 기준)
List<String> summarizedText2 = summarizer.summarizeByTextLen(
    text,
    Graph.SimilarityMethods.COSINE_SIMILARITY,
    2  // 최대 요약 전체문장 수 제한
);

// - 방법 3 (최대 글자수 기준)
List<String> summarizedText3 = summarizer.summarizeByTextLen(
    text,
    Graph.SimilarityMethods.COSINE_SIMILARITY,
    100  // 최대 요약 전체글자 수 제한
);

System.out.println(summarizer.joinText(summarizedText3, null));
// ==> "웹 서비스를 제작해보신 분들은 알겠지만 소셜 로그인을 구현하는게 어렵습니다.\n
//      이 과정을 라이브러리화 해서 다양한 소셜 플랫폼을 지원할 쉽게 이용할 수 있도록 제작해보고 싶습니다."

System.out.println(summarizer.joinText(summarizedText3, " "));
// ==> "웹 서비스를 제작해보신 분들은 알겠지만 소셜 로그인을 구현하는게 어렵습니다. 이 과정을 라이브러리화 해서 다양한 소셜 플랫폼을 지원할 쉽게 이용할 수 있도록 제작해보고 싶습니다."
```

<br>

## 아키텍처
![structure](https://github.com/shj1106/recap-plus/blob/main/images/structure.png)

<br>

## 라이선스
[MIT license](https://github.com/shj1106/recap-plus/blob/main/LICENSE)
