@echo off
echo ========================================
echo Schedule Calendar Tests Runner
echo ========================================

echo.
echo 1. Quick Tests (Fast)
echo 2. Functional Tests (Medium)
echo 3. Load Tests (Slow)
echo 4. All Tests
echo.

set /p choice="Select test type (1-4): "

if "%choice%"=="1" (
    echo Running Quick Tests...
    gradlew.bat test --tests "ScheduleCalendarQuickTest" --info
) else if "%choice%"=="2" (
    echo Running Functional Tests...
    gradlew.bat test --tests "ScheduleCalendarFunctionalTest" --info
) else if "%choice%"=="3" (
    echo Running Load Tests...
    gradlew.bat test --tests "ScheduleCalendarLoadTest" --info
) else if "%choice%"=="4" (
    echo Running All Tests...
    gradlew.bat test --tests "*ScheduleCalendar*" --info
) else (
    echo Invalid choice. Please run the script again.
)

echo.
echo Test execution completed.
pause
