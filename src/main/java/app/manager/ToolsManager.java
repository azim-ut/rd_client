package app.manager;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class ToolsManager {

    public static String getMacAddressLink() throws InternalError {
        String macAddr = getMacAddress();
        StringBuilder linkBuilder = new StringBuilder();
        linkBuilder.append("https://it-prom.com/rd?trg=");
        linkBuilder.append(macAddr);
        return linkBuilder.toString();
    }

    public static String getMacAddress() throws InternalError {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            return String.join("", hexadecimal);
        } catch (SocketException | UnknownHostException e) {
            log.error("MacAddress define exception: " + e.getMessage(), e);
        }
        throw new InternalError("Can't get mac address");
    }
}
