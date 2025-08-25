# P6Spy SQL 로깅 + Spring Boot Actuator + AOP 실행시간 측정 설정 가이드

## 📋 개요
이 문서는 `getMonthlySchedules` 메서드의 성능 모니터링을 위한 종합적인 설정 방법을 설명합니다.
- P6Spy: SQL 쿼리 로깅
- Spring Boot Actuator: HTTP 요청 기록 및 성능 모니터링
- AOP: 메서드 실행 시간 측정

## 🛠️ 설정 완료 사항

### 1. 의존성 추가
```gradle
// build.gradle에 추가됨
implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'  // P6Spy
implementation 'org.springframework.boot:spring-boot-starter-actuator'    // Actuator
implementation 'org.springframework.boot:spring-boot-starter-aop'         // AOP
```

### 2. 설정 파일
- **application.properties**: P6Spy 드라이버 및 Actuator 설정
- **spy.properties**: P6Spy 로깅 설정
- **application-test.yml**: 테스트 환경용 설정

### 3. 코드 설정
- **LogExecutionTime.java**: 실행시간 측정 어노테이션
- **ExecutionTimeLogger.java**: AOP Aspect 클래스
- **ScheduleCalendarService**: `@Slf4j` 및 `@LogExecutionTime` 어노테이션 추가
- **ScheduleCalendarController**: `@LogExecutionTime` 어노테이션 추가

## 📊 로깅 출력 예시

### P6Spy SQL 로그
```
2024-01-15 14:30:25 | 150ms | statement | 
SELECT 
    s1_0.id,
    s1_0.content,
    s1_0.created_at,
    s1_0.deep_study_required,
    s1_0.meeting_url,
    s1_0.name,
    s1_0.schedule_date,
    s1_0.study_id,
    s1_0.updated_at 
FROM schedule s1_0 
WHERE s1_0.schedule_date BETWEEN ? AND ? 
ORDER BY s1_0.schedule_date
```

### 애플리케이션 로그
```
2024-01-15 14:30:25.123 INFO  c.e.s.s.ScheduleCalendarService - === getMonthlySchedules 시작 ===
2024-01-15 14:30:25.124 INFO  c.e.s.s.ScheduleCalendarService - 요청 데이터: year=2025, month=7
2024-01-15 14:30:25.125 INFO  c.e.s.s.ScheduleCalendarService - 조회 기간: 2025-07-01T00:00 ~ 2025-07-31T23:59:59.999999999
2024-01-15 14:30:25.275 INFO  c.e.s.s.ScheduleCalendarService - 데이터베이스 조회 완료: 15개 일정, 소요시간: 150ms
2024-01-15 14:30:25.280 INFO  c.e.s.s.ScheduleCalendarService - === getMonthlySchedules 완료: 15개 일정 반환 ===
```

### AOP 실행시간 측정 로그
```
2024-01-15 14:30:25.123 INFO  c.e.s.a.ExecutionTimeLogger - 🎯 [실행시간 측정] ScheduleCalendarController.getMonthlySchedules() - 158ms
2024-01-15 14:30:25.124 INFO  c.e.s.a.ExecutionTimeLogger - 🎯 [실행시간 측정] ScheduleCalendarService.getMonthlySchedules() - 155ms
```

### Spring Boot Actuator HTTP 요청 기록
```
GET http://localhost:8080/actuator/httpexchanges
{
  "exchanges": [
    {
      "timestamp": "2025-01-15T14:30:25.123Z",
      "request": {
        "method": "POST",
        "uri": "http://localhost:8080/api/schedules/calendar"
      },
      "response": {
        "status": 200
      },
      "timeTaken": "PT0.158S"
    }
  ]
}
```

## 🔧 설정 상세 설명

### spy.properties 주요 설정
```properties
# 로그 포맷: 시간 | 실행시간 | 카테고리 | SQL
customLogMessageFormat=%(currentTime) | %(executionTime)ms | %(category) | %(sql)

# 100ms 이상 실행되는 쿼리만 로깅
executionThreshold=100

# 콘솔 출력
appender=com.p6spy.engine.spy.appender.StdoutLogger
```

