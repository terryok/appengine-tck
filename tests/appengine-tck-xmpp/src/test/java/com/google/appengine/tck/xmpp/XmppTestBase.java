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

package com.google.appengine.tck.xmpp;

import com.google.appengine.tck.base.TestBase;
import com.google.appengine.tck.base.TestContext;
import com.google.appengine.tck.xmpp.support.XmppMessageServlet;
import org.jboss.shrinkwrap.api.spec.WebArchive;


/**
 * @author <a href="mailto:terryok@google.com">Terry Okamoto</a>
 */
public abstract class XmppTestBase extends TestBase {
    protected static WebArchive getDefaultDeployment() {
        TestContext context = new TestContext().setUseSystemProperties(true).setCompatibilityProperties(TCK_PROPERTIES);

        context.setWebXmlFile("xmpp-web.xml");
        context.setAppEngineWebXmlFile("xmpp-appengine-web.xml");

        WebArchive war = getTckDeployment(context);
        war.addClasses(XmppTestBase.class, XmppMessageServlet.class);
        return war;
    }
}
