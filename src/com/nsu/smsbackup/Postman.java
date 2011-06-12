package com.nsu.smsbackup;

import android.util.Log;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

public class Postman extends Authenticator {

    private static final String TAG = "com.nsu.smsbackup.Postman";

    private final String user;
    private final String password;
    private final Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public Postman(String user, String password) {
        this.user = user;
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
        return new PasswordAuthentication(user, password);
    }

    public void send(String subject, String body, String to) {
        send(subject, body, user, to);
    }

    public synchronized void send(String subject, String body, String from, String to) {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
        try{
            message.setSender(new InternetAddress(from));
            message.setSubject(subject);
            message.setDataHandler(handler);

            if (to.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            Transport.send(message);
        } catch (MessagingException e) {
            Log.e(TAG, "Sending failed", e);
        }
    }

    private static class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
