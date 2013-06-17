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

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

/**
 * TODO: http://go/java-style#javadoc
 */
@RunWith(Arquillian.class)
public class ChannelTest extends ChannelTestBase {

    private WebClient client;

    @Before
    public void setUp() {
        client = new WebClient(BrowserVersion.FIREFOX_17);
//        client.setThrowExceptionOnScriptError(false);
//        client.setAjaxController(new NicelyResynchronizingAjaxController());

//        client.setAjaxController(new AjaxController(){
//            @Override
//            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
//            {
//                return true;
//            }
//        });
    }

    @After
    public void tearDown() throws Exception {
    }

    private boolean isLastMessageInitialized(HtmlPage page) {
        HtmlElement lastMsg = page.getHtmlElementById("last-sent-message");
        String echoMsg = lastMsg.asText().trim();
        return !echoMsg.equals("never-set");
    }

    @Test
    @RunAsClient
    @InSequence(1)
    public void testSimpleMessage(@ArquillianResource URL url) throws Exception {
        //String channelId = "id-" + System.currentTimeMillis();
        String channelId = "1234";
        HtmlPage page = (HtmlPage)client.getPage(url + "channelPage.jsp?test-channel-id=" + channelId);
        client.waitForBackgroundJavaScript(5000);
        HtmlElement channel = page.getHtmlElementById("channel-id");
        Assert.assertEquals(channelId, channel.asText());
        sync();
        HtmlElement doit =

        HtmlElement status = page.getHtmlElementById("status");
        Assert.assertEquals("oppened", status.asText());

        HtmlElement sendButton = page.getHtmlElementById("send-message-button");
        HtmlPage p = sendButton.click();

//        HtmlPage pageAfterSentMsg = (HtmlPage) sr.getNewPage();
        HtmlPage pageAfterSentMsg = p;

        HtmlElement lastMsg = pageAfterSentMsg.getHtmlElementById("last-sent-message");
        String echoMsg = lastMsg.asText().trim();
        Assert.assertTrue("Echo Message should be set", !echoMsg.equals("never-set"));

        client.waitForBackgroundJavaScript(5000);
        sync(5000);

        HtmlElement lastReceived = pageAfterSentMsg.getHtmlElementById("last-received-message");
        String receivedMsg = lastReceived.asText().trim();
        Assert.assertEquals(echoMsg, receivedMsg);

    }
}
