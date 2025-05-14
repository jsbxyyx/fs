package com.github.jsbxyyx.fs;

import com.github.jsbxyyx.fs.args.ArgsParser;
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
        parseArgs(args);
        String network = System.getProperty("network", "192.168,10.");
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        String port = context.getEnvironment().getProperty("server.port");
        log.info("\n\n\thttp://{}:{}\n\thttp://127.0.0.1:{}\n",
                NetUtil.getLocalIp(network.split(",")), port, port);
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
            }
            Boolean download = parser.getParsedOptionValue("download");
            if (download != null) {
                System.setProperty("fs.download", download.toString());
            }
            Boolean upload = parser.getParsedOptionValue("upload");
            if (upload != null) {
                System.setProperty("fs.upload", upload.toString());
            }
        } catch (ArgsParser.ParseException e) {
            parser.printUsage();
            System.exit(0);
        }
    }

}