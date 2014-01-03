/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.jcabi.github.Github;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;
import javax.json.JsonObject;
import java.io.IOException;

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
@EqualsAndHashCode(of = { "storage", "self", "name" })
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
    private final transient String name;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param gist Gist name
     * @checkstyle ParameterNumber (5 lines)
     */
    MkGist(final MkStorage stg, final String login,
        final String gist) {
        this.storage = stg;
        this.self = login;
        this.name = gist;
    }

    @Override
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    public String read(final String file) throws IOException {
        return this.storage.xml().xpath(
            String.format(
                "%s/files/file[filename='%s']/raw_content/text()",
                this.xpath(), file
            )
        ).get(0);
    }

    @Override
    public void write(final String file, final String content)
        throws IOException {
        this.storage.apply(
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
     * @todo #19:30min Have no idea in mkstorage format
     * @throws IOException If there is any I/O problem
     */
    @Override
    public void star() throws IOException {
    }

    /**
     * Checks if starred.
     * @todo #19:30min Have no idea in mkstorage format
     * @return True if gist is starred
     * @throws IOException If there is any I/O problem
     */
    @Override
    public boolean starred() throws IOException {
        return false;
    }

    @Override
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/gists/gist[id='%s']",
            this.name
        );
    }

}
