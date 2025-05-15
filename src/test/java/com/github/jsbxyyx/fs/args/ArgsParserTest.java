package com.github.jsbxyyx.fs.args;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArgsParserTest {

    @Test
    void test_parse() {
        String[] args = new String[]{"--download", "true"};

        ArgsParser parser = new ArgsParser("java -jar fs.jar")
                .addOption(new ArgsParser.Option("h", "help", "help", "Print this message"));
        parser.parse(args);
        Assertions.assertEquals(2, parser.getParameters().size());
    }

    @Test
    void test_parse2() {
        String[] args = new String[]{"--download", "true"};

        ArgsParser parser = new ArgsParser("java -jar fs.jar")
                .addOption(new ArgsParser.Option("h", "help", "help", "Print this message"))
                .addOption(new ArgsParser.Option("d", "download", "download", "enable download true/false"));
        parser.parse(args);
        Assertions.assertEquals(0, parser.getParameters().size());
        Assertions.assertTrue(parser.hasOption("download"));
        Assertions.assertFalse(parser.hasOption("upload"));
    }

    @Test
    void test_parse3() {
        String[] args = new String[]{"-d", "true"};

        ArgsParser parser = new ArgsParser("java -jar fs.jar")
                .addOption(new ArgsParser.Option("h", "help", "help", "Print this message"))
                .addOption(new ArgsParser.Option("d", "download", "download", "enable download true/false")
                        .converter(Boolean::valueOf));
        parser.parse(args);
        Assertions.assertEquals(0, parser.getParameters().size());
        Assertions.assertTrue(parser.hasOption("download"));
        Assertions.assertEquals(true, parser.getFirstValue("download"));
        Assertions.assertNull(parser.getFirstValue("d"));
        Assertions.assertFalse(parser.hasOption("upload"));
    }

    @Test
    void test_parse4() {
        String[] args = new String[]{"-c", "true"};
        ArgsParser parser = new ArgsParser("java -jar fs.jar")
                .addOption(new ArgsParser.Option("h", "help", "help", "Print this message"))
                .addOption(new ArgsParser.Option("c", "count", "count", "print count number")
                        .converter(Integer::valueOf));
        Assertions.assertThrows(ArgsParser.ParseException.class, () -> parser.parse(args));
    }

    @Test
    void test_parse5() {
        String[] args = new String[]{"-c", "true", "-c", "false"};
        ArgsParser parser = new ArgsParser("java -jar fs.jar")
                .addOption(new ArgsParser.Option("h", "help", "help", "Print this message"))
                .addOption(new ArgsParser.Option("c", "count", "count", "print count number")
                        .converter(Boolean::valueOf));
        parser.parse(args);
        Assertions.assertEquals(2, parser.getValue("count").size());
        Assertions.assertEquals(true, parser.getFirstValue("count"));
        Assertions.assertEquals(false, parser.getValue("count", 1));
    }


}
