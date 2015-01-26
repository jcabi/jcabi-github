/**
 * Copyright (c) 2013-2015, jcabi.com
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
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Mock Github content.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords", "location", "branch" })
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
     * Branch of this content.
     */
    private final transient String branch;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @param path Path of this file
     * @param ref Branch of this file
     * @throws IOException If there is any I/O problem
     * @checkstyle ParameterNumberCheck (6 lines)
     */
    public MkContent(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "rep can't be NULL") final Coordinates rep,
        @NotNull(message = "path can't be NULL") final String path,
        @NotNull(message = "ref can't be NULL") final String ref
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.location = path;
        this.branch = ref;
    }

    @Override
    public int compareTo(
        @NotNull(message = "cont should not be NULL") final Content cont
    ) {
        return new CompareToBuilder()
            .append(this.path(), cont.path())
                .append(this.repo().coordinates(), cont.repo().coordinates())
            .build();
    }

    @Override
    public void patch(
        @NotNull(message = "JSON is never NULL") final JsonObject json)
        throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    @NotNull(message = "repo is never NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    @NotNull(message = "path is never NULL")
    public String path() {
        return this.location;
    }

    @Override
    @NotNull(message = "input stream is never NULL")
    public InputStream raw() throws IOException {
        return new ByteArrayInputStream(
            DatatypeConverter.parseBase64Binary(
                this.storage.xml().xpath(
                    String.format("%s/content/text()", this.xpath())
                ).get(0)
            )
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return The XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            // @checkstyle LineLength (1 line)
            "/github/repos/repo[@coords='%s']/contents/content[path='%s' and @ref='%s']",
            this.coords, this.location, this.branch
        );
    }
}
