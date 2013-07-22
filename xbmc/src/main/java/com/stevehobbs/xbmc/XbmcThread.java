package com.stevehobbs.xbmc;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonEncoding;
import it.freedomotic.app.Freedomotic;
import it.freedomotic.events.ProtocolRead;
import java.io.IOException;
import java.io.*;

/**
 *
 * @author steve
 */
public class XbmcThread implements Runnable {
    private String str;
    private Integer port;
    
    public XbmcThread(String str, Integer port) {
//	super(str);
        this.str = str;
        this.port = port;
    }
    
     @Override
     public void run() {
        BufferedOutputStream myOutputStream = null;
        InputStream myInputStream = null;
        
        do{
            try{
                while (myInputStream ==null) {
                    Streams myStreams = new Streams(str,port);
                    Thread.sleep(4000); //really only needed for retry logic
                    myInputStream = myStreams.getInputStream();
                    myOutputStream = myStreams.getOutputStream();
                    if(myOutputStream != null) {
                       sendJsonPing(myOutputStream);//Do an initial ping when xbmc connected, need to check timing of response
                    }
                }
            } 
            catch(InterruptedException ieException){
                 System.out.println(str+ " interruption"); //Not sure what to do here
            }
            catch(IOException ioException){
                System.out.println(str+ " ioException");//lost the stream probably, xbmc closed? 
                myInputStream = null; //Stream has gone away get get new one
            }
            
            try {
                if(myInputStream != null) {
                    parseJson(myInputStream,myOutputStream); // check incomming json messages
                }
            } 
            catch (IOException ioException) {
                myInputStream = null; //Stream has gone away get get new one
            }
        } while (true);
     }
     
     
    public void parseJson(InputStream inJsonStream, BufferedOutputStream outJsonStream) throws IOException {
        String methodXbmc = "";
        String senderXbmc = "";
        String fieldName = "";
        String fullMessage = "";
        JsonFactory factory = new JsonFactory();
        JsonParser jasonParser = factory.createJsonParser(inJsonStream);
        jasonParser.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE); //stop jasonParser.close from closing stream
        try{
            if (jasonParser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("no data") ; //stream probably went away                   
            }
            while (jasonParser.nextToken() != JsonToken.END_OBJECT) {
                fieldName = jasonParser.getCurrentName();
                fullMessage = fullMessage + "|" + fieldName;
                jasonParser.nextToken(); // move to value, or START_OBJECT/START_ARRAY
                fullMessage = fullMessage + ":" + jasonParser.getText();
                if ("method".equals(fieldName)) { // contains an object in normal XBMC message structure
                    methodXbmc = jasonParser.getText();
                } else if("id".equals(fieldName)) { //
                    senderXbmc = jasonParser.getText();
                    while (jasonParser.nextToken() != JsonToken.END_OBJECT) {
                        fieldName = jasonParser.getCurrentName();
                        jasonParser.nextToken();// move to value
                        if("id".equals(fieldName)) {
                            senderXbmc = jasonParser.getText();
                        }
                    }
                }
            } //finished parsing the current json message
            if (methodXbmc.equals("")) { //if we didn't find a std xbmc msg pickup the last fieldname
                methodXbmc = fieldName;
            }
            // Do stuff here
            Freedomotic.logger.severe(str+" : "+fullMessage); //log xbmc action for now
            Freedomotic.logger.severe(str+" : "+ senderXbmc + " : " + methodXbmc); //log xbmc action for now
            //if (methodXbmc.equals("System.OnQuit")) {
                ProtocolRead event = new ProtocolRead(this, "XBMC", str);
                    event.addProperty("powered","true");
                    //event.addProperty("object.class","XBMC");
                    event.addProperty("object.name", "XBMC-" + str);
               Freedomotic.sendEvent(event);
            //}
     
        } 
        
        catch(IOException ioException){
            throw new IOException( "lost connection or bad data");//lost the stream probably
        }
        
       jasonParser.close(); 
    }

    public void sendJsonPing(BufferedOutputStream outPingStream) throws IOException {
        JsonFactory factory = new JsonFactory();
 
        JsonGenerator jasonGenerator = factory.createJsonGenerator(outPingStream,JsonEncoding.UTF8);
        jasonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET); //stop jasonGenerator.close from closing stream
        
        jasonGenerator.writeStartObject();
        jasonGenerator.writeStringField("jsonrpc", "2.0");
        jasonGenerator.writeStringField("method", "JSONRPC.Ping");
        jasonGenerator.writeStringField("id", "freedomotic");
        jasonGenerator.writeEndObject();
        
        jasonGenerator.close();
   
   }
}