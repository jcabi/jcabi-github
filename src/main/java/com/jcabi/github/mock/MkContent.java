/**
 * Copyright (c) 2013-2014, JCabi.com
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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Content;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github content.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @todo #166 Content mock should be implemented.
 *  Need to implement the methods of MkContent: 1) compareTo,
 *  2) json, 3) patch
 *  Don't forget to update the unit test class {@link MkContent}.
 *  See http://developer.github.com/v3/repos/contents
 * @todo #314:30m MkContent should be able to return its own repository when
 *  the repo() method is invoked, and its own path when the path() method
 *  is invoked. Don't forget to implement unit tests.
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords", "location" })
final class MkContent implements Content {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Path of this content.
     */
    private final transient String location;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param path Path of this file
     * @throws IOException If there is any I/O problem
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    public MkContent(final MkStorage stg, final String login,
        final Coordinates rep, final String path) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.location = path;
    }

    @Override
    public int compareTo(final Content cont) {
        throw new UnsupportedOperationException("MkContent#compareTo()");
    }

    @Override
    public void patch(
        @NotNull(message = "JSON is never NULL") final JsonObject json)
        throws IOException {
        throw new UnsupportedOperationException("MkContent#patch()");
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public String path() {
        return this.location;
    }

    @Override
    public InputStream raw() throws IOException {
        return new ByteArrayInputStream(
            this.storage.xml().xpath(
                String.format("%s/content/text()", this.xpath())
            ).get(0).getBytes()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return The XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/contents/content[path='%s']",
            this.coords, this.location
        );
    }
}
