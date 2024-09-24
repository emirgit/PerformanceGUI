package com.systemData.data.service;

import com.sun.management.OperatingSystemMXBean;
import com.systemData.data.model.NetworkUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * SystemDataService is the service handles all logic
 * the default values accepted as -1
 * It uses oshi for network datas, OSMXBean for memory and cpu, File for disk
 * If some exception occured when retrieve datas, it assign default value to datas
 */
@Service
public class SystemDataService {

    private static final Logger logger = LoggerFactory.getLogger(SystemDataService.class);

    private static final double DEFAULT_VALUE = -1;
    private static final long L_DEFAULT_VALUE = -1;

    private static final String cpuErrorMessage = "Error retrieving CPU usage. ";
    private static final String memoryErrorMessage = "Error retrieving Memory usage. ";
    private static final String diskErrorMessage = "Error retrieving Disk usage. ";
    private static final String networkErrorMessage = "Error retrieving Network usage. ";

    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final GlobalMemory memory = hardware.getMemory();
    private final List<NetworkIF> networkInterfaces = hardware.getNetworkIFs();

    private final RestTemplate restTemplate = new RestTemplate();

    private long counter = 0;

    private boolean simulateCpuCommand = false;
    private boolean simulateMemoryCommand = false;
    private boolean simulateDiskCommand = false;
    private boolean simulateNetworkCommand = false;

    private boolean isNetworkInitiliazed = false;

    public void sendSystemData(NetworkUsage prevNetworkUsage) {

        StringBuilder log = new StringBuilder();
        double cpuUsage = DEFAULT_VALUE;
        double ramUsage = DEFAULT_VALUE;
        double diskUsage = DEFAULT_VALUE;
        NetworkUsage curNetworkUsage = new NetworkUsage(0, 0);

        if (simulateCpuCommand) {
            logger.info("CPU data sending is closed !");
            log.append(cpuErrorMessage);
        } else {
            try {
                cpuUsage = getCpuUsage();
            } catch (Exception ex) {
                logger.info("Error while sending CPU data");
                log.append(cpuErrorMessage);
                cpuUsage = DEFAULT_VALUE;
            }
        }

        if (simulateMemoryCommand) {
            logger.info("Memory data sending is closed !");
            log.append(memoryErrorMessage);
        } else {
            try {
                ramUsage = getMemoryUsageRatio();
            } catch (Exception ex) {
                logger.info("Error while sending Memory data");
                log.append(memoryErrorMessage);
                ramUsage = DEFAULT_VALUE;
            }
        }

        if (simulateDiskCommand) {
            logger.info("Disk data sending is closed !");
            log.append(diskErrorMessage);
        } else {
            try {
                diskUsage = getDiskUsageRatio();
            } catch (Exception ex) {
                logger.info("Error while sending Disk data");
                log.append(diskErrorMessage);
                diskUsage = DEFAULT_VALUE;
            }
        }

        if (simulateNetworkCommand) {
            logger.info("Network data sending is closed !");
            log.append("Error retrieving Network usage.\n");
            curNetworkUsage.setNetworkSentKb(L_DEFAULT_VALUE);
            curNetworkUsage.setNetworkReceivedKb(L_DEFAULT_VALUE);
            isNetworkInitiliazed = false;
        }
        else {
            try {
                //call by reference
                if (isNetworkInitiliazed) {
                    initializeNetworkUsage(prevNetworkUsage);
                    isNetworkInitiliazed = true;
                    curNetworkUsage.setNetworkSentKb(L_DEFAULT_VALUE);
                    curNetworkUsage.setNetworkReceivedKb(L_DEFAULT_VALUE);
                }
                else {
                    getNetworkUsage(prevNetworkUsage, curNetworkUsage);
                }
            } catch (Exception ex) {
                logger.info("Error while sending Network data");
                log.append(networkErrorMessage);
                isNetworkInitiliazed = false;
                curNetworkUsage.setNetworkSentKb(L_DEFAULT_VALUE);
                curNetworkUsage.setNetworkReceivedKb(L_DEFAULT_VALUE);
            }
        }
        String payload = String.format(Locale.US,
                "CPU: %.2f %%\n" +
                        "RAM: %.2f %%\n" +
                        "DISK: %.2f %%\n" +
                        "NETWORK: %d Kb Received / %d Kb Sent\n" +
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
        logger.trace(counter + ". Data sent to other microservice: \n" + payload);
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

    public void startCpu() {
        this.simulateCpuCommand = false;
    }

    public void stopCpu() {
        this.simulateCpuCommand = true;
    }

    public void startMemory() {
        this.simulateMemoryCommand = false;
    }

    public void stopMemory() {
        this.simulateMemoryCommand = true;
    }

    public void startDisk() {
        this.simulateDiskCommand = false;
    }

    public void stopDisk() {
        this.simulateDiskCommand = true;
    }

    public void startNetwork() {
        this.simulateNetworkCommand = false;
    }

    public void stopNetwork() {
        this.simulateNetworkCommand = true;
    }

}


