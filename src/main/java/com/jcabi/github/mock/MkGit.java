/**
 * Copyright (c) 2013-2025, jcabi.com
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
import com.jcabi.github.Blobs;
import com.jcabi.github.Commits;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Git;
import com.jcabi.github.References;
import com.jcabi.github.Repo;
import com.jcabi.github.Tags;
import com.jcabi.github.Trees;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Github Mock Git.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkGit implements Git {

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
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    public MkGit(
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
            ).addIf("git")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Blobs blobs() throws IOException {
        return new MkBlobs(this.storage, this.self, this.coords);
    }

    @Override
    public Commits commits() {
        try {
            return new MkCommits(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public References references() {
        try {
            return new MkReferences(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Tags tags() {
        try {
            return new MkTags(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Trees trees() {
        try {
            return new MkTrees(this.storage, this.self, this.coords);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
