/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XML;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.w3c.dom.Node;

/**
 * Json node in XML.
 *
 * @since 0.5
 *
 */
final class JsonNode {

    /**
     * XML.
     */
    private final transient XML xml;

    /**
     * Public ctor.
     * @param src Source
     */
    JsonNode(final XML src) {
        this.xml = src;
    }

    /**
     * Fetch JSON object.
     * @return JSON
     * @checkstyle MultipleStringLiteralsCheck (30 lines)
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public JsonObject json() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        for (final XML child : this.xml.nodes("* ")) {
            final Node node = child.node();
            if (child.nodes("*").isEmpty()) {
                builder.add(node.getNodeName(), node.getTextContent());
            } else if (
                !child.xpath("//@array").isEmpty()
                    && "true".equals(child.xpath("//@array").get(0))
            ) {
                final JsonArrayBuilder bld = Json.createArrayBuilder();
                for (final XML item : child.nodes("*")) {
                    bld.add(item.node().getTextContent());
                }
                builder.add(node.getNodeName(), bld.build());
            } else {
                builder.add(node.getNodeName(), new JsonNode(child).json());
            }
        }
        return builder.build();
    }
}
