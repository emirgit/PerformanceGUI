package com.systemData.data.service;

import com.sun.management.OperatingSystemMXBean;
import com.systemData.data.model.NetworkUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Locale;

@Service
public class SystemDataService {

    @Autowired
    private RestTemplate restTemplate;

    private long counter = 0;

    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final GlobalMemory memory = hardware.getMemory();

    private final List<NetworkIF> networkInterfaces = hardware.getNetworkIFs();

    private final String cpuErrorMessage = "Error retrieving CPU usage.\n";
    private final String memoryErrorMessage = "Error retrieving Memory usage.\n";
    private final String diskErrorMessage = "Error retrieving Disk usage.\n";
    private final String networkErrorMessage = "Error retrieving Network usage.\n";

    public void sendSystemData(NetworkUsage prevNetworkUsage) {

        StringBuilder log = new StringBuilder();
        double cpuUsage = -10;
        double ramUsage = -10;
        double diskUsage = - 10;
        NetworkUsage curNetworkUsage = new NetworkUsage(0, 0);

        try {
            cpuUsage = getCpuUsage();
        }
        catch (Exception ex) {
            log.append(cpuErrorMessage);
            cpuUsage = -1;
        }

        try {
            ramUsage = getMemoryUsageRatio();
        }
        catch (Exception ex) {
            log.append(memoryErrorMessage);
            ramUsage = -1;
        }

        try {
            diskUsage = getDiskUsageRatio();
        }
        catch (Exception ex) {
            log.append(diskErrorMessage);
            diskUsage = -1;
        }


        try {
            //call by reference
            getNetworkUsage(prevNetworkUsage, curNetworkUsage);
        }
        catch (Exception ex) {
            log.append(networkErrorMessage);
            curNetworkUsage = new NetworkUsage(-1, -1);
        }

        String payload = String.format(Locale.US,
                "CPU: %.2f %%\n" +
                        "RAM: %.2f %%\n" +
                        "DISK: %.2f %%\n" +
                        "NETWORK: %d KB Received / %d KB Sent\n" +
                        "LOG: %s",
                cpuUsage,
                ramUsage,
                diskUsage,
                curNetworkUsage.getNetworkReceivedKb(), curNetworkUsage.getNetworkSentKb(),
                log.toString()
        );

        // Send data to another microservice
        String otherMicroserviceUrl = "http://localhost:8081/api/performance";

        counter += 1;
        //Using RestTemplate to send data
        restTemplate.postForObject(otherMicroserviceUrl, payload, String.class);
        System.out.println(counter + ". Data sent to other microservice: \n" + payload);
    }

    private double getCpuUsage() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osBean.getSystemCpuLoad() * 100;
    }

    private double getMemoryUsageRatio() {
        long totalMemoryMB = memory.getTotal();
        long availableMemoryMB = memory.getAvailable();
        return ( (double) (totalMemoryMB - availableMemoryMB) / totalMemoryMB) * 100;
    }

    private double getDiskUsageRatio() {
        File root = new File("/");
        long totalDiskMB = root.getTotalSpace() / (1024 * 1024); // MB
        long freeDiskMB = root.getFreeSpace() / (1024 * 1024); // MB
        return  ((double) (totalDiskMB - freeDiskMB) / totalDiskMB) * 100;
    }

    private void getNetworkUsage(NetworkUsage prevNetworkUsage, NetworkUsage curNetworkUsage) {
        NetworkUsage tempNetworkUsage = new NetworkUsage(0, 0);
        for (NetworkIF netIf : networkInterfaces) {
            netIf.updateAttributes(); // Update network statistics
            tempNetworkUsage.setNetworkReceivedKb(tempNetworkUsage.getNetworkReceivedKb() + netIf.getBytesRecv() / 128);
            tempNetworkUsage.setNetworkSentKb(tempNetworkUsage.getNetworkSentKb() + netIf.getBytesSent() / 128);
        }

        long networkReceivedKb = tempNetworkUsage.getNetworkReceivedKb() - prevNetworkUsage.getNetworkReceivedKb();
        long networkSentKb = tempNetworkUsage.getNetworkSentKb() - prevNetworkUsage.getNetworkSentKb();

        prevNetworkUsage.setNetworkReceivedKb(tempNetworkUsage.getNetworkReceivedKb());
        prevNetworkUsage.setNetworkSentKb(tempNetworkUsage.getNetworkSentKb());

        curNetworkUsage.setNetworkReceivedKb(networkReceivedKb);
        curNetworkUsage.setNetworkSentKb(networkSentKb);
    }

    public void initializeNetworkUsage(NetworkUsage networkUsage) {

        for (NetworkIF netIf : networkInterfaces) {
            netIf.updateAttributes(); // Update network statistics
            networkUsage.setNetworkReceivedKb(networkUsage.getNetworkReceivedKb() + netIf.getBytesRecv() / 128);
            networkUsage.setNetworkSentKb(networkUsage.getNetworkSentKb() + netIf.getBytesSent() / 128);
        }
    }

}


