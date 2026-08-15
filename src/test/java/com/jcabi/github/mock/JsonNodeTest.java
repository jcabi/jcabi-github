/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XMLDocument;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link JsonNode}.
 * @since 0.5
 */
final class JsonNodeTest {

    @Test
    void convertsXmlToJson() {
        MatcherAssert.assertThat(
            "Plain element is not converted",
            JsonNodeTest.user().getString("name"),
            Matchers.equalTo("Jeff")
        );
    }

    @Test
    void convertsNestedXmlToJson() {
        MatcherAssert.assertThat(
            "Nested element is not converted",
            JsonNodeTest.user().getJsonObject("dept").getString("title"),
            Matchers.equalTo("IT")
        );
    }

    @Test
    void convertsXmlToJsonArray() {
        MatcherAssert.assertThat(
            "Assertion failed",
            new JsonNode(
                new XMLDocument(
                    "<users array=\"true\"><item>Jeff</item><item>Bauer</item><item>Iko</item></users>"
                )
            ).json().toString(),
            new IsEqual<>("{\"users\":[\"Jeff\",\"Bauer\",\"Iko\"]}")
        );
    }

    /**
     * A user with a name and a department.
     * @return JSON of the user
     */
    private static JsonObject user() {
        return new JsonNode(
            new XMLDocument(
                "<user><name>Jeff</name><dept><title>IT</title></dept></user>"
            ).nodes("user").get(0)
        ).json();
    }
}
