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
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Repository coordinates.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
public interface Coordinates {

    /**
     * Get usr name.
     * @return User name
     */
    @NotNull(message = "user is never NULL")
    String user();

    /**
     * Get rpo name.
     * @return Repo name
     */
    @NotNull(message = "repo is never NULL")
    String repo();

    @Immutable
    @EqualsAndHashCode(of = { "usr", "rpo" })
    final class Simple implements Coordinates {
        /**
         * User name.
         */
        private final transient String usr;
        /**
         * Repository name.
         */
        private final transient String rpo;
        /**
         * Public ctor.
         * @param user User name
         * @param repo Repository name
         */
        Simple(
            @NotNull(message = "user can't be NULL") final String user,
            @NotNull(message = "repo can't be NULL") final String repo) {
            this.usr = user;
            this.rpo = repo;
        }
        /**
         * Public ctor.
         * @param mnemo Mnemo name
         */
        Simple(@NotNull(message = "mnemo can't be NULL") final String mnemo) {
            final String[] parts = mnemo.split("/", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException(
                    String.format("invalid coordinates '%s'", mnemo)
                );
            }
            this.usr = parts[0];
            this.rpo = parts[1];
        }
        @Override
        public String toString() {
            return String.format("%s/%s", this.usr, this.rpo);
        }
        @Override
        public String user() {
            return this.usr;
        }
        @Override
        public String repo() {
            return this.rpo;
        }
    }

}
