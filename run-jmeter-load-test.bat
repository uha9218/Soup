@echo off
echo ========================================
echo 스케줄 캘린더 API 부하테스트 실행
echo ========================================

REM JMeter 설치 경로 확인 (사용자가 설치한 경로로 수정 필요)
set JMETER_HOME=C:\apache-jmeter-5.6.3
set JMETER_BIN=%JMETER_HOME%\bin

REM JMeter가 설치되어 있는지 확인
if not exist "%JMETER_BIN%\jmeter.bat" (
    echo JMeter가 설치되어 있지 않습니다.
    echo https://jmeter.apache.org/download_jmeter.cgi 에서 다운로드 후 설치해주세요.
    echo 설치 후 이 배치 파일의 JMETER_HOME 경로를 수정해주세요.
    pause
    exit /b 1
)

REM 테스트 결과 저장 디렉토리 생성
if not exist "jmeter-results" mkdir jmeter-results

REM 현재 시간을 파일명에 포함
set TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%

echo 테스트 시작 시간: %date% %time%
echo 결과 파일: jmeter-results\schedule-calendar-load-test_%TIMESTAMP%.jtl
echo HTML 보고서: jmeter-results\schedule-calendar-load-test_%TIMESTAMP%_report\

REM JMeter 테스트 실행
echo.
echo JMeter 부하테스트를 시작합니다...
"%JMETER_BIN%\jmeter.bat" -n -t jmeter-schedule-calendar-load-test.jmx ^
    -l jmeter-results\schedule-calendar-load-test_%TIMESTAMP%.jtl ^
    -e -o jmeter-results\schedule-calendar-load-test_%TIMESTAMP%_report\

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 부하테스트가 성공적으로 완료되었습니다!
    echo ========================================
    echo 결과 파일: jmeter-results\schedule-calendar-load-test_%TIMESTAMP%.jtl
    echo HTML 보고서: jmeter-results\schedule-calendar-load-test_%TIMESTAMP%_report\index.html
    echo.
    echo HTML 보고서를 브라우저에서 열어 결과를 확인하세요.
    start jmeter-results\schedule-calendar-load-test_%TIMESTAMP%_report\index.html
) else (
    echo.
    echo ========================================
    echo 부하테스트 실행 중 오류가 발생했습니다.
    echo ========================================
    echo 오류 코드: %ERRORLEVEL%
)

pause



