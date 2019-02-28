package com.misd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class WebPageMonitor {
     private static final Logger logger = 
Logger.getLogger(WebPageMonitor.class);

     static LoadConfig loadConfig=null;
     public static void main(String[] args) {
         // TODO Auto-generated method stub
         try {

             loadConfig=new LoadConfig("MonitorConfig.properties");
             
             String old_response = getText(loadConfig.getElement("Monitor.URL"));
             

             while(true) {

            	 
                 String new_response = getText(loadConfig.getElement("Monitor.URL"));
                 if(old_response.equalsIgnoreCase(new_response)) {
                	 logger.info("Two files are same");
                 }else {
                     logger.error("Its different");
                     logger.error("Old : "+old_response);
                     logger.error("New : "+new_response);
                     
                     String msg=URLEncoder.encode("There is a difference in two request. Please, check the site", "UTF-8");;
                     sendSMS(msg);
                 }
                 
                 old_response = new_response;
            	 Thread.sleep(300000);                 
             }
                      }catch(Exception e) {
        	 logger.error(e);
             e.printStackTrace();
             sendSMS(e.getMessage());
         }
     }
     
     private static boolean sendSMS(String message) {

         try {
			String url = "";
			url+=loadConfig.getElement("SMSGateway.URL");
			url+=loadConfig.getElement("SMSGateway.UserIDParamName");
			url+="=";
			url+=loadConfig.getElement("SMSGateway.UserID");
			url+="&";
			url+=loadConfig.getElement("SMSGateway.PasswordParamName");
			url+="=";
			url+=loadConfig.getElement("SMSGateway.Password");
			url+="&";
			url+="Message="+message+"&";
			url+=loadConfig.getElement("SMSGateway.MobileNoParam");
			url+="=";
			url+=loadConfig.getElement("SMSGateway.MsgSend2MobileNo");
			url+="&";
			url+=loadConfig.getElement("SMSGateway.GSMIDParam");
			url+="=";
			url+=loadConfig.getElement("SMSGateway.GSMID");
			
//	       String smsurlstring="http://mobi1.blogdns.com/httpmsgid/SMSSenders.aspx?UserID=SpaceTrns&UserPass=Misd@123&Message="+old_line 
// 		 + " vs " + new_line+"&MobileNo=9429029225&GSMID=SACADM";
			
			http://mobi1.blogdns.com/httpmsgid/SMSSenders.aspx?UserID=SpaceTrns&UserPass=Misd@123&Message=There is a difference in two request. Please, check the site.&MobileNo=9429029225&GSMID=SACADM
			
			System.out.println(url);
			logger.info(url);

			logger.info(getText(url));
			  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
			return false;
		}
    	 return true;

     }
     
     public static String getText(String url) throws Exception {
    	 
         URL website = new URL(url);
         URLConnection connection = website.openConnection();
         BufferedReader in = new BufferedReader(
                                 new InputStreamReader(
                                     connection.getInputStream()));

         StringBuilder response = new StringBuilder();
         String inputLine;

         while ((inputLine = in.readLine()) != null) 
             response.append(inputLine);

         in.close();

         return response.toString();
     }
     
}