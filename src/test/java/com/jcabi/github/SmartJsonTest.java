/**
 * Copyright (c) 2013-2018, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link SmartJsonTest}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class SmartJsonTest {

    /**
     * SmartJson can fetch data from JSON.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesStringFromJson() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"first\": \"a\"}")
            ).text("first"),
            Matchers.equalTo("a")
        );
    }

    /**
     * SmartJson can fetch number from JSON.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesNumberFromJson() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"second\": 1}")
            ).number("second"),
            Matchers.equalTo(1)
        );
    }

    /**
     * SmartJson can fetch an array from JSON.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesArrayFromJson() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"arr\": [1, 2]}")
            ).value("arr", JsonArray.class),
            Matchers.hasSize(2)
        );
    }

    /**
     * SmartJson can fetch an object from JSON.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesObjectFromJson() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"o\": {\"foo\": [1]}}")
            ).value("o", JsonObject.class).getJsonArray("foo"),
            Matchers.hasSize(1)
        );
    }

    /**
     * SmartJson can check for not null keys.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksNotNullKeyNotPresent() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"first\": \"a\"}")
            ).hasNotNull("second"),
            Matchers.equalTo(false)
        );
    }

    /**
     * SmartJson can check for not null keys.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksNotNullKeyPresentAndNull() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"first\": null}")
            ).hasNotNull("first"),
            Matchers.equalTo(false)
        );
    }

    /**
     * SmartJson can check for not null keys.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksNotNullKeyPresentAndNotNull() throws Exception {
        MatcherAssert.assertThat(
            new SmartJson(
                SmartJsonTest.json("{\"first\": \"a\"}")
            ).hasNotNull("first"),
            Matchers.equalTo(true)
        );
    }

    /**
     * Make a readable with this JSON content.
     * @param txt JSON content
     * @return Readable
     */
    private static JsonReadable json(final String txt) {
        return new JsonReadable() {
            @Override
            public JsonObject json() {
                return Json.createReader(
                    IOUtils.toInputStream(txt, StandardCharsets.UTF_8)
                ).readObject();
            }
        };
    }

}
