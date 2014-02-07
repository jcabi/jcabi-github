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
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github release.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Release extends JsonReadable, JsonPatchable {

    /**
     * Owner of them.
     * @return Repo
     */
    @NotNull(message = "repository is never NULL")
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
    @NotNull(message = "assets are never NULL")
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
        public Smart(final Release original) {
            this.release = original;
            this.jsn = new SmartJson(original);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.release.json();
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
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
         * Get release target commitish.
         * @return Release target commitish value
         * @throws IOException If there is any I/O problem
         */
        public String commitish() throws IOException {
            return this.jsn.text("target_commitish");
        }

        /**
         * Get release name.
         * @return Release name
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Get release body.
         * @return Release body
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            return this.jsn.text("body");
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
            return booleanValue("draft");
        }

        /**
         * Is it prerelease.
         * @return Returns true if it's prerelease
         * @throws IOException If there is any I/O problem
         */
        public boolean prerelease() throws IOException {
            return booleanValue("prerelease");
        }

        /**
         * Get a property as boolean.
         * @param name Parameter name
         * @return Returns boolean property value
         * @throws IOException If there is any I/O problem
         */
        private boolean booleanValue(final String name) throws IOException {
            return JsonValue.TRUE.equals(this.json().get(name));
        }

        @Override
        public void delete() throws IOException {
            this.release.delete();
        }
    }

}
