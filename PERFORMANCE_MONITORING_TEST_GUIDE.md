# 성능 모니터링 테스트 가이드

## 🚀 빠른 테스트 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. API 호출 테스트
```bash
curl -X POST http://localhost:8080/api/schedules/calendar \
  -H "Content-Type: application/json" \
  -d '{"year": 2025, "month": 7}'
```

### 3. 성능 모니터링 확인

#### A. 콘솔 로그 확인
애플리케이션 콘솔에서 다음 로그들을 확인할 수 있습니다:

**P6Spy SQL 로그:**
```
2024-01-15 14:30:25 | 150ms | statement | 
SELECT s1_0.id, s1_0.content, s1_0.created_at, s1_0.deep_study_required, s1_0.meeting_url, s1_0.name, s1_0.schedule_date, s1_0.study_id, s1_0.updated_at FROM schedule s1_0 WHERE s1_0.schedule_date BETWEEN ? AND ? ORDER BY s1_0.schedule_date
```

**AOP 실행시간 측정 로그:**
```
2024-01-15 14:30:25.123 INFO  c.e.s.a.ExecutionTimeLogger - 🎯 [실행시간 측정] ScheduleCalendarController.getMonthlySchedules() - 158ms
2024-01-15 14:30:25.124 INFO  c.e.s.a.ExecutionTimeLogger - 🎯 [실행시간 측정] ScheduleCalendarService.getMonthlySchedules() - 155ms
```

**애플리케이션 로그:**
```
2024-01-15 14:30:25.123 INFO  c.e.s.s.ScheduleCalendarService - === getMonthlySchedules 시작 ===
2024-01-15 14:30:25.124 INFO  c.e.s.s.ScheduleCalendarService - 요청 데이터: year=2025, month=7
2024-01-15 14:30:25.125 INFO  c.e.s.s.ScheduleCalendarService - 조회 기간: 2025-07-01T00:00 ~ 2025-07-31T23:59:59.999999999
2024-01-15 14:30:25.275 INFO  c.e.s.s.ScheduleCalendarService - 데이터베이스 조회 완료: 15개 일정, 소요시간: 150ms
2024-01-15 14:30:25.280 INFO  c.e.s.s.ScheduleCalendarService - === getMonthlySchedules 완료: 15개 일정 반환 ===
```

#### B. Spring Boot Actuator 확인
브라우저에서 다음 URL들을 확인:

**HTTP 요청 기록:**
```
http://localhost:8080/actuator/httpexchanges
```

응답 예시:
```json
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

**헬스 체크:**
```
http://localhost:8080/actuator/health
```

**메트릭스:**
```
http://localhost:8080/actuator/metrics
```

**애플리케이션 정보:**
```
http://localhost:8080/actuator/info
```

## 📊 성능 분석

### 로그 분석 포인트
1. **SQL 실행 시간**: P6Spy 로그에서 `150ms` 확인
2. **서비스 메서드 실행 시간**: AOP 로그에서 `155ms` 확인
3. **컨트롤러 전체 실행 시간**: AOP 로그에서 `158ms` 확인
4. **HTTP 요청 전체 시간**: Actuator에서 `PT0.158S` (158ms) 확인

### 성능 기준 체크
- ✅ SQL 실행 시간: 150ms (100ms 이하 기준 초과 - 개선 필요)
- ✅ 서비스 메서드: 155ms (150ms 이하 기준 초과 - 개선 필요)
- ✅ 컨트롤러 전체: 158ms (200ms 이하 기준 만족)
- ✅ HTTP 요청 전체: 158ms (250ms 이하 기준 만족)

## 🔧 JMeter 부하테스트 연동

### 1. JMeter 테스트 실행
```bash
run-jmeter-load-test.bat
```

### 2. 실시간 모니터링
테스트 실행 중 다음을 동시에 확인:

1. **JMeter 결과**: HTML 보고서에서 응답 시간 통계
2. **콘솔 로그**: SQL 및 메서드 실행 시간
3. **Actuator**: http://localhost:8080/actuator/httpexchanges

### 3. 성능 병목 분석
- **SQL이 느린 경우**: P6Spy 로그에서 쿼리 최적화 필요
- **서비스 로직이 느린 경우**: AOP 로그에서 비즈니스 로직 개선 필요
- **전체 요청이 느린 경우**: Actuator에서 전체적인 성능 개선 필요

## 🎯 추가 설정 옵션

### P6Spy 설정 조정
```properties
# spy.properties에서 수정
executionThreshold=50  # 50ms 이상만 로깅
logLevel=warn          # 로그 레벨 조정
```

### Actuator 설정 조정
```properties
# application.properties에서 수정
management.httpexchanges.recording.enabled=false  # HTTP 기록 비활성화
management.endpoints.web.exposure.include=health  # 헬스만 노출
```

### AOP 설정 조정
```java
// ExecutionTimeLogger.java에서 수정
@Around("@annotation(logExecutionTime)")
public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
    // 실행 시간 임계값 설정
    long threshold = 100; // 100ms 이상만 로깅
    
    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long executionTime = System.currentTimeMillis() - startTime;
    
    if (executionTime > threshold) {
        log.warn("⚠️ [느린 실행] {} - {}ms", 
                joinPoint.getSignature().toShortString(), 
                executionTime);
    }
    
    return result;
}
```



