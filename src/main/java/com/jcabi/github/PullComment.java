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
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github pull comment.
 *
 * <p>PullComment implements {@link JsonReadable},
 * that's how you can get its full details in JSON format.
 * For example, to get its id, you get the entire JSON and
 * then gets its element:
 *
 * <pre>String id = comment.jsn().getString("id");</pre>
 *
 * <p>However, it's better to use a supplementary "smart" decorator, which
 * automates most of these operations:
 *
 * <pre>String id = new PullComment.Smart(comment).identifier();</pre>
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/pulls/comments/">Pull Comments API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface PullComment extends JsonReadable, JsonPatchable,
    Comparable<PullComment> {

    /**
     * Pull we're in.
     * @return Pull
     */
    @NotNull(message = "pull is never NULL")
    Pull pull();

    /**
     * Get its number.
     * @return Pull comment number
     */
    int number();

    /**
     * Smart PullComment with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "cmnt", "jsn" })
    final class Smart implements PullComment {

        /**
         * Id field's name in JSON.
         */
        private static final String ID = "id";

        /**
         * Commit id field's name in JSON.
         */
        private static final String COMMIT_ID = "commit_id";

        /**
         * Commit to reply to id field's name in JSON.
         */
        private static final String REPLY_ID = "in_reply_to";

        /**
         * Url field's name in JSON.
         */
        private static final String URL = "url";

        /**
         * Body field's name in JSON.
         */
        private static final String BODY = "body";

        /**
         * Encapsulated pull comment.
         */
        private final transient PullComment cmnt;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param pcomment Pull comment
         */
        public Smart(
            @NotNull(message = "pull comment is never NULL")
            final PullComment pcomment
        ) {
            this.cmnt = pcomment;
            this.jsn = new SmartJson(pcomment);
        }

        /**
         * Get its id value.
         * @return Id of pull comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "identifier is never NULL")
        public String identifier() throws IOException {
            return this.jsn.text(ID);
        }

        /**
         * Change its id value.
         * @param value Id of pull comment
         * @throws IOException If there is any I/O problem
         */
        public void identifier(
            @NotNull(message = "value cannot be NULL") final String value
        ) throws IOException {
            this.cmnt.patch(
                Json.createObjectBuilder().add(ID, value).build()
            );
        }

        /**
         * Get its commit id value.
         * @return Commit id of pull comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "commit id is never NULL")
        public String commitId() throws IOException {
            return this.jsn.text(COMMIT_ID);
        }

        /**
         * Change its commit id value.
         * @param value Commit id of pull comment
         * @throws IOException If there is any I/O problem
         */
        public void commitId(
            @NotNull(message = "value shouldn't be NULL") final String value
        ) throws IOException {
            this.cmnt.patch(
                Json.createObjectBuilder().add(COMMIT_ID, value).build()
            );
        }

        /**
         * Get its url value.
         * @return Url of pull comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public String url() throws IOException {
            return this.jsn.text(URL);
        }

        /**
         * Get its reply id value.
         * @return Reply id of pull comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public int reply() throws IOException {
            return this.jsn.number(REPLY_ID);
        }

        /**
         * Change its url value.
         * @param value Url of pull comment
         * @throws IOException If there is any I/O problem
         */
        public void url(
            @NotNull(message = "value is never NULL") final String value
        ) throws IOException {
            this.cmnt.patch(
                Json.createObjectBuilder().add(URL, value).build()
            );
        }

        /**
         * Get its body value.
         * @return Url of pull comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "body is never NULL")
        public String body() throws IOException {
            return this.jsn.text(BODY);
        }

        /**
         * Change its body value.
         * @param value Url of pull comment
         * @throws IOException If there is any I/O problem
         */
        public void body(
            @NotNull(message = "value can't be NULL") final String value
        ) throws IOException {
            this.cmnt.patch(
                Json.createObjectBuilder().add(BODY, value).build()
            );
        }

        @Override
        @NotNull(message = "pull is never NULL")
        public Pull pull() {
            return this.cmnt.pull();
        }

        @Override
        public int number() {
            return this.cmnt.number();
        }

        @Override
        public int compareTo(
            @NotNull(message = "comment can't be NULL")
            final PullComment comment
        ) {
            return this.cmnt.compareTo(comment);
        }

        @Override
        public void patch(
            @NotNull(message = "JSON is never NULL") final JsonObject json
        ) throws IOException {
            this.cmnt.patch(json);
        }

        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.cmnt.json();
        }
    }
}
