/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link SmartJsonTest}.
 * @since 0.5
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class SmartJsonTest {

    @Test
    public void fetchesStringFromJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new SmartJson(
                SmartJsonTest.json("{\"first\": \"a\"}")
            ).text("first"),
            Matchers.equalTo("a")
        );
    }

    @Test
    public void fetchesNumberFromJson() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new SmartJson(
                SmartJsonTest.json("{\"second\": 1}")
            ).number("second"),
            Matchers.equalTo(1)
        );
    }

    @Test
    public void fetchesArrayFromJson() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            new SmartJson(
                SmartJsonTest.json("{\"arr\": [1, 2]}")
            ).value("arr", JsonArray.class),
            Matchers.hasSize(2)
        );
    }

    @Test
    public void fetchesObjectFromJson() throws IOException {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            new SmartJson(
                SmartJsonTest.json("{\"o\": {\"foo\": [1]}}")
            ).value("o", JsonObject.class).getJsonArray("foo"),
            Matchers.hasSize(1)
        );
    }

    @Test
    public void checksNotNullKeyNotPresent() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new SmartJson(
                SmartJsonTest.json("{\"first\": \"a\"}")
            ).hasNotNull("second"),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void checksNotNullKeyPresentAndNull() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new SmartJson(
                SmartJsonTest.json("{\"first\": null}")
            ).hasNotNull("first"),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void checksNotNullKeyPresentAndNotNull() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
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
