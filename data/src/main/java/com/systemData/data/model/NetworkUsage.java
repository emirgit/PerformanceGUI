package com.systemData.data.model;

/**
 * Basic NetworkUsage class to keep the data of network
 */
public class NetworkUsage {

    private long networkReceivedKb;
    private long networkSentKb;

    public NetworkUsage(long networkReceivedKb, long networkSentKb) {
        this.networkReceivedKb = networkReceivedKb;
        this.networkSentKb = networkSentKb;
    }

    public long getNetworkReceivedKb() {
        return networkReceivedKb;
    }

    public void setNetworkReceivedKb(long networkReceivedKb) {
        this.networkReceivedKb = networkReceivedKb;
    }

    public long getNetworkSentKb() {
        return networkSentKb;
    }

    public void setNetworkSentKb(long networkSentKb) {
        this.networkSentKb = networkSentKb;
    }
}
