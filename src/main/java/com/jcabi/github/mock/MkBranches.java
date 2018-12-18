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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Branch;
import com.jcabi.github.Branches;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Git branches.
 *
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
public final class MkBranches implements Branches {
    /**
     * XPath from a given branch to its commit SHA string.
     */
    private static final String XPATH_TO_SHA = "sha/text()";

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
     * Public ctor.
     * @param stg Storage
     * @param login Username
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    MkBranches(
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
                    "/github/repos/repo[@coords='%s']",
                    this.coords
                )
            ).addIf("branches")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Iterable<Branch> iterate() {
        return new MkIterable<Branch>(
            this.storage,
            String.format("%s/branch", this.xpath()),
            new MkIterable.Mapping<Branch>() {
                @Override
                public Branch map(final XML xml) {
                    return new MkBranch(
                        MkBranches.this.storage,
                        MkBranches.this.self,
                        MkBranches.this.coords,
                        xml.xpath("@name").get(0),
                        xml.xpath(MkBranches.XPATH_TO_SHA).get(0)
                    );
                }
            }
        );
    }

    @Override
    public Branch find(final String name) {
        throw new UnsupportedOperationException("find(name) not implemented");
    }

    /**
     * Creates a new branch.
     * @param name Name of branch
     * @param sha Commit SHA
     * @return New branch
     * @throws IOException if there is an I/O problem
     */
    public Branch create(
        final String name,
        final String sha)
        throws IOException {
        final Directives directives = new Directives()
            .xpath(this.xpath())
            .add("branch")
            .attr("name", name)
            .add("sha").set(sha).up();
        this.storage.apply(directives);
        return new MkBranch(this.storage, this.self, this.coords, name, sha);
    }

    /**
     * Gets a branch by name.
     * @param name Name of branch.
     * @return The branch with the given name
     * @throws IOException If there is an I/O problem
     */
    public Branch get(
        final String name
    ) throws IOException {
        return new MkBranch(
            this.storage,
            this.self,
            this.coords,
            name,
            this.storage.xml()
                .nodes(
                    String.format(
                        "%s/branch[@name='%s']",
                        this.xpath(),
                        name
                    )
                )
                .get(0)
                .xpath(MkBranches.XPATH_TO_SHA)
                .get(0)
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/branches",
            this.coords
        );
    }
}
