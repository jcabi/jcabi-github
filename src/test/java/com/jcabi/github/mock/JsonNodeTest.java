/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Test case for {@link JsonNode}.
 * @since 0.5
 */
public final class JsonNodeTest {

    @Test
    public void convertsXmlToJson() {
        final XML xml = new XMLDocument(
            "<user><name>Jeff</name><dept><title>IT</title></dept></user>"
        );
        final JsonObject json = new JsonNode(xml.nodes("user").get(0)).json();
        MatcherAssert.assertThat(
            "Value is null",json, Matchers.notNullValue());
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getString("name"),
            Matchers.equalTo("Jeff")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getJsonObject("dept").getString("title"),
            Matchers.equalTo("IT")
        );
    }

    @Test
    public void convertsXmlToJsonArray() {
        final XML xml = new XMLDocument(
            // @checkstyle LineLength (1 line)
            "<users array=\"true\"><item>Jeff</item><item>Bauer</item><item>Iko</item></users>"
        );
        final JsonObject json = new JsonNode(xml).json();
        MatcherAssert.assertThat(
            "Assertion failed",
            json.toString(),
            new IsEqual<>("{\"users\":[\"Jeff\",\"Bauer\",\"Iko\"]}")
        );
    }

}
