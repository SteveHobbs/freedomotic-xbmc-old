package com.stevehobbs.xbmc;

import it.freedomotic.api.EventTemplate;
import it.freedomotic.api.Protocol;
import it.freedomotic.app.Freedomotic;
import it.freedomotic.exceptions.UnableToExecuteException;
import it.freedomotic.reactions.Command;
import java.io.IOException;import java.util.List;
import java.util.ArrayList;



    public class Xbmc extends Protocol {
    final int POLLING_WAIT;
    List<XbmcSystem> systemList = new ArrayList<XbmcSystem>();
    
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

        String thisHost;
        Integer thisPort;
        Thread thisThread;
                
        for (XbmcSystem thisXbmcSystem : systemList) {
            thisThread = new Thread(new XbmcThread(thisXbmcSystem), "xbmcPluginThread" + systemList.indexOf(thisXbmcSystem));
            thisXbmcSystem.setXbmcThread(thisThread);
            thisXbmcSystem.getXbmcThread().start();
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
        //bindGuiToPlugin(new Xbmc(this));
    }

    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {

        String thisHost;
        String thisState;
        Thread thisThread;

        for (XbmcSystem thisXbmcSystem : systemList) {
            thisHost = thisXbmcSystem.getXbmcHost();
            thisState = thisXbmcSystem.getXbmcThread().getState().toString();
           // System.out.println("Host : "+ thisHost + " State : " + thisState); // just checking to see if any die
        }
            
    }
 
    @Override
    protected void onStop() {
        Freedomotic.logger.info("XBMC plugin is stopped ");
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        Freedomotic.logger.info("XBMC plugin receives a command called " + c.getName()
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
    Integer numberOfSystems;
    
    numberOfSystems = configuration.getTuples().size();
    
    for (counter = 0 ; counter < numberOfSystems; counter++) {
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