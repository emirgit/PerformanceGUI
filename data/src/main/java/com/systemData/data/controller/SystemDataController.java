package com.systemData.data.controller;

import com.systemData.data.model.NetworkUsage;
import com.systemData.data.service.SystemDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * SystemDataController
 * It is controller for the url api/system
 * It uses log4j2 to log the exceptions
 * It uses oshi library to take network datas
 */
@RestController
@RequestMapping("/api/system")
public class SystemDataController {

    private static final Logger logger = LoggerFactory.getLogger(SystemDataController.class);

    private final SystemDataService systemDataService;

    private final List<String> validCommands = Arrays.asList("start", "stop");
    private final List<String> validElements = Arrays.asList("cpu", "memory", "disk", "network");


    private NetworkUsage networkUsage;

    /**
     *
     * @param systemDataService
     * constructor dependency injection
     * It instantiates Network usage with 0 to get initial state of Networks
     */
    @Autowired
    public SystemDataController(SystemDataService systemDataService) {
        this.systemDataService = systemDataService;
        networkUsage = new NetworkUsage(0, 0);
        systemDataService.initializeNetworkUsage(networkUsage);
    }

    /**
     * triggerDataSending methods
     * posting the datas in 1 second interval.
     * @return information
     */
    @Scheduled(fixedRate = 1000) // Execute every 1000 milliseconds
    public String triggerDataSending() {

        logger.trace("Data sending triggered");
        systemDataService.sendSystemData(networkUsage);
        return "Data sending triggered";
    }

    /**
     * checkCommands takes the parameter that posted to /api/system/command part
     * @param command
     *
     * It checks whether the parameter is v alid or not by the given rules (field of the class)
     * @return information
     */
    @PostMapping("/command")
    public String checkCommands(@RequestParam String command) {

        logger.trace("Commands is checking" + command);
        try {
            String[] parts = command.trim().toLowerCase().split("\\s+");
            if (parts.length != 2) {
                logger.info("Wrong Command !");
                return "Invalid command format. Use 'command element' format.";
            }

            String commandPart = parts[0];
            String elementPart = parts[1];
            // Validate command and element
            if (!validCommands.contains(commandPart) || !validElements.contains(elementPart)) {
                logger.info("Invalid command !");
                return "Invalid command or element. Please use valid command and element.";
            }

            String methodName = commandPart + elementPart.substring(0, 1).toUpperCase() + elementPart.substring(1);
            Method method = findMethod(methodName);

            if (method != null) {
                method.invoke(systemDataService);
            }
            else {
                return "No method found for command: " + methodName;
            }
        }
        catch (Exception ex) {
            logger.error("Controller: Error while performing action", ex);
            return "Exception occurred: " + ex.getMessage();
        }
        return "Test command executed: " + command;
    }

    /**
     * findMethod uses for ease
     * It uses reflection feature in Java
     * @param methodName
     * @return
     */
    private Method findMethod(String methodName) {
        try {
            return SystemDataService.class.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            logger.error("Controller: Error while performing action", e);
        }
        return null;
    }
}

