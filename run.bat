@echo off
title Multi Banking Transaction System
color 0b

echo =====================================================================
echo          🏦 MULTI BANKING TRANSACTION SYSTEM
echo =====================================================================
echo.
echo  [1] Start Local Web Server (Open in Browser http://localhost:8080)
echo  [2] Run Console CLI Application (Main.java)
echo  [3] Recompile All Java Classes & Servlets
echo  [4] Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto start_web
if "%choice%"=="2" goto start_cli
if "%choice%"=="3" goto compile_all
if "%choice%"=="4" exit

:start_web
echo.
echo [*] Starting Local Web Application Server...
java -cp "WebContent/WEB-INF/classes;WebContent/WEB-INF/lib/*" runner.LocalServer
pause
exit

:start_cli
echo.
echo [*] Launching Console CLI Application...
java -cp ".;WebContent/WEB-INF/classes;WebContent/WEB-INF/lib/*" Main
pause
exit

:compile_all
echo.
echo [*] Compiling Java classes, Servlets, and DAOs...
if not exist "WebContent\WEB-INF\classes" mkdir "WebContent\WEB-INF\classes"
javac -cp "WebContent/WEB-INF/lib/*" -d "WebContent/WEB-INF/classes" src/com/banking/model/*.java src/com/banking/dao/*.java src/com/banking/servlet/*.java runner/LocalServer.java
copy /Y src\db.properties WebContent\WEB-INF\classes\ >nul
javac -cp "WebContent/WEB-INF/classes;WebContent/WEB-INF/lib/*" Main.java
echo [*] Compilation completed successfully!
pause
exit
