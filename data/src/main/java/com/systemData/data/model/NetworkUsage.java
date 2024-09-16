package com.systemData.data.model;

public class NetworkUsage {

    private long networkReceivedKB;
    private long networkSentKB;

    public NetworkUsage(long networkReceivedKB, long networkSentKB) {
        this.networkReceivedKB = networkReceivedKB;
        this.networkSentKB = networkSentKB;
    }

    public long getNetworkReceivedKB() {
        return networkReceivedKB;
    }

    public void setNetworkReceivedKB(long networkReceivedKB) {
        this.networkReceivedKB = networkReceivedKB;
    }

    public long getNetworkSentKB() {
        return networkSentKB;
    }

    public void setNetworkSentKB(long networkSentKB) {
        this.networkSentKB = networkSentKB;
    }
}
