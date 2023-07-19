package com.github.jsbxyyx.fs;

import com.github.jsbxyyx.fs.controller.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.github.jsbxyyx"})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        String port = context.getEnvironment().getProperty("server.port");
        log.info("\n\n\thttp://{}:{}\n\thttp://127.0.0.1:{}",
                NetUtil.getLocalIp("192.", "10."), port, port);
    }

}