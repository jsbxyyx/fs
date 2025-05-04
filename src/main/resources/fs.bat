set JAVA_HOME=D:\.jdks\jdk8
set pwd=%~dp0
%JAVA_HOME%\bin\java -Dspring.servlet.multipart.max-file-size=10GB -Dspring.servlet.multipart.max-request-size=10GB -jar %pwd%\fs.jar %1