# JMeter 부하테스트 가이드

## 📋 개요
이 문서는 `/api/schedules/calendar` API에 대한 JMeter 부하테스트 설정과 실행 방법을 설명합니다.

## 🛠️ 사전 준비

### 1. JMeter 설치
1. [Apache JMeter 공식 사이트](https://jmeter.apache.org/download_jmeter.cgi)에서 다운로드
2. 압축 해제 후 원하는 경로에 설치 (예: `C:\apache-jmeter-5.6.3`)
3. `run-jmeter-load-test.bat` 파일에서 `JMETER_HOME` 경로 수정

### 2. Spring Boot 애플리케이션 실행
```bash
# 애플리케이션이 8080 포트에서 실행 중인지 확인
./gradlew bootRun
```

## 📊 테스트 구성

### 테스트 플랜 구조
```
스케줄 캘린더 부하테스트
├── 스케줄 캘린더 부하테스트 그룹 (일반 부하테스트)
│   ├── 50개 스레드
│   ├── 30초 램프업 시간
│   ├── 10회 반복
│   └── 총 500회 요청
└── 스케줄 캘린더 스트레스 테스트 (스트레스 테스트)
    ├── 100개 스레드
    ├── 60초 램프업 시간
    ├── 5회 반복
    └── 총 500회 요청
```

### 테스트 시나리오

#### 1. 일반 부하테스트
- **목적**: 일반적인 사용 패턴 시뮬레이션
- **설정**:
  - 스레드 수: 50개
  - 램프업 시간: 30초
  - 반복 횟수: 10회
  - 총 요청 수: 500회
- **요청 데이터**: 랜덤 년도(2024-2025), 랜덤 월(1-12)

#### 2. 스트레스 테스트
- **목적**: 시스템 한계 테스트
- **설정**:
  - 스레드 수: 100개
  - 램프업 시간: 60초
  - 반복 횟수: 5회
  - 총 요청 수: 500회
- **요청 데이터**: 고정 값 (2025년 7월)

## 🚀 테스트 실행

### 방법 1: 배치 파일 사용 (권장)
```bash
# 배치 파일 실행
run-jmeter-load-test.bat
```

### 방법 2: 명령줄 직접 실행
```bash
# JMeter 설치 경로로 이동
cd C:\apache-jmeter-5.6.3\bin

# 테스트 실행
jmeter.bat -n -t jmeter-schedule-calendar-load-test.jmx ^
    -l jmeter-results\schedule-calendar-load-test_%TIMESTAMP%.jtl ^
    -e -o jmeter-results\schedule-calendar-load-test_%TIMESTAMP%_report\
```

## 📈 결과 분석

### 생성되는 파일들
- **JTL 파일**: 원시 테스트 데이터
- **HTML 보고서**: 시각화된 테스트 결과

### 주요 지표
1. **응답 시간 (Response Time)**
   - 평균 응답 시간
   - 90th, 95th, 99th 퍼센타일
   - 최대/최소 응답 시간

2. **처리량 (Throughput)**
   - 초당 처리 요청 수 (RPS)
   - 총 처리 요청 수

3. **오류율 (Error Rate)**
   - 성공/실패 요청 비율
   - 오류 유형별 분류

4. **리소스 사용량**
   - CPU 사용률
   - 메모리 사용량
   - 네트워크 대역폭

### 성능 기준
- **응답 시간**: 5초 이하 (Duration Assertion)
- **성공률**: 95% 이상
- **처리량**: 초당 10 요청 이상

## 🔧 테스트 커스터마이징

### 스레드 수 조정
```xml
<!-- jmeter-schedule-calendar-load-test.jmx 파일에서 수정 -->
<stringProp name="ThreadGroup.num_threads">100</stringProp>
```

### 램프업 시간 조정
```xml
<!-- 부하를 점진적으로 증가시키는 시간 -->
<stringProp name="ThreadGroup.ramp_time">60</stringProp>
```

### 반복 횟수 조정
```xml
<!-- 각 스레드가 실행할 횟수 -->
<stringProp name="LoopController.loops">10</stringProp>
```

### 요청 데이터 수정
```xml
<!-- 년도 범위 변경 -->
<stringProp name="Argument.value">${__Random(2023,2026)}</stringProp>

<!-- 월 범위 변경 -->
<stringProp name="Argument.value">${__Random(1,12)}</stringProp>
```

## 🐛 문제 해결

### 일반적인 문제들

1. **JMeter 실행 오류**
   - Java 8 이상 설치 확인
   - JMETER_HOME 경로 확인
   - 환경 변수 설정 확인

2. **연결 오류**
   - Spring Boot 애플리케이션 실행 확인
   - 포트 8080 사용 가능 확인
   - 방화벽 설정 확인

3. **메모리 부족**
   - JMeter 힙 메모리 증가
   - `jmeter.bat`에서 `-Xmx2g` 옵션 추가

### JMeter 힙 메모리 증가
```bash
# jmeter.bat 파일 수정
set HEAP=-Xms1g -Xmx2g -XX:MaxMetaspaceSize=256m
```

## 📝 테스트 결과 예시

### 성공적인 테스트 결과
```
성공률: 98.5%
평균 응답 시간: 245ms
90th 퍼센타일: 890ms
95th 퍼센타일: 1,245ms
처리량: 15.2 RPS
총 요청 수: 1,000
성공 요청 수: 985
실패 요청 수: 15
```

### 개선이 필요한 결과
```
성공률: 85.2%
평균 응답 시간: 2,890ms
90th 퍼센타일: 8,500ms
95th 퍼센타일: 12,000ms
처리량: 5.8 RPS
총 요청 수: 1,000
성공 요청 수: 852
실패 요청 수: 148
```

## 🔄 정기적인 부하테스트

### 권장 테스트 주기
- **개발 단계**: 코드 변경 후
- **스테이징 환경**: 배포 전
- **프로덕션 환경**: 월 1회

### 테스트 결과 추적
- 테스트 결과를 버전 관리 시스템에 저장
- 성능 지표 변화 추적
- 성능 회귀 감지

## 📞 추가 지원

문제가 발생하거나 추가 설정이 필요한 경우:
1. JMeter 공식 문서 참조
2. Spring Boot 애플리케이션 로그 확인
3. 시스템 리소스 모니터링 도구 사용
