/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stevehobbs.xbmc;

/**
 *
 * @author steve
 */
public class XbmcSystem {

    private String xbmcName;
    private String xbmcHost;
    private Integer xbmcPort;
    private String xbmcLocation;
    private Thread xbmcThread;
    
    public XbmcSystem(String xbmcName, String xbmcHost, int xbmcPort, String xbmcLocation) {
        setXbmcName(xbmcName);
        setXbmcHost(xbmcHost);
        setXbmcPort(xbmcPort);
        setXbmcLocation(xbmcLocation);
        setXbmcThread(null);
    }

     public void setXbmcName(String xbmcName) {
        this.xbmcName = xbmcName;
    }
     
     public String getxbmcName() {
        return xbmcName;
    }
    public void setXbmcHost(String xbmcHost) {
        this.xbmcHost = xbmcHost;
    }
     
     public String getxbmcHost() {
        return xbmcHost;
     }
     
     public void setXbmcLocation(String xbmcLocation) {
        this.xbmcLocation = xbmcLocation;
    }
     
     public String getxbmcLocation() {
        return xbmcLocation;
     }
     
    public void setXbmcPort(int xbmcPort) {
        this.xbmcPort = xbmcPort;
    }

    public int getXbmcPort() {
        return xbmcPort;
    }
    
public void setXbmcThread(Thread xbmcThread) {
        this.xbmcThread = xbmcThread;
    }

    public Thread getXbmcThread() {
        return xbmcThread;
    }
}
