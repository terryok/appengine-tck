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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Handle incoming mail
 */
public class MailHandlerServlet extends HttpServlet {

    Logger log = Logger.getLogger(MailHandlerServlet.class.getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Entity mail = null;
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);
        try {
            MimeMessage message = new MimeMessage(session, req.getInputStream());
            log.info("Received msg via " + req.getPathInfo());
            String subjectKey = message.getSubject();
            mail = new Entity("Mail", subjectKey);

            mail.setProperty("path_info", req.getPathInfo());
            mail.setProperty("from", message.getFrom()[0].toString());
            mail.setProperty("subject", message.getSubject());

        } catch (MessagingException me) {
            mail.setProperty("error", me.getMessage());
        } finally {
            datastoreService.put(mail);
        }
    }
}
