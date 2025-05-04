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
        if (args != null && args.length > 0) {
            System.setProperty("fs.dir", args[0]);
        }
        String network = System.getProperty("network", "192.168,10.");
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        String port = context.getEnvironment().getProperty("server.port");
        log.info("\n\n\thttp://{}:{}\n\thttp://127.0.0.1:{}\n",
                NetUtil.getLocalIp(network.split(",")), port, port);
    }

}