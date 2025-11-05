/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Test case for {@link JsonNode}.
 */
public final class JsonNodeTest {

    /**
     * JsonNode can text XML to JSON.
     */
    @Test
    public void convertsXmlToJson() {
        final XML xml = new XMLDocument(
            "<user><name>Jeff</name><dept><title>IT</title></dept></user>"
        );
        final JsonObject json = new JsonNode(xml.nodes("user").get(0)).json();
        MatcherAssert.assertThat(json, Matchers.notNullValue());
        MatcherAssert.assertThat(
            json.getString("name"),
            Matchers.equalTo("Jeff")
        );
        MatcherAssert.assertThat(
            json.getJsonObject("dept").getString("title"),
            Matchers.equalTo("IT")
        );
    }

    /**
     * JsonNode can text XML to JSON array.
     */
    @Test
    public void convertsXmlToJsonArray() {
        final XML xml = new XMLDocument(
            // @checkstyle LineLength (1 line)
            "<users array=\"true\"><item>Jeff</item><item>Bauer</item><item>Iko</item></users>"
        );
        final JsonObject json = new JsonNode(xml).json();
        MatcherAssert.assertThat(
            json.toString(),
            new IsEqual<>("{\"users\":[\"Jeff\",\"Bauer\",\"Iko\"]}")
        );
    }

}
