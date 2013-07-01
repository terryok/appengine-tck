/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.appengine.tck.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * End-to-end test for Mail.
 *
 * Tests sending via {@link MailService#send} and {@link javax.mail.Transport#send}, and, for a
 * deployed application, receiving via a POST.
 *
 * @author terryok@google.com
 */
@RunWith(Arquillian.class)
public class MailServiceTest extends MailTestBase {

    private static final String BODY =
        "from GAE TCK Mail Test.\n";

    private static final int TIMEOUT_IN_SECONDS = 30;

    private MailService mailService;
    private DatastoreService datastoreService;

    private String user;
    @Before
    public void setUp() {
        mailService = MailServiceFactory.getMailService();
        datastoreService = DatastoreServiceFactory.getDatastoreService();
        user = System.getProperty("appengine.userId");
    }

    @Test
    public void testSendAndReceiveBasicMessage() throws Exception {
        log.info("email:" + getTestEmail());
        MailService.Message msg = new MailService.Message();
        msg.setSender(getFrom());
        msg.setTo(getTo());
        String subjectTag = "Basic-Message-Test-" + System.currentTimeMillis();
        msg.setSubject(subjectTag);
        msg.setTextBody(BODY);
        mailService.send(msg);

        assertMessageReceived(subjectTag);
        deleteTestMailEntity(subjectTag);
    }

    @Test
    public void testJavaxTransportSendAndReceiveBasicMessage() throws Exception {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(getFrom()));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(getTo()));
        String subjectTag = "Javax-Transport-Test-" + System.currentTimeMillis();
        msg.setSubject(subjectTag);

        msg.setText(BODY);
        Transport.send(msg);

        assertMessageReceived(subjectTag);
        deleteTestMailEntity(subjectTag);
    }

    @Test
    public void testSendToAdmin() throws Exception {
        MailService.Message msg = new MailService.Message();
        msg.setSender(getFrom());
        String subjectTag = "Send-to-admin-" + System.currentTimeMillis();
        msg.setSubject(subjectTag);
        msg.setTextBody(BODY);
        mailService.sendToAdmins(msg);

        // Assuming success if no exception was thrown without calling msg.setTo()
    }

    private void assertMessageReceived(String subjectTag) {
        if (onAppServer()) {
            Entity mail = pollForMailWithTimeout(subjectTag, TIMEOUT_IN_SECONDS);
            if (mail == null) {
                fail("gave up after " + TIMEOUT_IN_SECONDS + " seconds");
            }
            assertEquals(subjectTag, mail.getProperty("subject"));
        }
    }

    private void deleteTestMailEntity(String subjectTag) {
        Key key = KeyFactory.createKey("Mail", subjectTag);
        datastoreService.delete(key);
    }

    private String getTo() {
        int i = getTestEmail().indexOf('@');
        String userName = getTestEmail().substring(0,i);

        return userName + "@" + appId() + "."  + "appspotmail.com";
    }

    private String getFrom() {
        return getTestEmail();
//        return "toshaokanter@gmail.com";
    }

    private String appId() {
        return SystemProperty.applicationId.get();
    }

    private String mailGateway() {
//        return System.getProperty("mail.gateway");
        return System.getProperty("appengine.mail.gateway");
    }

    private String getTestEmail() {
        return "toshaokanter@gmail.com";
//        return user;
//        return System.getProperty("appengine.userId");
    }

    private boolean onAppServer() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
    }

    private Entity pollForMailWithTimeout(String subjectTag, int timeoutInSeconds) {
        Entity mail = null;
        Key key = KeyFactory.createKey("Mail", subjectTag);
        while (timeoutInSeconds-- > 0) {
            try {
                mail = datastoreService.get(key);
                break;
            } catch (EntityNotFoundException e1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
        return mail;
    }
}
