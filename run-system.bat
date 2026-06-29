@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%SystemRoot%\system32;%SystemRoot%;%PATH%"
set "WF_HOME=C:\wildfly-39.0.0.Final"
SET "PROJECT_ROOT=%~dp0"
:: Remove trailing backslash if present
if "%PROJECT_ROOT:~-1%"=="\" set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"

echo [1/3] Building Backend...
cd /d "%PROJECT_ROOT%\backend"
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Deploying to WildFly...
copy /Y "%PROJECT_ROOT%\backend\target\parking-backend.war" "%WF_HOME%\standalone\deployments\"

echo [3/3] Starting WildFly Server...
cd /d "%WF_HOME%\bin"
standalone.bat
