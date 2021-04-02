/**
 * © ATRAK 2021
 */
package com.atrak.aim.cardmon.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides server IP addresses and resolving IP address.
 * 
 * @author Josef Valo
 */
@Slf4j
@Component
public class NetworkInterfaceService {

    /**
     * Resolves IP address from hostname.
     * 
     * @return resolved IP address.
     * @throws UnknownHostException if host was not found
     */
    public String resolveIP(final String hostname) throws UnknownHostException {
        final InetAddress addr = InetAddress.getByName(hostname);

        return addr.getHostAddress();
    }

    /**
     * @return List of the server IP addresses
     */
    public List<String> getIpAddress() {
        final List<String> result = new ArrayList<>();

        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                final NetworkInterface inter = interfaces.nextElement();
                final Enumeration<InetAddress> addresses = inter.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();
                    result.add(address.getHostAddress());
                }
            }
        } catch (SocketException e) {
            log.warn("Server does not have any IP address", e);
        }

        return result;
    }
}