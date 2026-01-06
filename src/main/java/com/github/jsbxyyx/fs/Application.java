package com.github.jsbxyyx.fs;

import com.github.jsbxyyx.fs.args.ArgsParser;
import com.github.jsbxyyx.fs.controller.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.github.jsbxyyx"})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        parseArgs(args);
        String network = System.getProperty("network", "192.168,10.");
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        String port = context.getEnvironment().getProperty("server.port");

        StringBuilder ipStr = new StringBuilder();
        List<String> localIpList = NetUtil.getLocalIpList(network.split(","));
        for (String ip : localIpList) {
            ipStr.append("\t - ").append("http://").append(ip).append(":").append(port).append("\n");
        }
        ipStr.append("\n");
        ipStr.append("\t").append("确保所有设备连接到相同得网络。").append("\n");
        ipStr.append("\t").append("Ensure all devices are connected to the same network.").append("\n");
        ipStr.append("\n");
        ipStr.append("\t").append("用手机或者其他网络设备的浏览器打开上面得网址。").append("\n");
        ipStr.append("\t").append("Open the URL above in your phone's browser or on any other device.").append("\n");
        log.info("\n\n{}", ipStr);
    }

    public static void parseArgs(String[] args) {
        ArgsParser parser = new ArgsParser("java -jar fs.jar [options] [dir]");
        try {
            parser.addOption(new ArgsParser.Option("h", "help", "help", "Print this message")
                            .help(true))
                    .addOption(new ArgsParser.Option("d", "download", "download", "Enable download true/false")
                            .converter(Boolean::valueOf))
                    .addOption(new ArgsParser.Option("u", "upload", "upload", "Enable upload true/false")
                            .converter(Boolean::valueOf));
            parser.parse(args);
            if (parser.hasOption("help")) {
                parser.printUsage();
                System.exit(0);
            }
            if (!parser.getParameters().isEmpty()) {
                System.setProperty("fs.dir", parser.getParameters().get(0));
                log.info("file dir : {}", parser.getParameters().get(0));
            }
            Boolean download = parser.getFirstValue("download");
            if (download != null) {
                System.setProperty("fs.download", download.toString());
                log.info("enable download : {}", download);
            }
            Boolean upload = parser.getFirstValue("upload");
            if (upload != null) {
                System.setProperty("fs.upload", upload.toString());
                log.info("enable upload : {}", upload);
            }
        } catch (ArgsParser.ParseException e) {
            parser.printUsage();
            System.exit(0);
        }
    }

}