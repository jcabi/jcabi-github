/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link SmartJsonTest}.
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
        return () -> Json.createReader(
            IOUtils.toInputStream(txt, StandardCharsets.UTF_8)
        ).readObject();
    }

}
