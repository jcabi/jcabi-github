/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * Repository coordinates.
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
     * @since 0.1
     */
    @Immutable
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
            this(
                Coordinates.Simple.parse(mnemo)[0],
                Coordinates.Simple.parse(mnemo)[1]
            );
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

        @Override
        public boolean equals(final Object obj) {
            final boolean result;
            if (this == obj) {
                result = true;
            } else if (obj == null || this.getClass() != obj.getClass()) {
                result = false;
            } else {
                final Coordinates.Simple other = (Coordinates.Simple) obj;
                result = this.usr.equals(other.usr)
                    && this.rpo.equals(other.rpo);
            }
            return result;
        }

        @Override
        public int hashCode() {
            int result = this.usr.hashCode();
            result = 31 * result + this.rpo.hashCode();
            return result;
        }

        /**
         * Parse mnemo into parts.
         * @param mnemo Mnemo name
         * @return Parts
         */
        private static String[] parse(final String mnemo) {
            final String[] parts = mnemo.split(Coordinates.SEPARATOR, 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException(
                    String.format("invalid coordinates '%s'", mnemo)
                );
            }
            return parts;
        }
    }

    /**
     * Implementation of HTTPs coordinates.
     * @since 0.23
     */
    @Immutable
    final class Https implements Coordinates {

        /**
         * GitHub domain.
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
            final String result;
            if (repo.endsWith(suffix)) {
                result = repo.substring(0, repo.length() - suffix.length());
            } else {
                result = repo;
            }
            return result;
        }

        @Override
        public int compareTo(final Coordinates other) {
            return new CompareToBuilder()
                .append(this.user(), other.user())
                .append(this.repo(), other.repo())
                .build();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean result;
            if (this == obj) {
                result = true;
            } else if (obj == null || this.getClass() != obj.getClass()) {
                result = false;
            } else {
                final Coordinates.Https other = (Coordinates.Https) obj;
                result = this.url.equals(other.url);
            }
            return result;
        }

        @Override
        public int hashCode() {
            return this.url.hashCode();
        }

        /**
         * Split URL.
         * @return Array of repo coordinates.
         */
        private String[] split() {
            if (!this.url.startsWith(Coordinates.Https.DOMAIN)) {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid URL, the '%s' should start with '%s'",
                        this.url,
                        Coordinates.Https.DOMAIN
                    )
                );
            }
            return this.url.substring(Coordinates.Https.DOMAIN.length())
                .split(Coordinates.SEPARATOR, 2);
        }
    }
}
