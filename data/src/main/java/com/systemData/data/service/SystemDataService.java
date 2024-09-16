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

    public void sendSystemData(NetworkUsage prevNetworkUsage) {
        // Get CPU usage using OperatingSystemMXBean
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = osBean.getSystemCpuLoad() * 100;

        // Get Memory usage
        long totalMemoryMB = memory.getTotal() / 1024 / 1024;
        long availableMemoryMB = memory.getAvailable() / 1024 / 1024;
        long usedMemoryMB = totalMemoryMB - availableMemoryMB;

        // Get Disk usage
        File root = new File("/");
        long totalDiskMB = root.getTotalSpace() / (1024 * 1024); // MB
        long freeDiskMB = root.getFreeSpace() / (1024 * 1024); // MB
        long usedDiskMB = totalDiskMB - freeDiskMB;


        NetworkUsage curNetworkUsage = new NetworkUsage(0, 0);
        for (NetworkIF netIf : networkInterfaces) {
            netIf.updateAttributes(); // Update network statistics
            curNetworkUsage.setNetworkReceivedKB(curNetworkUsage.getNetworkReceivedKB() + netIf.getBytesRecv() / 1024);
            curNetworkUsage.setNetworkSentKB(curNetworkUsage.getNetworkSentKB() + netIf.getBytesSent() / 1024);
        }

        long networkReceivedKB = curNetworkUsage.getNetworkReceivedKB() - prevNetworkUsage.getNetworkReceivedKB();
        long networkSentKB = curNetworkUsage.getNetworkSentKB() - prevNetworkUsage.getNetworkSentKB();

        prevNetworkUsage.setNetworkReceivedKB(curNetworkUsage.getNetworkReceivedKB());
        prevNetworkUsage.setNetworkSentKB(curNetworkUsage.getNetworkSentKB());
        // Create payload
        String payload = String.format(Locale.US,
                "CPU: %.2f %%\n" +
                        "RAM: %d MB / %d MB\n" +
                        "DISK: %d MB / %d MB\n" +
                        "NETWORK: %d KB Received / %d KB Sent",
                cpuUsage,
                usedMemoryMB, totalMemoryMB,
                usedDiskMB, totalDiskMB,
                networkReceivedKB, networkSentKB
        );

        // Send data to another microservice
        String otherMicroserviceUrl = "http://localhost:8081/api/performance";

        counter += 1;
        //Using RestTemplate to send data
        restTemplate.postForObject(otherMicroserviceUrl, payload, String.class);
        System.out.println(counter + ". Data sent to other microservice: \n" + payload);
    }

}


