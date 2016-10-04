package com.bc.util;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

public class Emailer
{
    private static final Logger logger = Logger.getLogger(Emailer.class);
    public static final String CONTENT_TYPE_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_HTML  = "text/html";
    public static final String SMTP_PORT      = "25";
    public static final String SMTP_HOST_NAME = "192.168.1.6"; // mail.bookcountryclearinghouse.com
    public static final String FROM_USER = "info@bookcountryclearinghouse.com";
    public static final String SMTP_AUTH_USER = "book\\info";
    public static final String SMTP_AUTH_PWD  = "Password09";

    private String hostArg = SMTP_HOST_NAME;
    private String portArg = SMTP_PORT;
    private String authUserArg = SMTP_AUTH_USER;
    private String passwordArg = SMTP_AUTH_PWD;
    private String fromUserArg = FROM_USER;
    private String subjectArg = "";
    private String[] recipientsArg = {"tim@megela.com"};
    private String messageArg = "";

    public Emailer () { }

    public static void sendMail(String[] recipients,
                                String subject,
                                String message,
                                String from) throws MessagingException
    {
        sendMail(recipients, SMTP_HOST_NAME, SMTP_PORT, subject, message, from, SMTP_AUTH_USER, SMTP_AUTH_PWD, CONTENT_TYPE_HTML);
    }

    public static void sendMail(String[] recipients,
                                String subject,
                                String message) throws MessagingException
    {
        sendMail(recipients, SMTP_HOST_NAME, SMTP_PORT, subject, message, FROM_USER, SMTP_AUTH_USER, SMTP_AUTH_PWD, CONTENT_TYPE_HTML);
    }

    public static void sendMail(String[] recipients,
                                String host,
                                String subject,
                                String message,
                                String from) throws MessagingException
    {
        sendMail(recipients, host, SMTP_PORT, subject, message, from, SMTP_AUTH_USER, SMTP_AUTH_PWD, CONTENT_TYPE_HTML);
    }

    public static void sendMail(String host,
                                String port,
                                String subject,
                                String message,
                                String from,
                                String[] recipients) throws MessagingException
    {
        sendMail(recipients, host, port, subject, message, from, SMTP_AUTH_USER, SMTP_AUTH_PWD, CONTENT_TYPE_HTML);
    }

    public static void sendMail(String[] recipients,
                                String host,
                                String subject,
                                String message,
                                String from,
                                String contentType) throws MessagingException
    {
        sendMail(recipients, host, SMTP_PORT, subject, message, from, SMTP_AUTH_USER, SMTP_AUTH_PWD, CONTENT_TYPE_HTML);
    }

    public static void sendMail(String[] recipients,
                                String host,
                                String subject,
                                String message,
                                String from,
                                String smtpUser,
                                String smtpPass) throws MessagingException
    {
        sendMail(recipients, host, SMTP_PORT, subject, message, from, smtpUser, smtpPass, CONTENT_TYPE_HTML);
    }
    
    public static boolean sendNotification(String to, String subject, String text, boolean html){
        return sendNotification(to, subject, text, null, html);
    }
    
    public static boolean sendNotification(String to, String subject, String text, File attach, boolean html){
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.debug", "true"); 
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.EnableSSL.enable", "true");
        
        Session session = null;
        if (SMTP_AUTH_USER != null && SMTP_AUTH_PWD != null){
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new SMTPAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD);
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props);
        }

        try {
            
            // add handlers for main MIME types
            MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);            
            
            MimeMessage msg = new MimeMessage(session);
            msg.setSentDate(Calendar.getInstance().getTime());
            msg.setFrom(new InternetAddress(FROM_USER));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject);

            if (html || attach != null){
                // Create the Multipart to be added the parts to
                MimeMultipart mp = new MimeMultipart("alternative");
                MimeBodyPart mbp = new MimeBodyPart();
                if (html){
                    mbp.setContent(text, "text/html");
                } else {
                    mbp.setContent(text, "text/plain");
                }
                mp.addBodyPart(mbp);
    
                if (attach != null){
                    MimeBodyPart mbpf = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(attach);
                    mbpf.setDataHandler(new DataHandler(fds));
                    mbpf.setFileName(fds.getName());
                    mp.addBodyPart(mbpf);
                }
                msg.setContent(mp);
            } else {
                msg.setText(text);
            }

            //Transport trans = session.getTransport();
            //trans.connect(SMTP_HOST_NAME, 25, SMTP_AUTH_USER, SMTP_AUTH_PWD);
            msg.saveChanges();
            Transport.send(msg);
            //trans.sendMessage(msg, msg.getAllRecipients());
            //trans.close();
        } catch (Throwable t){
            logger.error("Could not send email notification.", t);
            return false;
        }

        return true;
    }

    public static boolean sendMail(String to, String from, String cc, String bcc, String subject, String text, List<File> files){
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.transport.protocol", "smtp");
        Session session = null;
        if (SMTP_AUTH_USER != null && SMTP_AUTH_PWD != null){
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new SMTPAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD);
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props);
        }

        try {
            
            // add handlers for main MIME types
            MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);            
            
            MimeMessage msg = new MimeMessage(session);
            msg.setSentDate(Calendar.getInstance().getTime());
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            if (cc != null){
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
            }
            msg.setSubject(subject);

            // Create the Multipart to add the parts to
            MimeMultipart mmp = new MimeMultipart();
            MimeBodyPart messagePart = new MimeBodyPart();
            messagePart.setText(text);
