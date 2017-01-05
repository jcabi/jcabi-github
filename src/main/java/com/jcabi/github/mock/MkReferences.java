/**
 * Copyright (c) 2013-2017, jcabi.com
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
import com.jcabi.github.Reference;
import com.jcabi.github.References;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Mock of Github References.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkReferences implements References {

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
     * Public constructor.
     * @param stg Storage.
     * @param login Login name.
     * @param rep Repo coordinates.
     * @throws IOException - If something goes wrong.
     */
    MkReferences(
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
            ).addIf("refs")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Reference create(
        final String ref,
        final String sha
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("reference")
                .add("ref").set(ref).up()
                .add("sha").set(sha).up()
        );
        return this.get(ref);
    }

    @Override
    public Reference get(
        final String identifier
    ) {
        return new MkReference(
            this.storage, this.self, this.coords, identifier
        );
    }

    @Override
    public Iterable<Reference> iterate() {
        return new MkIterable<Reference>(
            this.storage,
            String.format("%s/reference", this.xpath()),
            new MkIterable.Mapping<Reference>() {
                @Override
                public Reference map(final XML xml) {
                    return MkReferences.this.get(
                        xml.xpath("ref/text()").get(0)
                    );
                }
            }
        );
    }

    @Override
    public Iterable<Reference> iterate(
        final String subnamespace
    ) {
        return new MkIterable<Reference>(
            this.storage,
            String.format(
                "%s/reference/ref[starts-with(., 'refs/%s')]", this.xpath(),
                subnamespace
            ),
            new MkIterable.Mapping<Reference>() {
                @Override
                public Reference map(final XML xml) {
                    return MkReferences.this.get(
                        xml.xpath("text()").get(0)
                    );
                }
            }
        );
    }

    @Override
    public Iterable<Reference> tags() {
        return this.iterate("tags");
    }

    @Override
    public Iterable<Reference> heads() {
        return this.iterate("heads");
    }

    @Override
    public void remove(
        final String identifier
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "%s/reference[ref='%s']", this.xpath(), identifier
                )
            ).remove()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/git/refs",
            this.coords
        );
    }

}
