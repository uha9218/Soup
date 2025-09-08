@echo off
echo 테스트 실행 중... 로그는 test-output.txt 파일에 저장됩니다.
echo.

REM 특정 테스트 클래스만 실행하고 로그를 파일로 저장
gradlew.bat test --tests "com.example.soup.study.StudyProgressServicePerformanceTest" > test-output.txt 2>&1

echo.
echo 테스트 완료! 로그 파일: test-output.txt
echo 파일을 열어서 결과를 확인하세요.
pause
