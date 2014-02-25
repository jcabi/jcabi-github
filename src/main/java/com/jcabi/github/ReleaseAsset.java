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
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github release asset.
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @todo #282 We should be able to fetch a release asset's binary contents. See
 *  http://developer.github.com/v3/repos/releases/#get-a-single-release-asset
 *  for details on how this needs to be done. The ReleaseAsset interface should
 *  be able to expose this function through a method, which we can name
 *  something like "content", "body" or "raw", whichever is most appropriate.
 *  I'm not sure what the return type should be at the moment but it will likely
 *  be either a byte array or a stream implementation.
 * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
 * @since 0.8
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface ReleaseAsset extends JsonReadable, JsonPatchable {

    /**
     * The release we're in.
     * @return Release
     */
    @NotNull(message = "release is never NULL")
    Release release();

    /**
     * Number.
     * @return Release asset number
     */
    int number();

    /**
     * Delete the release asset.
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/releases/#delete-a-release-asset">Delete a Release Asset</a>
     */
    void remove() throws IOException;

    /**
     * Smart ReleaseAsset with extra features.
     * @checkstyle MultipleStringLiterals (500 lines)
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {"releaseAsset", "jsn" })
    final class Smart implements ReleaseAsset {
        /**
         * Encapsulated Release Asset.
         */
        private final transient ReleaseAsset releaseAsset;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param asset Release asset
         */
        public Smart(final ReleaseAsset asset) {
            this.releaseAsset = asset;
            this.jsn = new SmartJson(asset);
        }

        /**
         * Get its URL.
         * @return URL of release asset
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }

        /**
         * Get its name.
         * @return Name of release asset
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Get its label.
         * @return Label of release asset
         * @throws IOException If there is any I/O problem
         */
        public String label() throws IOException {
            return this.jsn.text("label");
        }

        /**
         * Get its state.
         * @return State of release asset
         * @throws IOException If there is any I/O problem
         */
        public String state() throws IOException {
            return this.jsn.text("state");
        }

        /**
         * Get its content type.
         * @return Content type of release asset
         * @throws IOException If there is any I/O problem
         */
        public String contentType() throws IOException {
            return this.jsn.text("content_type");
        }

        /**
         * Get its size.
         * @return Size of release asset
         * @throws IOException If there is any I/O problem
         */
        public int size() throws IOException {
            return this.jsn.number("size");
        }

        /**
         * Get its downloadCount.
         * @return Download count of release asset
         * @throws IOException If there is any I/O problem
         */
        public int downloadCount() throws IOException {
            return this.jsn.number("download_count");
        }

        /**
         * When it was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new Github.Time(
                    this.jsn.text("created_at")
                ).date();
            } catch (ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * When it was updated.
         * @return Date of update
         * @throws IOException If there is any I/O problem
         */
        public Date updatedAt() throws IOException {
            try {
                return new Github.Time(
                    this.jsn.text("updated_at")
                ).date();
            } catch (ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * Change its name.
         * @param text Name of release asset
         * @throws IOException If there is any I/O problem
         */
        public void name(final String text) throws IOException {
            this.releaseAsset.patch(
                Json.createObjectBuilder().add("name", text).build()
            );
        }

        /**
         * Change its label.
         * @param text Label of release asset
         * @throws IOException If there is any I/O problem
         */
        public void label(final String text) throws IOException {
            this.releaseAsset.patch(
                Json.createObjectBuilder().add("label", text).build()
            );
        }

        @Override
        public Release release() {
            return this.releaseAsset.release();
        }

        @Override
        public int number() {
            return this.releaseAsset.number();
        }

        @Override
        public void remove() throws IOException {
            this.releaseAsset.remove();
        }

        @Override
        public void patch(
            @NotNull(message = "JSON is never NULL") final JsonObject json
        ) throws IOException {
            this.releaseAsset.patch(json);
        }

        @Override
        public JsonObject json() throws IOException {
            return this.releaseAsset.json();
        }
    }
}
