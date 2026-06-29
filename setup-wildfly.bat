@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
set "PATH=%JAVA_HOME%\bin;%SystemRoot%\system32;%SystemRoot%;%PATH%"

set "WF_HOME=C:\wildfly-39.0.0.Final"
set "PROJECT_ROOT=%~dp0"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
set "JAR_PATH=%BACKEND_DIR%\target\parking-backend\WEB-INF\lib\postgresql-42.7.2.jar"
set "MODULE_PATH=%WF_HOME%\modules\org\postgresql\main"

echo [1/4] Checking dependencies...
if exist "%JAR_PATH%" (
    echo PostgreSQL URL found. Skipping build.
) else (
    echo Building backend to fetch dependencies...
    cd /d "%BACKEND_DIR%"
    call mvn clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Maven build failed.
        pause
        exit /b %ERRORLEVEL%
    )
)

echo [2/4] Installing PostgreSQL Module...
if not exist "%MODULE_PATH%" mkdir "%MODULE_PATH%"
copy /Y "%JAR_PATH%" "%MODULE_PATH%\"

(
echo ^<?xml version="1.0" ?^>
echo ^<module xmlns="urn:jboss:module:1.1" name="org.postgresql"^>
echo     ^<resources^>
echo         ^<resource-root path="postgresql-42.7.2.jar"/^>
echo     ^</resources^>
echo     ^<dependencies^>
echo         ^<module name="jakarta.api"/^>
echo         ^<module name="jakarta.transaction.api"/^>
echo     ^</dependencies^>
echo ^</module^>
) > "%MODULE_PATH%\module.xml"

echo [3/4] Configuring Datasource...
set "CLI_SCRIPT=setup-ds.cli"
echo embed-server --std-out=echo > "%CLI_SCRIPT%"
echo /subsystem=datasources/jdbc-driver=postgresql:add(driver-name="postgresql",driver-module-name="org.postgresql",driver-class-name=org.postgresql.Driver) >> "%CLI_SCRIPT%"
echo data-source add --name=ParkingDS --jndi-name=java:jboss/datasources/ParkingDS --driver-name=postgresql --connection-url=jdbc:postgresql://localhost:5434/parking_db --user-name=postgres --password=1234 >> "%CLI_SCRIPT%"
echo stop-embedded-server >> "%CLI_SCRIPT%"

call "%WF_HOME%\bin\jboss-cli.bat" --file="%CLI_SCRIPT%"
del "%CLI_SCRIPT%"



echo [4/4] Setup Complete! Only run this once.
echo NOTE: Ensure the database "parking_db" exists in PostgreSQL.
echo You can now run "run-system.bat" to start the system.
pause
