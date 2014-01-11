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

package com.google.appengine.tck.mail.support;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tck.temp.AbstractTempData;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Save the state of a MimeMessage that was received on MailHandlerServlet so it can be
 * verified in the test.
 * <p/>
 * * @author terryok@google.com
 */
public class MimeProperties extends AbstractTempData implements Serializable {

    Logger log = Logger.getLogger(MimeProperties.class.getName());

    public static final String BLANK = "[Blank]";

    public MimeProperties() {
        super();
    }

    public MimeProperties(MimeMessage mime) {
        this();
        initWithMimeMessage(mime);
    }

    private void initWithMimeMessage(MimeMessage mime) {

        try {
            if (mime.getSubject() != null) {
                subject = mime.getSubject();
            }

            if (mime.getFrom() != null) {
                from = mime.getFrom()[0].toString();
            }

            Address[] addresses;
            addresses = mime.getRecipients(Message.RecipientType.TO);
            if (addresses != null) {
                to = addresses[0].toString();
            }

            addresses = mime.getRecipients(Message.RecipientType.CC);
            if (addresses != null) {
                cc = addresses[0].toString();
            }

            addresses = mime.getRecipients(Message.RecipientType.BCC);
            if (addresses != null) {
                bcc = addresses[0].toString();
            }

            addresses = mime.getReplyTo();
            if (addresses != null) {
                replyTo = addresses[0].toString();
            }

            if (mime.getContent() instanceof String) {
                body = mime.getContent().toString().trim();
                log.info("ContentTypeString: " + mime.getContentType());
                log.info("ContentString: " + mime.getContent().toString().trim());
            }

            if (mime.getContent() instanceof Multipart) {
                Multipart multipart = (Multipart) mime.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    log.info("ContentTypeMultiPart: " + bodyPart.getContentType());
                    log.info("Content: " + bodyPart.getContent().toString());
                    multiPartsList.add(new String(bodyPart.getContent().toString()));
                }
            }

        } catch (MessagingException me) {
            throw new IllegalStateException(me);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public String subject = BLANK;  // use as key
    public String to = BLANK;
    public String from = BLANK;
    public String cc = BLANK;
    public String bcc = BLANK;
    public String replyTo = BLANK;
    public String headers = BLANK;
    public String body = BLANK;
    public List<String> multiPartsList = new ArrayList<>();

    @Override
    protected void toProperties(DatastoreService ds, Map<String, Object> map) {
        map.put("subject", subject);
        map.put("sender", from);
        map.put("to", to);
        map.put("from", from);
        map.put("cc", cc);
        map.put("bcc", bcc);
        map.put("replyTo", replyTo);
        map.put("body", body);
        map.put("multiPartsList", multiPartsList);
        Text headersText = new Text(headers.toString());
        map.put("headers", headersText);
    }

    @Override
    protected void fromPropertiesInternal(Map<String, Object> properties) {
        subject = (String) properties.get("subject");
        from = (String) properties.get("from");
        to = (String) properties.get("to");
        cc = (String) properties.get("cc");
        bcc = (String) properties.get("bcc");
        replyTo = (String) properties.get("replyTo");
        body = (String) properties.get("body");
        multiPartsList = (List<String>) properties.get("multiPartsList");
        Text headersText = (Text) properties.get("headers");
        headers = headersText.getValue();
    }

    @Override
    public String toString() {
        return "MimeProperties{" +
            "subject='" + subject + '\'' +
            ", to='" + to + '\'' +
            ", from='" + from + '\'' +
            ", cc='" + cc + '\'' +
            ", bcc='" + bcc + '\'' +
            ", replyTo='" + replyTo + '\'' +
            ", headers='" + headers + '\'' +
            ", body='" + body + '\'' +
            ", multiPartsList=" + multiPartsList +
            '}';
    }

}