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
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github issue comment.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/comments/">Issue Comments API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Comment
    extends Comparable<Comment>, JsonReadable, JsonPatchable {

    /**
     * The issue it's in.
     * @return Owner of the comment
     */
    @NotNull(message = "issue is never NULL")
    Issue issue();

    /**
     * Number.
     * @return Comment number
     */
    int number();

    /**
     * Delete the comment.
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/issues/comments/#delete-a-comment">Delete a Comment</a>
     */
    void remove() throws IOException;

    /**
     * Smart comment with additional features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "comment")
    final class Smart implements Comment {
        /**
         * Encapsulated comment.
         */
        private final transient Comment comment;
        /**
         * Public ctor.
         * @param cmt Comment
         */
        public Smart(final Comment cmt) {
            this.comment = cmt;
        }
        /**
         * Get its author.
         * @return Author of comment
         * @throws IOException If fails
         */
        public User author() throws IOException {
            return this.comment.issue().repo().github().users().get(
                this.comment.json().getJsonObject("user").getString("login")
            );
        }
        /**
         * Get its body.
         * @return Body of comment
         * @throws IOException If fails
         */
        public String body() throws IOException {
            // @checkstyle MultipleStringLiterals (1 line)
            return this.comment.json().getString("body");
        }
        /**
         * Change comment body.
         * @param text Body of comment
         * @throws IOException If fails
         */
        public void body(final String text) throws IOException {
            this.comment.patch(
                Json.createObjectBuilder().add("body", text).build()
            );
        }
        /**
         * Get its URL.
         * @return URL of comment
         * @throws IOException If fails
         */
        public URL url() throws IOException {
            return new URL(this.comment.json().getString("url"));
        }
        /**
         * When this comment was created.
         * @return Date of creation
         * @throws IOException If fails
         */
        public Date createdAt() throws IOException {
            return new Time(this.comment.json().getString("created_at")).date();
        }
        /**
         * When this comment was updated last time.
         * @return Date of update
         * @throws IOException If fails
         */
        public Date updatedAt() throws IOException {
            return new Time(this.comment.json().getString("updated_at")).date();
        }
        @Override
        public Issue issue() {
            return this.comment.issue();
        }
        @Override
        public int number() {
            return this.comment.number();
        }
        @Override
        public void remove() throws IOException {
            this.comment.remove();
        }
        @Override
        public JsonObject json() throws IOException {
            return this.comment.json();
        }
        @Override
        public void patch(final JsonObject json) throws IOException {
            this.comment.patch(json);
        }
        @Override
        public int compareTo(final Comment obj) {
            return this.comment.compareTo(obj);
        }
    }

}
