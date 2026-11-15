@echo off
title Quan Ly Phong Kham Nha Khoa - Sai Gon Dental
echo ============================================================
echo   CHUONG TRINH QUAN LY PHONG KHAM NHA KHOA - SAI GON DENTAL
echo ============================================================
echo [1/2] Dang kiem tra va bien dich ma nguon Java...

cd /d "%~dp0"
if not exist bin mkdir bin

dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\mssql-jdbc.jar;lib\flatlaf.jar;lib\*" -d bin @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Bien dich that bai! Vui long kiem tra JDK hoac thu vien.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Bien dich thanh cong! Dang khoi chay chuong trinh...
start "" javaw -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -Dswing.aatext=true -Dawt.useSystemAAFontSettings=on -cp "bin;lib\mssql-jdbc.jar;lib\flatlaf.jar;lib\*" "-Djava.library.path=lib" com.nhakhoa.App
echo Da mo ung dung thanh cong!
exit /b 0