### application.properties 설정
```properties
# P6Spy 드라이버 사용
spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver

# SQL 포맷팅 활성화
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# 로깅 레벨 설정
logging.level.com.p6spy.engine.spy=INFO
logging.level.com.example.soup.schedule=DEBUG

# Spring Boot Actuator 설정
management.endpoints.web.exposure.include=httpexchanges,health,metrics,info
management.httpexchanges.recording.enabled=true
management.endpoint.httpexchanges.enabled=true
```

## 🚀 사용 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. API 호출
```bash
curl -X POST http://localhost:8080/api/schedules/calendar \
  -H "Content-Type: application/json" \
  -d '{"year": 2025, "month": 7}'
```

### 3. 로그 확인
콘솔에서 다음과 같은 로그를 확인할 수 있습니다:
- P6Spy SQL 로그 (실행 시간, 쿼리 내용)
- 애플리케이션 로그 (메서드 실행 과정)
- AOP 실행시간 측정 로그 (메서드별 실행 시간)

### 4. Actuator 모니터링
브라우저에서 다음 URL로 접속하여 성능 모니터링:
- **HTTP 요청 기록**: http://localhost:8080/actuator/httpexchanges
- **헬스 체크**: http://localhost:8080/actuator/health
- **메트릭스**: http://localhost:8080/actuator/metrics
- **애플리케이션 정보**: http://localhost:8080/actuator/info

## 📈 성능 모니터링

### 주요 지표
1. **SQL 실행 시간**: P6Spy에서 제공하는 실행 시간
2. **메서드 실행 시간**: AOP를 통한 정밀한 측정
3. **HTTP 요청 시간**: Actuator에서 제공하는 전체 요청 시간
4. **조회된 데이터 수**: 반환된 일정 개수

### 성능 기준
- **SQL 실행 시간**: 100ms 이하 (executionThreshold 설정)
- **서비스 메서드 실행 시간**: 150ms 이하
- **컨트롤러 전체 실행 시간**: 200ms 이하
- **HTTP 요청 전체 시간**: 250ms 이하
- **성공률**: 100%

## 🔍 문제 해결

### 일반적인 문제들

1. **P6Spy 로그가 출력되지 않는 경우**
   - `spy.properties` 파일이 `src/main/resources`에 있는지 확인
   - `spring.datasource.driver-class-name` 설정 확인

2. **SQL 로그가 너무 많은 경우**
   - `executionThreshold` 값을 높여서 필터링
   - `logLevel`을 `WARN`으로 변경

3. **로깅 레벨 조정**
   ```properties
   # 더 자세한 로그
   logging.level.com.p6spy.engine.spy=DEBUG
   
   # 간단한 로그
   logging.level.com.p6spy.engine.spy=WARN
   ```

## 🎯 JMeter 부하테스트와 연동

종합적인 성능 모니터링을 통해 JMeter 부하테스트 시 다음을 확인할 수 있습니다:

1. **SQL 쿼리 성능**: P6Spy를 통한 각 요청별 SQL 실행 시간
2. **메서드별 성능**: AOP를 통한 정밀한 실행 시간 측정
3. **HTTP 요청 성능**: Actuator를 통한 전체 요청 시간 모니터링
4. **쿼리 최적화**: 느린 쿼리 식별 및 개선
5. **데이터베이스 병목**: 동시 요청 시 DB 성능 영향

### 부하테스트 실행
```bash
# JMeter 테스트 실행
run-jmeter-load-test.bat

# 성능 모니터링 확인
# 1. 콘솔 로그: SQL 실행 시간, 메서드 실행 시간
# 2. Actuator: http://localhost:8080/actuator/httpexchanges
# 3. 메트릭스: http://localhost:8080/actuator/metrics

# 분석 지표
# - 평균 SQL 실행 시간
# - 최대 SQL 실행 시간
# - 메서드별 실행 시간 분포
# - HTTP 요청 응답 시간 통계
# - 느린 쿼리 패턴 분석
```

## 📝 추가 설정 옵션

### 파일 로깅 활성화
```properties
# spy.properties에서 주석 해제
appender=com.p6spy.engine.spy.appender.FileLogger
logfile=spy.log
```

### 특정 패키지만 로깅
```properties
# spy.properties에 추가
filter=true
include=com.example.soup.schedule
```

### 로그 포맷 커스터마이징
```properties
# spy.properties에서 수정
customLogMessageFormat=%(currentTime) | %(executionTime)ms | %(category) | %(sqlSingleLine)
```
