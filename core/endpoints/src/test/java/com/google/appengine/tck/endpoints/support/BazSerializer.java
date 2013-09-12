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

package com.google.appengine.tck.endpoints.support;

import com.google.api.server.spi.config.Serializer;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 * @author <a href="mailto:terryok@google.com">Ales Justin</a>
 */
public class BazSerializer implements Serializer<Baz, StringWrapper> {

    public static final String BZZZ = "Bzzz_";

    @Override
    public StringWrapper serialize(Baz in) {
        return new StringWrapper(BZZZ + in.getS());
    }

    @Override
    public Baz deserialize(StringWrapper wrapper) {
        Baz baz = new Baz();
        String original = wrapper.getStr().substring(BZZZ.length());
        baz.setS(original);
        return baz;
    }
}