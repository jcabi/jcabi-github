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
import com.jcabi.github.Collaborators;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import com.jcabi.github.User;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import org.xembly.Directives;

/**
 * Mock Github repository collaborators.
 *
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
@Immutable
final class MkCollaborators implements Collaborators {

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
     * @param crds Coordinates
     * @throws IOException If there is any I/O problem
     */
    public MkCollaborators(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login,
        @NotNull(message = "crds can't be NULL") final Coordinates crds
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = crds;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']", this.coords
                )
            ).addIf("collaborators")
        );
    }

    @Override
    @NotNull(message = "Repository is never NULL")
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public boolean isCollaborator(
        @NotNull(message = "User is never null") final String user
    ) throws IOException {
        return !this.storage.xml().xpath(
            String.format("%s/user[login='%s']/text()", this.xpath(), user)
        ).isEmpty();
    }

    @Override
    public void add(
        @NotNull(message = "User is never null") final String user
    ) throws IOException {
        this.storage.lock();
        try {
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("user").add("login")
                    .set(user)
            );
        } finally {
            this.storage.unlock();
        }
    }

    @Override
    public void remove(
        @NotNull(message = "user should not be NULL") final String user
    ) throws IOException {
        this.storage.apply(
            new Directives().xpath(
                String.format("%s/user[login='%s']", this.xpath(), user)
            ).remove()
        );
    }

    @Override
    @NotNull(message = "Iterable of users is never NULL")
    public Iterable<User> iterate() {
        return new MkIterable<User>(
            this.storage, String.format("%s/user", this.xpath()),
            new MkIterable.Mapping<User>() {
                @Override
                public User map(final XML xml) {
                    try {
                        return new MkUser(
                            MkCollaborators.this.storage,
                            xml.xpath("login/text()").get(0)
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

    /**
     * Gets a mocked User.
     * @param login User login
     * @return Mocked User
     * @throws IOException If there is any I/O problem
     */
    public User get(
        @NotNull(message = "login should not be NULL") final String login
    ) throws IOException {
        return new MkUser(this.storage, login);
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/collaborators",
            this.coords
        );
    }
}
