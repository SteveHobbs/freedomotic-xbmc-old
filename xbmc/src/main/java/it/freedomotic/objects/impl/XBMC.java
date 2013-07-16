package it.freedomotic.objects.impl;

import it.freedomotic.model.ds.Config;
import it.freedomotic.model.object.BooleanBehavior;
import it.freedomotic.objects.BooleanBehaviorLogic;
import it.freedomotic.objects.impl.ElectricDevice;
import it.freedomotic.model.ds.Config;
/**
 *
 * @author steve
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class XBMC extends ElectricDevice{
    
  private BooleanBehaviorLogic onoff; 
  
  
    @Override
    public void init() {
       //linking this property with the onoff behavior defined in the XML
        onoff = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior("powered"));
        onoff.addListener(new BooleanBehaviorLogic.Listener() {

            @Override
            public void onTrue(Config params, boolean fireCommand) {
               setOn(params);
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                setOff(params);
            }
        });
        super.init();
    }

    public void setOn(Config params) {
            //set the icon to show on
            getPojo().setCurrentRepresentation(1); //points to the second element in the XML views array 
            setChanged(true);
        }
    
    public void setOff(Config params) {
            //set the icon to show on
            getPojo().setCurrentRepresentation(0); //points to the first element in the XML views array 
            setChanged(true);
        }       
          
    @Override
    protected void createCommands() {
        //no commands for this kind of objects
        super.createCommands();
    }

    @Override
    protected void createTriggers() {
        
    }
}