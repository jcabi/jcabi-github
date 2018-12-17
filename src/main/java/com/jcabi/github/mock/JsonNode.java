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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
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
