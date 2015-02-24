package com.gmail.mariska.martin.mtginventory.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;

/**
 * Q: What are some of the most common mistakes people make when using JavaMail?
 * A: Unfortunately, the internet is full of copy and paste programmers who don't understand the code they're using,
 * which has resulted in a lot of unnecessarily complex and often incorrect examples. The most common mistakes are:
 * 
 * Use of Session.getDefaultInstance. Almost all code should use Session.getInstance instead, as described below
 * Caling the send method on a Transport instance variable. As described below, send is a static method and ignores the
 * Transport instance you use to call it.
 * Setting various socketFactory properties. Long, long ago JavaMail didn't have built in support for SSL connections,
 * so it was necessary to set these properties to use SSL. This hasn't been the case for years; remove these properties
 * and simplify your code. The easiest way to enable SSL support in current versions of JavaMail is to set the property
 * "mail.smtp.ssl.enable" to "true". (Replace "smtp" with "imap" or "pop3" as appropriate.)
 * Using an Authenticator just to supply a username and password. There's really nothing wrong with using an
 * Authenticator, it's just unnecessarily complex. A more straightforward approach is to call the connect method that
 * takes a username and password. This is easy when using a Store, but when using a Transport you need to switch from
 * using the static send method to using a Transport instance with the sendMessage method, as described below
 * 
 * 
 * @author MAR
 * 
 */
public class EmailService {
    private final Logger logger = Logger.getLogger(EmailService.class);
    private final String MTG_INVENTORY_NAME = "Mtg Inventory";
    private final String username = "mymtginventory@gmail.com";
    private final String password = "inventorymanie";
    private Session session;

    public EmailService() {
        // Using TLS authentification
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.debug", logger.isTraceEnabled() ? "true" : "false");

        session = Session.getInstance(props);
    }

    @Subscribe
    public void sendEmailMessage(EmailMessage emailMsg) {
        if (logger.isDebugEnabled()) {
            logger.debug("sending email to: " + emailMsg.getRecipient() + " subject: " + emailMsg.getSubject());
        }
        if (logger.isTraceEnabled()) {
            logger.trace("email content: " + emailMsg.getContent());
        }
        this.sendEmail(emailMsg);
    }

    public void sendEmail(EmailMessage emailMsg) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, MTG_INVENTORY_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMsg.getRecipient()));
            message.setSubject(emailMsg.getSubject(), "UTF-8");
            message.setSentDate(new Date());
            message.setContent(emailMsg.getContent(), "text/html; charset=UTF-8");
// message.setText("Lorem Ipsum - TEST");

            Transport t = session.getTransport("smtp");
            try {
                t.connect(username, password);
                t.sendMessage(message, message.getAllRecipients());
            } finally {
                t.close();
            }

            logger.trace("message send: " + message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Slouzi interne pro odesilani emailu pomoci email service
     * 
     * @author MAR
     */
    public static class EmailMessage {
        private final String recipient;
        private final String subject;
        private final String content;

        public static class Builder {
            private String recipient;
            private String subject;
            private String content;

            public Builder setRecipient(String recipient) {
                this.recipient = recipient;
                return this;
            }

            public Builder setSubject(String subject) {
                this.subject = subject;
                return this;
            }

            public Builder setContent(String content) {
                this.content = content;
                return this;
            }

            public Builder setRecMar() {
                recipient = "mariska.martin@gmail.com";
                return this;
            }

            public Builder testMsg() {
                setRecMar();
                subject = "Testovací email";
                content = "<h3>Test</h3>"
                        + "<p>Tento email je automaticky generovaný. Neodpovídejte na něj.</p>";
                return this;
            }

            public EmailMessage build() {
                java.util.Objects.requireNonNull(recipient, "neni vyplnen prijemce");
                java.util.Objects.requireNonNull(subject, "neni vyplnen predmet");
                java.util.Objects.requireNonNull(content, "neni zadny obsah zpravy");
                return new EmailMessage(recipient, subject, content);
            }
        }

        private EmailMessage(String recipient, String subject, String content) {
            super();
            this.recipient = recipient;
            this.subject = subject;
            this.content = content;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getContent() {
            return content;
        }

        public String getSubject() {
            return subject;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("recipient", recipient)
                    .add("subj", subject)
                    .add("content", content)
                    .toString();
        }
    }

    /**
     * For test only
     * 
     * @param args
     */
    public static void main(String[] args) {
        new EmailService().sendEmail(new EmailMessage.Builder().testMsg().build());
    }
}
