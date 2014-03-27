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
import com.jcabi.github.Gist;
import com.jcabi.github.GistComments;
import com.jcabi.github.Github;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.List;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.xembly.Directives;

/**
 * Mock Github gist.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "gist" })
@SuppressWarnings("PMD.TooManyMethods")
final class MkGist implements Gist {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Gist name.
     */
    private final transient String gist;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param name Gist name
     * @checkstyle ParameterNumber (5 lines)
     */
    MkGist(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "name can't be NULL") final String name
    ) {
        this.storage = stg;
        this.self = login;
        this.gist = name;
    }

    @Override
    @NotNull(message = "github is never NULL")
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    @NotNull(message = "itentifier is never NULL")
    public String identifier() {
        return this.gist;
    }

    @Override
    @NotNull(message = "content of file is never NULL")
    public String read(
        @NotNull(message = "file can't be NULL") final String file
    ) throws IOException {
        final List<XML> files = this.storage.xml().nodes(
            String.format(
                "%s/files/file[filename='%s']",
                this.xpath(), file
            )
        );
        if (files.isEmpty()) {
            throw new IOException(
                String.format("Couldn't find file with the name %s.", file)
            );
        }
        final List<String> contents = files.get(0)
            .xpath("raw_content/text()");
        String content = "";
        if (!contents.isEmpty()) {
            content = contents.get(0);
        }
        return content;
    }

    @Override
    public void write(
        @NotNull(message = "file should not be NULL") final String file,
        @NotNull(message = "content should nto be NULL") final String content
    )
        throws IOException {
        this.storage.apply(
            // @checkstyle MultipleStringLiterals (3 lines)
            new Directives().xpath(this.xpath()).xpath(
                String.format("files[not(file[filename='%s'])]", file)
            ).add("file").add("filename").set(file).up().add("raw_content")
        );
        this.storage.apply(
            new Directives().xpath(this.xpath()).xpath(
                String.format(
                    "files/file[filename='%s']/raw_content",
                    file
                )
            ).set(content)
        );
    }

    /**
     * Stars.
     * @throws IOException If there is any I/O problem
     * @checkstyle MultipleStringLiterals (10 lines)
     */
    @Override
    public void star() throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath())
                .attr("starred", Boolean.toString(true))
        );
    }

    /**
     * Unstars.
     * @throws IOException If there is any I/O problem
     * @checkstyle MultipleStringLiterals (10 lines)
     */
    @Override
    public void unstar() throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath())
                .attr("starred", Boolean.toString(false))
        );
    }

    /**
     * Checks if starred.
     * @return True if gist is starred
     * @throws IOException If there is any I/O problem
     */
    @Override
    public boolean starred() throws IOException {
        final List<String> xpath = this.storage.xml().xpath(
            String.format("%s/@starred", this.xpath())
        );
        return !xpath.isEmpty() && StringUtils.equalsIgnoreCase(
            Boolean.toString(true),
            xpath.get(0)
        );
    }

    @Override
    @NotNull(message = "Gist is never NULL")
    public Gist fork() throws IOException {
        this.storage.lock();
        final String number;
        try {
            final XML xml = this.storage.xml();
            number = Integer.toString(
                1 + xml.xpath("/github/gists/gist/id/text()").size()
            );
            final Directives dirs = new Directives().xpath("/github/gists")
                .add("gist")
                .add("id").set(number).up()
                .add("files");
            final List<XML> files = xml.nodes(
                String.format("%s/files/file", this.xpath())
            );
            for (final XML file : files) {
                final String filename = file.xpath("filename/text()").get(0);
                // @checkstyle MultipleStringLiterals (3 lines)
                dirs.add("file")
                    .add("filename").set(filename).up()
                    .add("raw_content").set(this.read(filename)).up().up();
            }
            this.storage.apply(dirs);
        } finally {
            this.storage.unlock();
        }
        return new MkGist(this.storage, this.self, number);
    }

    @Override
    @NotNull(message = "comments is never NULL")
    public GistComments comments() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    public void patch(
        @NotNull(message = "json can't be NULL") final JsonObject json
    ) throws IOException {
        new JsonPatch(this.storage).patch(this.xpath(), json);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/gists/gist[id='%s']",
            this.gist
        );
    }

}
