#!/bin/bash

# =========================================================================
# FS 文件传输系统 - Unix/Linux/macOS 启动脚本
# 用法:  ./fs.sh [options] [directory]
#       export JAVA_OPTS="-Dserver.port=9090"
#       ./fs.sh --download false --upload false /data/files
# =========================================================================

FS_HOME="$(cd "$(dirname "$0")" && pwd)"
JAR_FILE="$FS_HOME/fs.jar"

# -------------------------------------------------------------------------
# 检查 JAR 文件
# -------------------------------------------------------------------------
if [ ! -f "$JAR_FILE" ]; then
    echo "[错误] 找不到 $JAR_FILE"
    exit 1
fi

# -------------------------------------------------------------------------
# 查找 Java 环境
# -------------------------------------------------------------------------
JAVA_CMD=""

# 1. JAVA_HOME
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA_CMD="$JAVA_HOME/bin/java"
fi

# 2. PATH
if [ -z "$JAVA_CMD" ] && command -v java >/dev/null 2>&1; then
    JAVA_CMD="java"
fi

# 3. macOS - /usr/libexec/java_home
if [ -z "$JAVA_CMD" ] && [ "$(uname)" = "Darwin" ]; then
    if [ -x "/usr/libexec/java_home" ]; then
        JAVA_HOME_DETECTED="$(/usr/libexec/java_home 2>/dev/null)"
        if [ -n "$JAVA_HOME_DETECTED" ] && [ -x "$JAVA_HOME_DETECTED/bin/java" ]; then
            JAVA_CMD="$JAVA_HOME_DETECTED/bin/java"
        fi
    fi
fi

# 4. Linux 常见路径
if [ -z "$JAVA_CMD" ] && [ "$(uname)" = "Linux" ]; then
    for java_path in \
        /usr/lib/jvm/java-*/bin/java \
        /usr/lib/jvm/default-java/bin/java \
        /opt/java/*/bin/java
    do
        if [ -x "$java_path" ]; then
            JAVA_CMD="$java_path"
            break
        fi
    done
fi

# -------------------------------------------------------------------------
# 未找到 Java
# -------------------------------------------------------------------------
if [ -z "$JAVA_CMD" ]; then
    cat << 'EOF'

========================================
  未检测到 Java 运行环境
========================================

FS 需要 Java 8 或更高版本。

EOF

    if [ "$(uname)" = "Darwin" ]; then
        cat << 'EOF'
macOS 安装方式:
  1.  Homebrew:  brew install openjdk@17
  2. 下载安装: https://adoptium.net/

配置环境变量:
  echo 'export JAVA_HOME=$(/usr/libexec/java_home)' >> ~/.zshrc
  source ~/.zshrc
EOF
    elif [ -f /etc/debian_version ]; then
        cat << 'EOF'
Ubuntu/Debian 安装:
  sudo apt update
  sudo apt install openjdk-17-jdk -y

配置环境变量:
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
EOF
    elif [ -f /etc/redhat-release ]; then
        cat << 'EOF'
CentOS/RHEL 安装:
  sudo yum install java-17-openjdk-devel -y

配置环境变量:
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
  echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >> ~/.bash_profile
EOF
    else
        cat << 'EOF'
下载地址:
  - Eclipse Temurin:  https://adoptium.net/

配置环境变量:
  export JAVA_HOME=/path/to/jdk
  echo 'export JAVA_HOME=/path/to/jdk' >> ~/.bashrc
EOF
    fi

    cat << 'EOF'

验证安装:  java -version

EOF
    exit 1
fi

# -------------------------------------------------------------------------
# 启动应用
# -------------------------------------------------------------------------

# 直接启动，使用 JAVA_OPTS 和透传所有参数
exec "$JAVA_CMD" $JAVA_OPTS -Dspring.servlet.multipart.max-file-size=10GB -Dspring.servlet.multipart.max-request-size=10GB -jar "$JAR_FILE" "$@"