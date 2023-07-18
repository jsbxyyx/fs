package com.github.jsbxyyx.fs.controller;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetUtil {

    private static final String LOCALHOST = "127.0.0.1";

    private static final String ANY_HOST = "0.0.0.0";

    private static volatile InetAddress LOCAL_ADDRESS = null;

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static String toStringAddress(SocketAddress address) {
        if (address == null) {
            return "";
        }
        return toStringAddress((InetSocketAddress) address);
    }

    public static String toIpAddress(SocketAddress address) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) address;
        return inetSocketAddress.getAddress().getHostAddress();
    }

    public static String toStringAddress(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    public static InetSocketAddress toInetSocketAddress(String address) {
        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }

    public static long toLong(String address) {
        InetSocketAddress ad = toInetSocketAddress(address);
        String[] ip = ad.getAddress().getHostAddress().split("\\.");
        long r = 0;
        r = r | (Long.parseLong(ip[0]) << 40);
        r = r | (Long.parseLong(ip[1]) << 32);
        r = r | (Long.parseLong(ip[2]) << 24);
        r = r | (Long.parseLong(ip[3]) << 16);
        r = r | ad.getPort();
        return r;
    }

    public static String getLocalIp(String... preferredNetworks) {
        InetAddress address = getLocalAddress(preferredNetworks);
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? "localhost" : address.getHostName();
    }

    public static InetAddress getLocalAddress(String... preferredNetworks) {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0(preferredNetworks);
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0(String... preferredNetworks) {
        InetAddress localAddress = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        if (network.isUp()) {
                            Enumeration<InetAddress> addresses = network.getInetAddresses();
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        if (null == localAddress) {
                                            localAddress = address;
                                        }
                                        //check preferredNetworks
                                        if (preferredNetworks.length > 0) {
                                            String ip = address.getHostAddress();
                                            for (String regex : preferredNetworks) {
                                                if (regex == null || "".equals(regex.trim())) {
                                                    continue;
                                                }
                                                if (ip.matches(regex) || ip.startsWith(regex)) {
                                                    return address;
                                                }
                                            }
                                        } else {
                                            return address;
                                        }
                                    }
                                } catch (Throwable e) {
                                    System.out.println("Failed to retrieving ip address, " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Throwable e) {
                        System.out.println("Failed to retrieving ip address, " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println("Failed to retrieving ip address, " + e.getMessage());
            e.printStackTrace();
        }
        if (localAddress == null) {
            System.out.println("Could not get local host ip address, will use 127.0.0.1 instead.");
        } else {
            System.out.println("Could not match ip by preferredNetworks:" + Arrays.toString(preferredNetworks) + ", will use default first ip " + localAddress.getHostAddress() + " instead.");
        }
        return localAddress;
    }

    public static void validAddress(InetSocketAddress address) {
        if (address.getHostName() == null || 0 == address.getPort()) {
            throw new IllegalArgumentException("invalid address:" + address);
        }
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        return isValidIp(address.getHostAddress(), false);
    }

    public static boolean isValidIp(String ip, boolean validLocalAndAny) {
        if (ip == null) {
            return false;
        }
        ip = convertIpIfNecessary(ip);
        if (validLocalAndAny) {
            return IP_PATTERN.matcher(ip).matches();
        } else {
            return !ANY_HOST.equals(ip) && !LOCALHOST.equals(ip) && IP_PATTERN.matcher(ip).matches();
        }

    }

    private static String convertIpIfNecessary(String ip) {
        if (IP_PATTERN.matcher(ip).matches()) {
            return ip;
        } else {
            try {
                return InetAddress.getByName(ip).getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

}