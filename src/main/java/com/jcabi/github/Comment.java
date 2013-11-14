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
 * Github get comment.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/comments/">Issue Comments API</a>
 */
@Immutable
public interface Comment extends Comparable<Comment> {

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
     * Get author of it.
     * @return User who posted the comment
     * @throws IOException If fails
     */
    @NotNull(message = "comment author is never NULL")
    User author() throws IOException;

    /**
     * Delete the comment.
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/issues/comments/#delete-a-comment">Delete a Comment</a>
     */
    void remove() throws IOException;

    /**
     * Describe it in a JSON object.
     * @return JSON object
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/issues/comments/#get-a-single-comment">Get a Single Comment</a>
     */
    @NotNull(message = "JSON object is never NULL")
    JsonObject json() throws IOException;

    /**
     * Patch using this JSON object.
     * @param json JSON object
     * @throws IOException If fails
     * @see <a href="http://developer.github.com/v3/issues/comments/#edit-a-comment">Edit a Comment</a>
     */
    void patch(@NotNull(message = "JSON object can't be NULL") JsonObject json)
        throws IOException;

    /**
     * Comment manipulation toolkit.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "comment")
    final class Tool {
        /**
         * Encapsulated comment.
         */
        private final transient Comment comment;
        /**
         * Public ctor.
         * @param cmt Comment
         */
        public Tool(final Comment cmt) {
            this.comment = cmt;
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
    }

}
