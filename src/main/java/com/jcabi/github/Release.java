/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github release.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/releases/">Releases API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Release extends JsonReadable, JsonPatchable {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Release id.
     * @return Id
     */
    int number();

    /**
     * Deletes a release.
     * @throws IOException If any I/O problems occur.
     */
    void delete() throws IOException;

    /**
     * Get all release assets of this release.
     * @return Release assets.
     * @see <a href="https://developer.github.com/v3/repos/releases/">Releases API</a>
     */
    ReleaseAssets assets();

    /**
     * Smart release.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "release", "jsn" })
    final class Smart implements Release {

        /**
         * Encapsulated release.
         */
        private final transient Release release;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public CTOR.
         * @param original Original release
         */
        public Smart(
            final Release original
        ) {
            this.release = original;
            this.jsn = new SmartJson(original);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.release.json();
        }

        @Override
        public void patch(
            final JsonObject json
        ) throws IOException {
            this.release.patch(json);
        }

        @Override
        public Repo repo() {
            return this.release.repo();
        }

        @Override
        public int number() {
            return this.release.number();
        }

        @Override
        public ReleaseAssets assets() {
            return this.release.assets();
        }

        /**
         * Get release url.
         * @return Release url
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            try {
                return new URI(this.jsn.text("url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get release html url.
         * @return Release html url
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            try {
                return new URI(this.jsn.text("html_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get release assets url.
         * @return Release assets url
         * @throws IOException If there is any I/O problem
         */
        public URL assetsUrl() throws IOException {
            try {
                return new URI(this.jsn.text("assets_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get release upload url.
         * @return Release upload url
         * @throws IOException If there is any I/O problem
         */
        public URL uploadUrl() throws IOException {
            try {
                return new URI(this.jsn.text("upload_url")).toURL();
            } catch (final URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        /**
         * Get release tag name.
         * @return The release tag name
         * @throws IOException If there is any I/O problem
         */
        public String tag() throws IOException {
            return this.jsn.text("tag_name");
        }

        /**
         * Has release tag?
         * @return TRUE if tag exists
         * @throws IOException If there is any I/O problem
         * @since 0.21
         */
        public boolean hasTag() throws IOException {
            return this.jsn.hasNotNull("tag_name");
        }

        /**
         * Change its tag name.
         * @param text Tag name
         * @throws IOException If there is any I/O problem
         */
        public void tag(
            final String text
        ) throws IOException {
            this.release.patch(
                Json.createObjectBuilder().add("tag_name", text).build()
            );
        }

        /**
         * Get release target commitish.
         * @return Release target commitish value
         * @throws IOException If there is any I/O problem
         */
        public String commitish() throws IOException {
            return this.jsn.text("target_commitish");
        }

        /**
         * Change its target commitish.
         * @param text Target commitish.
         * @throws IOException If there is any I/O problem
         */
        public void commitish(
            final String text
        ) throws IOException {
            this.release.patch(
                Json.createObjectBuilder()
                    .add("target_commitish", text)
                    .build()
            );
        }

        /**
         * Does this release have a name?
         * @return Whether this release has a name
         * @throws IOException If there is any I/O problem
         */
        public boolean hasName() throws IOException {
            return this.jsn.hasNotNull("name");
        }

        /**
         * Get release name. Note that there may not be one, so make sure to
         * check with {@link Release.Smart#hasName()} first.
         * @return Release name
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Change its name.
         * @param text Name of release.
         * @throws IOException If there is any I/O problem
         */
        public void name(
            final String text
        ) throws IOException {
            this.release.patch(
                Json.createObjectBuilder().add("name", text).build()
            );
        }

        /**
         * Has release body.
         * @return TRUE if release body exists
         * @throws IOException If there is any I/O problem
         */
        public boolean hasBody() throws IOException {
            return this.jsn.hasNotNull("body");
        }

        /**
         * Get release body.
         * @return Release body
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            final String body;
            if (this.hasBody()) {
                body = this.jsn.text("body");
            } else {
                body = "";
            }
            return body;
        }

        /**
         * Change its body.
         * @param text Text describing the contents of the tag
         * @throws IOException If there is any I/O problem
         */
        public void body(
            final String text
        ) throws IOException {
            this.release.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }

        /**
         * Get release creation date.
         * @return Release creation date
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new Github.Time(this.jsn.text("created_at"))
                    .date();
            } catch (final ParseException ex) {
                throw new IOException(ex);
            }
        }

        /**
         * Get release publication date.
         * @return Release publication date
         * @throws IOException If there is any I/O problem
         */
        public Date publishedAt() throws IOException {
            try {
                return new Github.Time(this.jsn.text("published_at"))
                    .date();
            } catch (final ParseException ex) {
                throw new IOException(ex);
            }
        }

        /**
         * Is release draft.
         * @return Returns true if it's draft
         * @throws IOException If there is any I/O problem
         */
        public boolean draft() throws IOException {
            return this.json().getBoolean("draft", Boolean.FALSE);
        }

        /**
         * Change its status.
         * @param draft True makes the release a draft.
         * @throws IOException If there is any I/O problem
         */
        public void draft(final boolean draft) throws IOException {
            this.release.patch(
                Json.createObjectBuilder().add("draft", draft).build()
            );
        }

        /**
         * Is it prerelease.
         * @return Returns true if it's prerelease
         * @throws IOException If there is any I/O problem
         */
        public boolean prerelease() throws IOException {
            return Boolean.parseBoolean(
                this.json()
                    .getOrDefault("prerelease", JsonValue.FALSE)
                    .toString().replace("\"", "")
            );
        }

        /**
         * Change its prerelease.
         * @param pre True to identify the release as a prerelease.
         * @throws IOException If there is any I/O problem
         */
        public void prerelease(final boolean pre) throws IOException {
            this.release.patch(
                Json.createObjectBuilder().add("prerelease", pre).build()
            );
        }

        @Override
        public void delete() throws IOException {
            this.release.delete();
        }

    }

}
