REM set JAVA_HOME=D:\.jdks\jdk8
IF "%JAVA_HOME%" == "" (
    echo undefine JAVA_HOME
    goto end
)
set pwd=%~dp0
%JAVA_HOME%\bin\java -Dspring.servlet.multipart.max-file-size=10GB -Dspring.servlet.multipart.max-request-size=10GB -jar %pwd%fs.jar %*
:end
