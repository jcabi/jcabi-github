/**
 * Copyright (c) 2013-2020, jcabi.com
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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.Tag;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Mock of Github Tag.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords", "sha" })
final class MkTag implements Tag {

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
     * The Tag's sha.
     */
    private final transient String sha;

    /**
     * Public constructor.
     * @param strg The storage.
     * @param login The login name
     * @param crds Credential
     * @param identifier Tag's sha.
     * @checkstyle ParameterNumber (5 lines)
     */
    MkTag(
        final MkStorage strg,
        final String login,
        final Coordinates crds,
        final String identifier
    ) {
        this.storage = strg;
        this.self = login;
        this.coords = crds;
        this.sha = new StringBuilder().append('"').append(identifier)
            .append('"').toString();
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
    public String key() {
        return this.sha;
    }

    /**
     * XPath of this element in XML tree.
     *
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords = '%s']/git/tags/tag[sha = '%s']",
            this.coords, this.sha
        );
    }

}
