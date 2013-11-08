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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github client.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
public interface Github {

    /**
     * URI builder of the API entry point.
     */
    UriBuilder ENTRY = UriBuilder.fromUri("https://api.github.com");

    /**
     * Get myself.
     * @return Myself
     */
    @NotNull(message = "user is never NULL")
    User self();

    /**
     * Get repository.
     * @param name Repository name in "user/repo" format
     * @return Repository
     */
    @NotNull(message = "repository is never NULL")
    Repo repo(@NotNull String name);

    /**
     * Get gists.
     * @return Gists
     */
    @NotNull(message = "gists is never NULL")
    Gists gists();

    /**
     * Simple implementation.
     */
    @Immutable
    @Loggable(Loggable.DEBUG)
    @ToString
    @EqualsAndHashCode(of = "header")
    final class Simple implements Github {
        /**
         * Authentication header.
         */
        private final transient String header;
        /**
         * Public ctor.
         * @param token OAuth token
         */
        public Simple(@NotNull(message = "token can't be NULL")
            final String token) {
            this.header = String.format("token %s", token);
        }
        @Override
        public User self() {
            return new GhUser(this.header);
        }
        @Override
        @NotNull(message = "repo is never NULL")
        public Repo repo(@NotNull final String name) {
            return new GhRepo(this, this.header, new Coordinates.Simple(name));
        }
        @Override
        @NotNull(message = "gists are never NULL")
        public Gists gists() {
            return new GhGists(this, this.header);
        }
    }

}
