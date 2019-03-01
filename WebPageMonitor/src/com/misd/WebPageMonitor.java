package com.misd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class WebPageMonitor {
	private static final Logger logger = Logger.getLogger(WebPageMonitor.class);

	static LoadConfig loadConfig = null;

	public static void main(String[] args) {
		//ETAPrinter printer = null;
		int delay = 0;
		try {
			loadConfig = new LoadConfig("MonitorConfig.properties");
			delay = Integer.parseInt(loadConfig.getElement("Monitor.DelayInMin"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
		}

		while (true) {
			try {

				String old_response = getText(loadConfig.getElement("Monitor.URL"));
				old_response = old_response.replaceAll(loadConfig.getElement("Monitor.IgnorePattern"), "");

				String new_response = getText(loadConfig.getElement("Monitor.URL"));

				new_response = new_response.replaceAll(loadConfig.getElement("Monitor.IgnorePattern"), "");

				if (old_response.equalsIgnoreCase(new_response)) {
					logger.info("No change detected");
				} else {
					logger.error("Its different");
					logger.error("Old : " + old_response);
					logger.error("New : " + new_response);

					String msg2send = loadConfig.getElement("Monitor.ErrorMessage");
					String msg = URLEncoder.encode(msg2send, "UTF-8");

					if (sendSMS(msg))
						logger.info("Successfully send sms : " + msg);

					if (sendEMail("WebPageMonitor", msg)) {
						logger.info("Successfully send sms : " + msg);

					}
					old_response = new_response;

				}


				for (int i = (delay*60); i > 0; i--) {
					System.out.print("\rNext try in "+i+" Sec..  ");
					Thread.sleep(1000);
				}
				System.out.print("\r");

			} catch (WebPageMonitorException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
				try {
					sendSMS(e.getMessage());
					sendEMail("WebPageMonitor", e.getMessage());
				} catch (Exception ex) {
					logger.error(ex.getMessage());
				}
			}
		}

	}

	public static boolean sendEMail(String subject, String msg) throws WebPageMonitorException {
		try {

			Properties props = new Properties();

			loadConfig.getElement("SMSGateway.URL");

			props.put("mail.smtp.host", loadConfig.getElement("Mail.Smtphost"));
			props.put("mail.smtp.socketFactory.port", loadConfig.getElement("Mail.Smtpport"));
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", loadConfig.getElement("Mail.Smtp-auth"));
			props.put("mail.smtp.port", loadConfig.getElement("Mail.Smtpport"));

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(loadConfig.getElement("Mail.Username"),
							loadConfig.getElement("Mail.Password"));
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(loadConfig.getElement("Mail.Fromaddress")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(loadConfig.getElement("Mail.Toaddress")));
			message.setSubject(subject);
			message.setText(msg);

			Transport.send(message);
			return true;

		} catch (MessagingException e) {
			logger.error(e);
			e.printStackTrace();
			throw new WebPageMonitorException("Error while sending email");
		}
	}

	private static boolean sendSMS(String message) throws WebPageMonitorException {

		try {
			String url = "";
			url += loadConfig.getElement("SMSGateway.URL");
			url += loadConfig.getElement("SMSGateway.UserIDParamName");
			url += "=";
			url += loadConfig.getElement("SMSGateway.UserID");
			url += "&";
			url += loadConfig.getElement("SMSGateway.PasswordParamName");
			url += "=";
			url += loadConfig.getElement("SMSGateway.Password");
			url += "&";
			url += "Message=" + message + "&";
			url += loadConfig.getElement("SMSGateway.MobileNoParam");
			url += "=";
			url += loadConfig.getElement("SMSGateway.MsgSend2MobileNo");
			url += "&";
			url += loadConfig.getElement("SMSGateway.GSMIDParam");
			url += "=";
			url += loadConfig.getElement("SMSGateway.GSMID");

			System.out.println(url);
			logger.info(url);

			logger.info(getText(url));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
			throw new WebPageMonitorException("Error while sending SMS");
		}
		return true;

	}

	public static String getText(String url) throws Exception {

		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);

		in.close();

		return response.toString();
	}

}