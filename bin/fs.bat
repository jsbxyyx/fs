@echo off
chcp 65001
setlocal enabledelayedexpansion

rem =========================================================================
rem FS 文件传输系统 - Windows 启动脚本
rem 用法: fs.bat [options] [directory]
rem       set JAVA_OPTS=-Dserver.port=9090
rem       fs.bat --download false --upload false D:\files
rem =========================================================================

set "FS_HOME=%~dp0"
if "%FS_HOME:~-1%"=="\" set "FS_HOME=%FS_HOME:~0,-1%"
set "JAR_FILE=%FS_HOME%\fs.jar"

rem -------------------------------------------------------------------------
rem 检查 JAR 文件
rem -------------------------------------------------------------------------
if not exist "%JAR_FILE%" (
    echo [错误] 找不到 %JAR_FILE%
    pause
    exit /b 1
)

rem -------------------------------------------------------------------------
rem 查找 Java 环境
rem -------------------------------------------------------------------------
set "JAVA_CMD="

rem 1.  JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        goto java_found
    )
)

rem 2. PATH
where java.exe >nul 2>&1
if %errorlevel% equ 0 (
    for /f "delims=" %%i in ('where java.exe') do (
        set "JAVA_CMD=%%i"
        goto java_found
    )
)

rem 3. 常见路径
for %%d in (
    "C:\Program Files\Java"
    "C:\Program Files (x86)\Java"
    "C:\Program Files\Eclipse Adoptium"
    "C:\Program Files\Temurin"
    "C:\Program Files\Microsoft\jdk"
    "%ProgramFiles%\Java"
) do (
    if exist %%d (
        for /f "delims=" %%j in ('dir /b /ad %%d 2^>nul ^| sort /r') do (
            if exist "%%~d\%%j\bin\java.exe" (
                set "JAVA_CMD=%%~d\%%j\bin\java.exe"
                goto java_found
            )
        )
    )
)

rem -------------------------------------------------------------------------
rem 未找到 Java
rem -------------------------------------------------------------------------
echo ========================================
echo   未检测到 Java 运行环境
echo ========================================
echo. 
echo FS 需要 Java 8 或更高版本。
echo.
echo 下载地址: 
echo   1. Zulu OpenJDK (推荐): https://www.azul.com/downloads/?package=jdk#zulu
echo.
echo ----------------------------------------
echo 安装后的配置步骤:
echo ----------------------------------------
echo.
echo 如果安装后仍提示找不到 Java，请手动设置 JAVA_HOME:
echo.
echo 1. 右键"此电脑" - "属性" - "高级系统设置"
echo 2. 点击"环境变量"
echo 3. 在"系统变量"中点击"新建":
echo    变量名:    JAVA_HOME
echo    变量值:    C:\Program Files\Java\jdk-17
echo           (替换为你的实际安装路径)
echo 4. 编辑"Path"变量，添加:  %%JAVA_HOME%%\bin
echo 5. 重新打开命令行窗口
echo.
echo 或者使用命令行设置 (管理员权限):
echo    setx JAVA_HOME "C:\Program Files\Java\jdk17" /M
echo    setx PATH "%%PATH%%;%%JAVA_HOME%%\bin" /M
echo. 
echo 验证安装:  java -version
echo.
echo 如果显示版本信息，则安装成功。
echo.
pause
exit /b 1

rem -------------------------------------------------------------------------
rem 启动应用
rem -------------------------------------------------------------------------
: java_found

rem 直接启动，使用 JAVA_OPTS 和透传所有参数
"%JAVA_CMD%" %JAVA_OPTS% -Dspring.servlet.multipart.max-file-size=10GB -Dspring.servlet.multipart.max-request-size=10GB -jar "%JAR_FILE%" %*

exit /b %errorlevel%
