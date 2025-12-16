# Spring Sunshine Precourse

Spring Boot 기반 날씨 정보 조회 및 LLM을 활용한 날씨 요약 및 의상 추천 서비스입니다.

## 목차

- [기능 목록](#기능-목록)
- [구현 전략](#구현-전략)
- [Git 커밋 컨벤션](#git-커밋-컨벤션)
- [AI 도구 활용](#ai-도구-활용)
- [프로젝트 구조](#프로젝트-구조)
- [실행 방법](#실행-방법)
- [API 엔드포인트](#api-엔드포인트)

## 기능 목록

### 1. 기본 날씨 정보 조회 기능
- **기능**: Open-Meteo API를 활용한 날씨 정보 조회
- **구현 파일**: `WeatherService.java`, `WeatherController.java`
- **엔드포인트**: `GET /weather?city={cityName}`

### 2. LLM 기반 날씨 요약 및 의상 추천 기능
- **기능**: Google Gemini AI를 활용한 날씨 요약 및 의상 추천
- **구현 파일**: `WeatherLLMService.java`, `LLMService.java`, `LLMServiceImpl.java`
- **엔드포인트**: `GET /weather/llm?location={location}&useCache={true|false}`

### 3. LLM 호출 분리 (2단계 프로세스)
- **기능 3-1**: 날씨 정보 조회 (첫 번째 LLM 호출)
  - `getWeather` 함수를 사용하여 날씨 데이터 조회
  - 캐시 키: `weatherInfo`
- **기능 3-2**: 요약 및 추천 생성 (두 번째 LLM 호출)
  - 조회된 날씨 정보를 기반으로 요약 및 의상 추천 생성
  - 캐시 키: `weatherSummaries`

### 4. 캐싱 기능
- **기능**: Spring Cache를 활용한 LLM 응답 캐싱
- **구현 파일**: `CacheConfig.java`
- **캐시 전략**: 
  - `ConcurrentMapCacheManager` 사용
  - 조건부 캐싱 (`useCache` 파라미터 기반)
  - 두 개의 독립적인 캐시: `weatherInfo`, `weatherSummaries`

### 5. 사용량 추적 및 비용 계산
- **기능**: LLM 호출 시 토큰 사용량 추적 및 예상 비용 계산
- **구현 파일**: `WeatherLLMService.java`
- **추적 정보**: Input Tokens, Output Tokens, Total Tokens, Estimated Cost

### 6. 로깅 기능
- **기능**: 각 LLM 호출 단계별 상세 로깅
- **구현 파일**: `LLMServiceImpl.java`
- **로깅 내용**: 
  - 함수 시작/종료 로그
  - 응답 내용 로그
  - 토큰 사용량 로그

## 구현 전략

### 아키텍처 설계

#### 1. 계층 구조
```
Controller Layer (WeatherController)
    ↓
Service Layer (WeatherLLMService)
    ↓
LLM Service Interface (LLMService)
    ↓
LLM Service Implementation (LLMServiceImpl)
    ↓
Spring AI ChatClient
```

#### 2. 관심사 분리 (Separation of Concerns)
- **LLMService 인터페이스**: LLM 호출 계약 정의
- **LLMServiceImpl**: 실제 LLM 호출 로직 구현 및 캐싱
- **WeatherLLMService**: 비즈니스 로직 (파싱, 사용량 집계, DTO 변환)

#### 3. 캐싱 전략
- **2단계 캐싱**: 
  1. 날씨 정보 조회 결과 캐싱 (`weatherInfo`)
  2. 요약 생성 결과 캐싱 (`weatherSummaries`)
- **조건부 캐싱**: `@Cacheable`의 `condition` 속성을 활용하여 `useCache` 파라미터로 제어
- **캐시 키 전략**:
  - `weatherInfo`: `#location`
  - `weatherSummaries`: `#location + '_' + #weatherInfo.hashCode()`

#### 4. LLM 호출 분리 전략
- **1단계**: 날씨 정보 조회 (`fetchWeatherInfo`)
  - `getWeather` 함수 호출
  - 원시 날씨 데이터 반환
- **2단계**: 요약 생성 (`generateSummary`)
  - 1단계 결과를 입력으로 받아 요약 및 추천 생성
  - 각 단계별 독립적인 캐싱

### 기술 스택
- **Spring Boot 3.5.8**
- **Spring AI 1.1.2** (Google Gemini AI)
- **Java 21**
- **Spring Cache** (ConcurrentMapCacheManager)

## Git 커밋 컨벤션

본 프로젝트는 [AngularJS Git Commit Message Conventions](https://github.com/angular/angular.js/blob/master/DEVELOPERS.md#-git-commit-message-convention)을 따릅니다.

### 커밋 메시지 형식

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 종류
- `feat`: 새로운 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없음)
- `refactor`: 코드 리팩토링
- `test`: 테스트 코드 추가/수정
- `chore`: 빌드 업무 수정, 패키지 매니저 설정 등

### 커밋 예시

#### 기능 1: 기본 날씨 정보 조회
```
feat(weather): add basic weather information retrieval

- Implement WeatherService to fetch weather data from Open-Meteo API
- Add WeatherController with GET /weather endpoint
- Support city validation using City enum
```

#### 기능 2: LLM 기반 날씨 요약
```
feat(llm): add LLM-based weather summary and recommendation

- Integrate Google Gemini AI using Spring AI
- Implement WeatherLLMService for LLM interactions
- Add GET /weather/llm endpoint with location parameter
```

#### 기능 3: LLM 호출 분리
```
refactor(llm): split LLM call into two separate functions

- Create LLMService interface with fetchWeatherInfo and generateSummary methods
- Separate weather data fetching from summary generation
- Implement LLMServiceImpl with two-step LLM call process
```

#### 기능 4: 캐싱 기능
```
feat(cache): implement caching for LLM responses

- Add CacheConfig with ConcurrentMapCacheManager
- Implement conditional caching using @Cacheable condition
- Configure two separate caches: weatherInfo and weatherSummaries
```

#### 기능 5: 사용량 추적
```
feat(llm): add token usage tracking and cost estimation

- Extract usage information from ChatResponse metadata
- Calculate estimated cost based on Gemini 2.5 Flash Lite pricing
- Combine usage from both LLM calls
```

#### 기능 6: 로깅 기능
```
feat(logging): add detailed logging for LLM calls

- Add logging to fetchWeatherInfo method
- Add logging to generateSummary method
- Log response content and token usage for each function
```

## AI 도구 활용

### 활용한 AI 도구
- **Cursor AI (Composer)**: 코드 작성 및 리팩토링 지원

### 활용 방식

#### 1. LLM 서비스 인터페이스 및 구현체 생성
**요청**: LLM 호출 부분을 분리하여 인터페이스를 생성하고, 해당 부분만 캐싱 적용

**생성된 코드**:
- `LLMService.java`: 인터페이스 정의
- `LLMServiceImpl.java`: 구현체 및 캐싱 로직

**학습 내용**:
- Spring의 `@Cacheable` 어노테이션은 프록시 기반으로 동작하므로 인터페이스를 통한 호출이 필요함
- `condition` 속성을 활용하여 동적 캐싱 제어 가능
- 캐시 키에 SpEL 표현식 사용 가능 (`#location`, `#useCache` 등)

#### 2. LLM 호출 2단계 분리
**요청**: LLM 호출 함수를 정보 조회와 요약 생성 두 부분으로 분리

**수정된 코드**:
- `LLMService.java`: `fetchWeatherInfo()`, `generateSummary()` 메서드 추가
- `LLMServiceImpl.java`: 두 메서드 구현
- `WeatherLLMService.java`: 순차 호출 로직으로 변경

**학습 내용**:
- 각 단계별 독립적인 캐싱 가능
- 첫 번째 호출 결과를 두 번째 호출의 입력으로 전달하는 패턴
- 두 응답의 메타데이터를 합산하여 전체 사용량 추적

#### 3. 캐시 설정
**요청**: CacheConfig 생성 및 캐시 매니저 설정

**생성된 코드**:
- `CacheConfig.java`: `ConcurrentMapCacheManager` 설정

**학습 내용**:
- `@EnableCaching` 어노테이션은 Application 클래스에 필요
- `CacheManager` 빈을 통해 여러 캐시를 한 번에 등록 가능
- `ConcurrentMapCacheManager`는 인메모리 캐시로 간단한 사용 사례에 적합

#### 4. 로깅 추가
**요청**: 첫 번째 함수에 로그 추가하여 각 함수의 응답을 명확히 확인

**수정된 코드**:
- `LLMServiceImpl.java`: 각 메서드에 상세 로깅 추가

**학습 내용**:
- SLF4J Logger를 사용한 구조화된 로깅
- 함수별 프리픽스(`[fetchWeatherInfo]`, `[generateSummary]`)로 로그 구분
- 메타데이터에서 사용량 정보 추출 방법

#### 5. Spring AI Usage API 수정
**문제**: `getGenerationTokens()` 메서드가 정의되지 않음 오류

**수정 내용**:
- `usage.getCompletionTokens()` 사용 (Spring AI 1.1.2의 올바른 메서드명)

**학습 내용**:
- Spring AI 1.1.2에서 `Usage` 인터페이스의 메서드명:
  - `getPromptTokens()`: 입력 토큰
  - `getCompletionTokens()`: 출력 토큰 (not `getGenerationTokens()`)
  - `getTotalTokens()`: 전체 토큰

### 코드 수정 내역

1. **LLMService 인터페이스 생성**
   - 기존: 단일 `callLLM()` 메서드
   - 변경: `fetchWeatherInfo()`, `generateSummary()` 두 메서드로 분리

2. **캐싱 전략 변경**
   - 기존: 전체 응답 캐싱
   - 변경: 각 LLM 호출 단계별 독립 캐싱

3. **로깅 강화**
   - 기존: 최소한의 로깅
   - 변경: 각 함수별 상세 로깅 (시작, 응답 내용, 사용량)

4. **캐시 설정 추가**
   - 기존: 기본 캐시 설정
   - 변경: 명시적인 `CacheConfig` 클래스 생성

## 프로젝트 구조

```
src/main/java/sunshine/
├── Application.java                    # Spring Boot 메인 클래스
├── config/
│   ├── AppConfig.java                  # RestTemplate 빈 설정
│   ├── CacheConfig.java               # 캐시 매니저 설정
│   └── WeatherFunctions.java          # LLM 함수 정의 (getWeather)
├── controller/
│   └── WeatherController.java         # REST API 컨트롤러
├── dto/
│   ├── CurrentWeather.java
│   ├── ErrorResponse.java
│   ├── UsageInfo.java                 # LLM 사용량 정보
│   ├── WeatherResponse.java
│   ├── WeatherSummary.java
│   └── WeatherWithRecommendation.java # LLM 응답 DTO
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCityException.java
│   └── WeatherApiException.java
├── model/
│   └── City.java                      # 도시 enum
├── service/
│   ├── LLMService.java                # LLM 서비스 인터페이스
│   ├── LLMServiceImpl.java           # LLM 서비스 구현체 (캐싱 포함)
│   ├── WeatherLLMService.java         # 날씨 LLM 서비스 (비즈니스 로직)
│   └── WeatherService.java            # 기본 날씨 서비스
└── util/
    └── WeatherCodeConverter.java
```

## 실행 방법

### 1. 환경 설정
`src/main/resources/application.properties`에 Google Gemini API 키 설정:
```properties
spring.ai.google.genai.api-key=YOUR_API_KEY
spring.ai.google.genai.chat.options.model=gemini-2.5-flash-lite
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. API 테스트
```bash
# 기본 날씨 조회
curl "http://localhost:8080/weather?city=Seoul"

# LLM 기반 날씨 요약 (캐시 사용)
curl "http://localhost:8080/weather/llm?location=Seoul&useCache=true"

# LLM 기반 날씨 요약 (캐시 미사용)
curl "http://localhost:8080/weather/llm?location=Seoul&useCache=false"
```

## API 엔드포인트

### GET /weather
기본 날씨 정보 조회

**파라미터**:
- `city` (required): 도시명 (Seoul, Tokyo, New York, Paris, London)

**응답 예시**:
```json
{
  "summary": "Current weather information..."
}
```

### GET /weather/llm
LLM 기반 날씨 요약 및 의상 추천

**파라미터**:
- `location` (required): 위치명
- `useCache` (optional, default: false): 캐시 사용 여부

**응답 예시**:
```json
{
  "weatherSummary": "The weather in Seoul is...",
  "clothingRecommendation": "You should wear..."
}
```

## 참고 사항

- 캐싱은 `useCache=true`일 때만 동작합니다
- 두 단계 LLM 호출 모두 독립적으로 캐싱됩니다
- 로그에서 각 함수의 응답과 사용량을 확인할 수 있습니다
- 토큰 사용량은 두 LLM 호출의 합계로 계산됩니다
