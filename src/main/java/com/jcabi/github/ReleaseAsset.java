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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github release asset.
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @todo #282 Implement a Smart decorator for ReleaseAsset for the purposes of
 * JSON parsing. This class should be able to return the various attributes of
 * the JSON response for fetching comments, such as the ID, commit ID, URL, and
 * comment body. Smart should also be able to handle editing the attributes
 * of an existing comment by using
 * {@link JsonPatchable#patch(javax.json.JsonObject)}. Also include an example
 * of how to do this in the Javadoc comment above. You can refer to
 * {@link PublicKey} on how to do this.
 * @todo #282 We should be able to fetch a release asset's binary contents. See
 * http://developer.github.com/v3/repos/releases/#get-a-single-release-asset
 * for details on how this needs to be done. The ReleaseAsset interface should
 * be able to expose this function through a method, which we can name
 * something like "content", "body" or "raw", whichever is most appropriate.
 * I'm not sure what the return type should be at the moment but it will likely
 * be either a byte array or a stream implementation.
 * @see <a href="http://developer.github.com/v3/repos/releases/">Releases API</a>
 * @since 0.8
 */
@Immutable
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
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {"releaseAsset", "jsn"})
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
         * @param ra releaseAsset
         */
        public Smart(final ReleaseAsset ra) {
            this.releaseAsset = ra;
            this.jsn = new SmartJson(ra);
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
         * @return name of release asset
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Get its label.
         * @return label of release asset
         * @throws IOException If there is any I/O problem
         */
        public String label() throws IOException {
            return this.jsn.text("label");
        }

        /**
         * Get its state.
         * @return state of release asset
         * @throws IOException If there is any I/O problem
         */
        public String state() throws IOException {
            return this.jsn.text("state");
        }

        /**
         * Get its content type.
         * @return content type of release asset
         * @throws IOException If there is any I/O problem
         */
        public String contentType() throws IOException {
            return this.jsn.text("content_type");
        }

        /**
         * Get its size.
         * @return size of release asset
         * @throws IOException If there is any I/O problem
         */
        public int size() throws IOException {
            return this.jsn.number("size");
        }

        /**
         * Get its downloadCount.
         * @return downloadCount of release asset
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void patch(@NotNull(message = "JSON is never NULL") final JsonObject json) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public JsonObject json() throws IOException {
            throw new UnsupportedOperationException();
        }
    }


}
