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
import com.jcabi.github.Tags;
import java.io.IOException;
import java.util.Map.Entry;
import javax.json.JsonObject;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of Github Tags.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkTags implements Tags {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo's name.
     */
    private final transient Coordinates coords;
    /**
     * Public constructor.
     * @param stg The storage.
     * @param login The login name.
     * @param rep Repo's coordinates.
     * @throws IOException If something goes wrong.
     */
    MkTags(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']/git",
                    this.coords
                )
            ).addIf("tags")
        );
    }
    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Tag create(
        final JsonObject params
    ) throws IOException {
        final Directives dirs = new Directives().xpath(this.xpath()).add("tag");
        for (final Entry<String, JsonValue> entry : params.entrySet()) {
            dirs.add(entry.getKey()).set(entry.getValue().toString()).up();
        }
        this.storage.apply(dirs);
        new MkReferences(this.storage, this.self, this.coords).create(
            new StringBuilder().append("refs/tags/").append(
                params.getString("name")
            ).toString(),
            params.getString("sha")
        );
        return this.get(params.getString("sha"));
    }

    @Override
    public Tag get(final String sha) {
        return new MkTag(this.storage, this.self, this.coords, sha);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/tags",
            this.coords
        );
    }

}
