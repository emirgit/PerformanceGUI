package com.performance.gui.service;

import com.performance.gui.model.SystemData;
import com.performance.gui.repository.SystemDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SystemDataService {

    private final SystemDataRepository systemDataRepository;

    private static final double DEFAULT_VALUE = -1;
    private static final long L_DEFAULT_VALUE = -1;

    private static final String DEFAULT_STRING = "";

    private Queue<SystemData> dataQueue;

    @Autowired
    public SystemDataService(SystemDataRepository systemDataRepository) {
        this.systemDataRepository = systemDataRepository;
        dataQueue = new LinkedList<>();
    }

    public SystemData convertStringToData(String data) {
        // Parse the data
        double cpuUsage = parseDoubleValue(data, "CPU: ([\\d.]+) %");
        double ramUsage = parseDoubleValue(data, "RAM: ([\\d.]+) %");
        double diskUsage = parseDoubleValue(data, "DISK: ([\\d.]+) %");
        long networkReceivedKB = parseLongValue(data, "NETWORK: (\\d+) Kb Received");
        long networkSentKB = parseLongValue(data, "NETWORK: \\d+ Kb Received / (\\d+) Kb Sent");
        String log = parseLogData(data, "LOG: (.+)");

        // Create and save the SystemData object
        SystemData systemData = new SystemData();
        systemData.setTimestamp(LocalDateTime.now());
        systemData.setCpuUsage(cpuUsage);
        systemData.setRamUsage(ramUsage);
        systemData.setDiskUsage(diskUsage);
        systemData.setNetworkReceivedKB(networkReceivedKB);
        systemData.setNetworkSentKB(networkSentKB);
        systemData.setLog(log);

        return systemData;
    }

    public List<SystemData> processAndSaveData(SystemData systemData) {

        //Process the Data
        if (dataQueue.size() >= 600)
            dataQueue.remove();

        dataQueue.add(systemData);

        // save the data to database
        System.out.println(systemData);
        systemDataRepository.save(systemData);

        return dataQueue.stream().collect(Collectors.toList());
    }

    private double parseDoubleValue(String data, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(data);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return DEFAULT_VALUE; // Default value if pattern is not found
    }

    private long parseLongValue(String data, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(data);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return L_DEFAULT_VALUE;// Default value if pattern is not found
    }
    public String parseLogData(String data, String pattern) {

        // Create a Pattern object
        Pattern regexPattern = Pattern.compile(pattern);

        // Create matcher object
        Matcher matcher = regexPattern.matcher(data);

        if (matcher.find()) {
            // Extract log message
            return matcher.group(1);
        }

        // Return a default message or empty string if pattern is not found
        return DEFAULT_STRING;
    }
}

