package com.github.jsbxyyx.fs.args;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArgsParser {

    private static final String H_LINE = "-";
    private static final String H_LINE2 = "--";

    private final String command;
    private final List<String> parameters = new ArrayList<>();
    private final Map<String/*argName*/, List<Object>> values = new LinkedHashMap<>();
    private final Map<String/*argName*/, Option> options = new LinkedHashMap<>();
    private final Map<String/*-option | --option*/, String/*argName*/> names = new HashMap<>();

    public ArgsParser(String command) {
        this.command = command;
    }

    public ArgsParser addOption(Option option) {
        if (names.containsKey(H_LINE + option.getOpt()) || names.containsKey(H_LINE2 + option.getLongOpt())) {
            throw new ParseException(H_LINE + option.getOpt() + "," + H_LINE2 + option.getLongOpt() + " already exists");
        }
        names.put(H_LINE + option.getOpt(), option.getArgName());
        names.put(H_LINE2 + option.getLongOpt(), option.getArgName());
        options.put(option.getArgName(), option);
        return this;
    }

    public <T> List<T> getValue(String name) {
        return (List<T>) values.get(name);
    }

    public <T> T getFirstValue(String name) {
        return getValue(name, 0);
    }

    public <T> T getValue(String name, int index) {
        List<T> arg = getValue(name);
        return arg == null || arg.isEmpty() ? null : arg.get(index);
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

    public void printUsage() {
        StringBuilder usage = new StringBuilder();
        usage.append("Usage: ").append(command).append("\n");
        for (Option option : options.values()) {
            usage.append(String.format("\n%s,%-20s %s", H_LINE + option.getOpt(), H_LINE2 + option.getLongOpt(), option.getDescription()));
        }
        System.out.println(usage);
    }

    public ArgsParser parse(String[] args) {
        try {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];
                    String argName = names.get(arg);
                    if (argName != null) {
                        Option option = options.get(argName);
                        if (!option.isHelp()) {
                            i++;
                            Object value = option.getConverter().apply(args[i]);
                            if (values.containsKey(option.getArgName())) {
                                values.get(option.getArgName()).add(value);
                            } else {
                                List<Object> list = new ArrayList<>();
                                list.add(value);
                                values.put(option.getArgName(), list);
                            }
                        } else {
                            values.put(option.getArgName(), new ArrayList<>());
                        }
                    } else {
                        parameters.add(arg);
                    }
                }
            }
        } catch (Exception e) {
            throw new ParseException();
        }
        return this;
    }

    public boolean hasOption(String argName) {
        return values.containsKey(argName);
    }

    public static class Option {

        private final String opt;
        private final String longOpt;
        private final String argName;
        private final String description;
        private boolean required;
        private Converter<?> converter = String::valueOf;
        private boolean help;

        public Option(String opt, String longOpt, String argName, String description) {
            this.opt = Objects.requireNonNull(opt);
            this.longOpt = Objects.requireNonNull(longOpt);
            this.argName = Objects.requireNonNull(argName);
            this.description = Objects.requireNonNull(description);
        }

        public Option required(boolean required) {
            this.required = required;
            return this;
        }

        public Option converter(Converter<?> converter) {
            this.converter = Objects.requireNonNull(converter);
            return this;
        }

        public Option help(boolean help) {
            this.help = help;
            return this;
        }

        public String getOpt() {
            return opt;
        }

        public String getLongOpt() {
            return longOpt;
        }

        public String getArgName() {
            return argName;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return required;
        }

        public Converter<?> getConverter() {
            return converter;
        }

        public boolean isHelp() {
            return help;
        }
    }

    @FunctionalInterface
    public interface Converter<T> {
        T apply(String string);
    }

    public static class ParseException extends RuntimeException {
        public ParseException() {
            super("args parse error");
        }

        public ParseException(String message) {
            super(message);
        }
    }

}
