/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.log.Logger;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Github repos.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkRepos implements Repos {

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
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    MkRepos(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(new Directives().xpath("/github").addIf("repos"));
    }

    @Override
    @NotNull(message = "github can't be NULL")
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    @NotNull(message = "repo is never NULL")
    public Repo create(
        @NotNull(message = "json can't be NULL") final JsonObject json
    ) throws IOException {
        final String name = json.getString("name");
        final Coordinates coords = new Coordinates.Simple(this.self, name);
        this.storage.apply(
            new Directives().xpath(this.xpath()).add("repo")
                .attr("coords", coords.toString())
                .add("name").set(name)
        );
        final Repo repo = this.get(coords);
        repo.patch(json);
        Logger.info(
            this, "repository %s created by %s",
            coords, this.self
        );
        return repo;
    }

    @Override
    @NotNull(message = "Repo is never NULL")
    public Repo get(
        @NotNull(message = "coords can't be NULL") final Coordinates coords
    ) {
        try {
            final String xpath = String.format(
                "%s/repo[@coords='%s']", this.xpath(), coords
            );
            if (this.storage.xml().nodes(xpath).isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("repository %s doesn't exist", coords)
                );
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return new MkRepo(this.storage, this.self, coords);
    }

    @Override
    public void remove(
        @NotNull(message = "coordinates can't be NULL")
        final Coordinates coords) {
        try {
            this.storage.apply(
                new Directives().xpath(
                    String.format("%s/repo[@coords='%s']", this.xpath(), coords)
                ).remove()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Iterate all public repos, starting with the one you've seen already.
     * @todo #841 MkRepos#iterate should be implemented.
     * @param identifier The integer ID of the last Repo that you’ve seen.
     * @return Iterator of repo
     */
    @Override
    public Iterable<Repo> iterate(
        @NotNull(message = "identifier can't be NULL")
        final String identifier) {
        throw new UnsupportedOperationException("MkRepos#iterate");
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    @NotNull(message = "Xpath is never NULL")
    private String xpath() {
        return "/github/repos";
    }

}
