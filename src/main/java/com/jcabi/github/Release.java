/**
 * Copyright (c) 2013-2022, jcabi.com
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
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github release.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
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
     * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
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
            return new URL(this.jsn.text("url"));
        }

        /**
         * Get release html url.
         * @return Release html url
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            return new URL(this.jsn.text("html_url"));
        }

        /**
         * Get release assets url.
         * @return Release assets url
         * @throws IOException If there is any I/O problem
         */
        public URL assetsUrl() throws IOException {
            return new URL(this.jsn.text("assets_url"));
        }

        /**
         * Get release upload url.
         * @return Release upload url
         * @throws IOException If there is any I/O problem
         */
        public URL uploadUrl() throws IOException {
            return new URL(this.jsn.text("upload_url"));
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
