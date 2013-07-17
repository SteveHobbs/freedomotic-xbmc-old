package com.stevehobbs.xbmc;

import it.freedomotic.api.EventTemplate;
import it.freedomotic.api.Protocol;
import it.freedomotic.app.Freedomotic;
import it.freedomotic.events.ProtocolRead;
import it.freedomotic.exceptions.UnableToExecuteException;
import it.freedomotic.reactions.Command;
import java.io.IOException;import java.util.List;
import java.util.ArrayList;



    public class Xbmc extends Protocol {
    final int POLLING_WAIT;
    List<XbmcSystem> systemList = new ArrayList<XbmcSystem>();
    Integer numberOfThreads;
    // phase 2 pick up from manifest file in a list
    // pass 3 (?) use bonjor/JmDNS to detect notifications automatically  ??maybe              
    
    public Xbmc() {
    //every plugin needs a name and a manifest XML file
    super("Xbmc", "/com.stevehobbs.xbmc/xbmc-manifest.xml");
    //read a property from the manifest file below which is in
    //FREEDOMOTIC_FOLDER/plugins/devices/it.freedomotic.xbmc/xbmc.xml
    POLLING_WAIT = configuration.getIntProperty("time-between-reads", 2000); // Not sure if needed?
    //POLLING_WAIT is the value of the property "time-between-reads" or 2000 millisecs,
    //default value if the property does not exist in the manifest
    setPollingWait(POLLING_WAIT); //millisecs interval between hardware device status reads
    loadXbmcSystems();
    }
    
    @Override
    protected void onStart() {
        // will pickup address & port from manifest or Bonjour/JmDNS in next phases
        // not sure about thread handling standards in freedomotic??
        
        System.out.println("Number of Tuples = " + numberOfThreads); 
        Freedomotic.logger.severe("Number of Tuples = " + numberOfThreads); 
        
        XbmcSystem thisXbmcSystem;
        String thisHost;
        Integer thisPort;
        Thread thisThread;
        for (Integer counter =0 ; counter < numberOfThreads ; counter++){
            thisXbmcSystem = systemList.get(counter);
            thisHost = thisXbmcSystem.getxbmcHost();
            thisPort = thisXbmcSystem.getXbmcPort();
            thisThread = new Thread(new XbmcThread(thisHost,thisPort), "xbmcPluginThread"+counter);
            thisXbmcSystem.setXbmcThread(thisThread);
            thisXbmcSystem.getXbmcThread().start();
            System.out.println("xbmcPluginThread" + counter + "started");
        }
   
        Freedomotic.logger.info("XBMC plugin is started");
    }
    
    @Override
    protected void onShowGui() {
        /**
         * uncomment the line below to add a GUI to this plugin the GUI can be
         * started with a right-click on plugin list on the desktop frontend
         * (it.freedomotic.jfrontend plugin)
         */
        //bindGuiToPlugin(new HelloWorldGui(this));
    }

    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {
        XbmcSystem thisXbmcSystem;
        String thisHost;
        String thisState;
        Thread thisThread;
        Freedomotic.logger.info("xbmc onRun() logs this message every "
                + "POLLINGWAIT=" + POLLING_WAIT + "milliseconds");
        for (Integer counter = 0 ; counter < numberOfThreads ; counter++){
            thisXbmcSystem = systemList.get(counter);
            thisHost = thisXbmcSystem.getxbmcHost();
            thisState = thisXbmcSystem.getXbmcThread().getState().toString();
            System.out.println("Host : "+ thisHost + " State : " + thisState); // just checking to see if any die
        }

        ProtocolRead event = new ProtocolRead(this, "XBMC", "XBMC-W7");
        event.addProperty("powered","true");
        Freedomotic.sendEvent(event);
             
    }
        //at the end of this method the system waits POLLINGTIME 
        //before calling it again. The result is this log message is printed
        //every 2 seconds (2000 millisecs)
    

    @Override
    protected void onStop() {
        Freedomotic.logger.info("HelloWorld plugin is stopped ");
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        Freedomotic.logger.info("HelloWorld plugin receives a command called " + c.getName()
                + " with parameters " + c.getProperties().toString());
    }

    @Override
    protected boolean canExecute(Command c) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private void loadXbmcSystems(){
    Integer counter;
    String xbmcName;
    String xbmcHost;
    Integer xbmcPort;
    String xbmcLocation;
    String result;
    
    numberOfThreads = configuration.getTuples().size();
    
    for (counter = 0 ; counter < numberOfThreads; counter++) {
        result = configuration.getTuples().getProperty(counter, "System");
        if (result != null) { 
             xbmcHost = configuration.getTuples().getStringProperty(counter, "XBMCHost", "localhost");
             xbmcPort = configuration.getTuples().getIntProperty(counter, "XBMCPort", 9090);
             xbmcName = configuration.getTuples().getStringProperty(counter, "System", "none");
             xbmcLocation =  configuration.getTuples().getStringProperty(counter, "Location", "none");
             XbmcSystem xbmcSystem = new XbmcSystem(xbmcName, xbmcHost,xbmcPort,xbmcLocation);
             systemList.add(xbmcSystem);
         }
    }  
    } 

}