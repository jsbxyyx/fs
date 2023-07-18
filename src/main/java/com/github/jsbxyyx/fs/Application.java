package com.github.jsbxyyx.fs;

import com.github.jsbxyyx.fs.controller.NetUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.github.jsbxyyx"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("local ip : " + NetUtil.getLocalIp("192.", "10."));
    }

}