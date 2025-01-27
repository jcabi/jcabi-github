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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Repository coordinates.
 *
 * @since 0.1
 */
@Immutable
public interface Coordinates extends Comparable<Coordinates> {

    /**
     * Coordinate pats separator.
     */
    String SEPARATOR = "/";

    /**
     * Get usr name.
     * @return User name
     */
    String user();

    /**
     * Get rpo name.
     * @return Repo name
     */
    String repo();

    /**
     * Jcabi.http implementation.
     */
    @Immutable
    @EqualsAndHashCode(of = {"usr", "rpo"})
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
        public Simple(final String user, final String repo) {
            this.usr = user;
            this.rpo = repo;
        }

        /**
         * Public ctor.
         * @param mnemo Mnemo name
         */
        public Simple(final String mnemo) {
            final String[] parts = mnemo.split(Coordinates.SEPARATOR, 2);
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

        @Override
        public int compareTo(final Coordinates other) {
            return new CompareToBuilder()
                .append(this.usr, other.user())
                .append(this.rpo, other.repo())
                .build();
        }
    }

    /**
     * Implementation of HTTPs coordinates.
     * @author volodya-lombrozo
     */
    @Immutable
    @EqualsAndHashCode
    final class Https implements Coordinates {

        /**
         * Github domain.
         */
        private static final String DOMAIN = "https://github.com/";

        /**
         * URL.
         */
        private final String url;

        /**
         * Public ctor.
         * @param https URL
         */
        public Https(final String https) {
            this.url = https;
        }

        @Override
        public String user() {
            return this.split()[0];
        }

        @Override
        public String repo() {
            final String repo = this.split()[1];
            final String suffix = ".git";
            if (repo.endsWith(suffix)) {
                return repo.substring(0, repo.length() - suffix.length());
            } else {
                return repo;
            }
        }

        @Override
        public int compareTo(final Coordinates other) {
            return new CompareToBuilder()
                .append(this.user(), other.user())
                .append(this.repo(), other.repo())
                .build();
        }

        /**
         * Split URL.
         * @return Array of repo coordinates.
         */
        private String[] split() {
            if (!this.url.startsWith(Https.DOMAIN)) {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid URL, the '%s' should start with '%s'",
                        this.url,
                        Https.DOMAIN
                    )
                );
            }
            return this.url.substring(Https.DOMAIN.length())
                .split(Coordinates.SEPARATOR, 2);
        }
    }
}