//            messagePart.setText(text, "utf-8", "text/html");
            mmp.addBodyPart(messagePart);

            if (files != null && files.size() > 0){
                for (File f : files){
                    logger.info("Attaching file: "+f.getAbsolutePath());
                    MimeBodyPart attachPart = new MimeBodyPart();
                    FileDataSource fds = new FileDataSource(f);
                    attachPart.setDataHandler(new DataHandler(fds));
                    attachPart.setFileName(fds.getName());
                    mmp.addBodyPart(attachPart);
                }
            }
            msg.setContent(mmp);

            //Transport trans = session.getTransport();
            //trans.connect(SMTP_HOST_NAME, 25, SMTP_AUTH_USER, SMTP_AUTH_PWD);
            msg.saveChanges();
            Transport.send(msg);
            //trans.sendMessage(msg, msg.getAllRecipients());
            //trans.close();
        } catch (Throwable t){
            logger.error("Could not send email notification.", t);
            return false;
        }
        
        // remove the pdf files
        for (File f : files){
            logger.info("Deleting attachment: "+f.getAbsolutePath());
            f.delete();
        }

        return true;
    }


    public static void sendMail(String[] recipients,
                                String host,
                                String port,
                                String subject,
                                String message,
                                String from,
                                String smtpUser,
                                String smtpPass,
                                String contentType)  throws MessagingException
    {
        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = null;
        if (smtpUser != null && smtpPass != null){
            props.put("mail.smtp.auth", "true");
            Authenticator auth = new SMTPAuthenticator(smtpUser, smtpPass);
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props);
        }

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setSentDate(Calendar.getInstance().getTime());
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);

        if (contentType != null) msg.setContent(message, contentType);
        else msg.setContent(message, CONTENT_TYPE_HTML);

        Transport.send(msg);
    }

    private void parseArgs(String[] args) {
        int p = 0;
        while (p < args.length) {
            String arg = args[p++];
            if ("-host".equals(arg))
                hostArg = args[p++];
            else if ("-port".equals(arg))
                portArg = args[p++];
            else if ("-authUser".equals(arg))
                authUserArg = args[p++];
            else if ("-password".equals(arg))
                passwordArg = args[p++];
            else if ("-fromUser".equals(arg))
                fromUserArg = args[p++];
            else if ("-subject".equals(arg))
                subjectArg = args[p++];
            else if ("-recipients".equals(arg))
                recipientsArg = args[p++].split(",");
            else if ("-message".equals(arg))
                messageArg = args[p++];
        }
    }


    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
    private static class SMTPAuthenticator extends javax.mail.Authenticator
    {
        private String smtpUser;
        private String smtpPass;

        public SMTPAuthenticator(String smtpUser, String smtpPass){
            this.smtpUser = smtpUser;
            this.smtpPass = smtpPass;
        }

        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(smtpUser, smtpPass);
        }
    }

    public static void main(String[] args) throws Exception {
        Emailer em = new Emailer();
        em.parseArgs(args);
        em.sendMail(em.recipientsArg, em.hostArg, SMTP_PORT, em.subjectArg, em.messageArg,
                    em.fromUserArg, em.authUserArg, em.passwordArg, CONTENT_TYPE_PLAIN);
    }

}                                    // Emailer