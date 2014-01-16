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
public interface Release extends Comparable<Release>,
    JsonReadable, JsonPatchable {

    /**
     * Release id.
     * @return Id
     */
    int number();

    /**
     * Delete the release.
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/releases/#delete-a-release">Delete a release</a>
     */
    void remove() throws IOException;

    /**
     * Smart pull request with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "release")
    final class Smart implements Release {
        /**
         * Encapsulated release.
         */
        private final transient Release release;
        /**
         * Public ctor.
         * @param rel Release
         */
        public Smart(final Release rel) {
            this.release = rel;
        }
        /**
         * Get the tag name of the release.
         * @return The name of the tag
         * @throws IOException If there is any I/O problem
         */
        public String tag() throws IOException {
            return new SmartJson(this).text("tag_name");
        }
        /**
         * Get target commitish of the release.
         * @return Specifies the commitish value.
         * @throws IOException If there is any I/O problem
         */
        public String commitish() throws IOException {
            return new SmartJson(this).text("target_commitish");
        }
        /**
         * Get the name of the release.
         * @return Name of release
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return new SmartJson(this).text("name");
        }
        /**
         * Get its body.
         * @return Body of release
         * @throws IOException If there is any I/O problem
         */
        public String body() throws IOException {
            return new SmartJson(this).text("body");
        }
        /**
         * Get its URL.
         * @return URL of release
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            return new URL(new SmartJson(this).text("url"));
        }
        /**
         * Get its HTML URL.
         * @return HTML URL of release
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            return new URL(new SmartJson(this).text("html_url"));
        }
        /**
         * Get its Assets URL.
         * @return Assets URL of release
         * @throws IOException If there is any I/O problem
         */
        public URL assetsUrl() throws IOException {
            return new URL(new SmartJson(this).text("assets_url"));
        }
        /**
         * Get its Upload URL.
         * @return Upload URL of release
         * @throws IOException If there is any I/O problem
         */
        public URL uploadUrl() throws IOException {
            return new URL(new SmartJson(this).text("upload_url"));
        }
        /**
         * When this release was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new Github.Time(
                    new SmartJson(this).text("created_at")
                ).date();
            } catch (ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * When this release was published.
         * @return Date of published
         * @throws IOException If there is any I/O problem
         */
        public Date publishedAt() throws IOException {
            try {
                return new Github.Time(
                    new SmartJson(this).text("published_at")
                ).date();
            } catch (ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }
        @Override
        public int number() {
            return this.release.number();
        }

        @Override
        public void remove() throws IOException {
            this.release.remove();
        }

        @Override
        public int compareTo(final Release rel) {
            return this.release.compareTo(rel);
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.release.patch(json);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.release.json();
        }
    }
}
