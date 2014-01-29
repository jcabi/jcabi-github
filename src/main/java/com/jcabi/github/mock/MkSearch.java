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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.jcabi.github.Search;
import com.jcabi.github.User;
import com.jcabi.xml.XML;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Mock Github search.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkSearch implements Search {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     *
     * @param stg Storage
     * @param login User to login
     */
    MkSearch(final MkStorage stg, final String login) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    public Iterable<Repo> repos(
        final String keywords,
        final String sort,
        final String order)
        throws IOException {
        return new MkIterable<Repo>(
            this.storage,
            "/github/repos/repo",
            new MkIterable.Mapping<Repo>() {
                @Override
                public Repo map(final XML xml) {
                    return new MkRepo(
                        MkSearch.this.storage, MkSearch.this.self,
                        new Coordinates.Simple(xml.xpath("@coords").get(0))
                    );
                }
            }
        );
    }

    @Override
    public Iterable<Issue> issues(
        final String keywords,
        final String sort,
        final String order)
        throws IOException {
        return new MkIterable<Issue>(
            this.storage,
            "/github/repos/repo/issues/issue",
            new MkIterable.Mapping<Issue>() {
                @Override
                public Issue map(final XML xml) {
                    return new MkIssue(
                        MkSearch.this.storage, MkSearch.this.self,
                        new Coordinates.Simple(
                            xml.xpath("../../@coords").get(0)
                        ),
                        Integer.parseInt(xml.xpath("number/text()").get(0))
                    );
                }
            }
        );
    }

    @Override
    public Iterable<User> users(
        final String keywords,
        final String sort,
        final String order)
        throws IOException {
        return new MkIterable<User>(
            this.storage,
            "/github/users/user",
            new MkIterable.Mapping<User>() {
                @Override
                public User map(final XML xml) {
                    try {
                        return new MkUser(
                            MkSearch.this.storage,
                            xml.xpath("login/text()").get(0)
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

}
