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

package com.google.appengine.tck.channel;

import com.google.appengine.tck.base.TestBase;
import com.google.appengine.tck.base.TestContext;
import com.google.appengine.tck.channel.support.ChannelServlet;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;


/**
 * @author <a href="mailto:terryok@google.com">Terry Okamoto</a>
 */
public abstract class ChannelTestBase extends TestBase {

    /**
     * Every test package needs to declare a *TestBase class which
     * getDeployment() declares the resources needed to run the test.
     * Basically this is how you specify the WEB-INF files, and the
     * test classes themselves.
     * <p/>
     * Note that tools may indicate that getDeployment() is not used
     * since it is called dynamically.
     */
    @Deployment
    public static WebArchive getDeployment() {
        TestContext context = new TestContext();

        context.setAppEngineWebXmlFile("channel-ae-web.xml");

        WebArchive war = getTckDeployment(context);

        // Add all the test class files here.  Maven knows how to compile the
        // tests, declaring them here is for the arquillian framework.
        //
        war.addClass(TestBase.class);
        war.addClass(ChannelTestBase.class);
        war.addClasses(ChannelTest.class, ChannelServlet.class);

        war.addAsWebInfResource("channelPage.jsp");
        war.addAsWebResource("channelPage.jsp", "channelPage.jsp");

        war.addAsWebInfResource("channelEcho.jsp");
        war.addAsWebResource("channelEcho.jsp", "channelEcho.jsp");

        return war;
    }
}
