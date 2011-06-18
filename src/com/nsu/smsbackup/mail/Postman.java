package com.nsu.smsbackup.mail;

import android.util.Log;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Properties;

/**
 * @author andy
 */
public class Postman extends Authenticator {

    private static final String TAG = "com.nsu.smsbackup.mail.Postman";

    private final String account;
    private final String password;

    private final Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    /**
     * @param account  e-mail account to send messages from
     * @param password password for this account
     */
    public Postman(String account, String password) {
        Log.d(TAG, "Initialized: account = \"" + account + "\", password = \"" + password + "\"");
        this.account = account;
        this.password = password;

        // set up SMTP
        final String MAIL_HOST = "smtp.gmail.com";

        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", MAIL_HOST);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        // get a session
        session = Session.getDefaultInstance(props, this);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(account, password);
    }

    public synchronized void send(String subject, String body, String to) {
        Log.d(TAG, String.format("Sending: subject = \"%s\", body=\"%s\", to=\"%s\"", subject, body, to));

        final MimeMessage message = new MimeMessage(session);
        final DataHandler handler = new DataHandler(new StringDataSource(body));
        try {
            message.setSender(new InternetAddress(account));
            message.setSubject(subject);
            message.setDataHandler(handler);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            Transport.send(message);
        } catch (MessagingException e) {
            Log.e(TAG, "Sending failed", e);
        }
    }

    /**
     * Sends {@code data} marked with {@code id} to the specified account (from the same account).
     */
    public void backup(String id, String data) {
        send(id, data, account);
    }

}
