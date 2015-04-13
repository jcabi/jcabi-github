/**
 * Copyright (c) 2013-2015, jcabi.com
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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github status.
 *
 * <p>The status exposes all available properties through its
 * {@code json()} method. However, it is recommended to use its
 * "smart" decorator, which helps you to get access to all JSON properties,
 * for example:
 *
 * <pre> URL url = new Status.Smart(status).url();</pre>
 *
 * @author Marcin Cylke (marcin.cylke+github@gmail.com)
 * @version $Id$
 * @since 1.16
 * @see <a href="https://developer.github.com/v3/repos/statuses/">Repo statuses</a>
 */
@Immutable
public interface Statuses extends Comparable<Statuses>, JsonReadable {

    /**
     * Associated commit.
     * @return Commit
     */
    @NotNull(message = "commit is never NULL")
    Commit commit();

    /**
     * Create new status.
     * @param status Add this status
     * @throws java.io.IOException If there is any I/O problem
     * @return The added status
     */
    Status create(
            @NotNull(message = "status can't be NULL") final Status status
    ) throws IOException;

    /**
     * List all statuses for a given ref.
     * @param ref It can be a SHA, a branch name, or a tag name.
     * @return List of statuses
     */
    @NotNull(message = "list of statuses is never NULL")
    Iterable<Statuses> list(
            @NotNull(message = "ref can't be NULL") final String ref
    );

    /**
     * Smart commit.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "statuses", "jsn" })
    final class Smart implements Statuses {
        /**
         * Encapsulated status.
         */
        private final transient Statuses statuses;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param stats Status
         */
        public Smart(
                @NotNull(message = "cmt can't be NULL") final Statuses stats
        ) {
            this.statuses = stats;
            this.jsn = new SmartJson(this.statuses);
        }
        /**
         * Get its message.
         * @return Message of commit
         * @throws java.io.IOException If there is any I/O problem
         */
        @NotNull(message = "message is never NULL")
        public String message() throws IOException {
            return this.jsn.text("message");
        }
        /**
         * Get its URL.
         * @return URL of comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }
        @Override
        @NotNull(message = "commit is never NULL")
        public Commit commit() {
            return this.statuses.commit();
        }

        @Override
        public Status create(
                @NotNull(message = "status can't be NULL") final Status status
        ) throws IOException {
            return this.statuses.create(status);
        }

        @Override
        public Iterable<Statuses> list(
                @NotNull(message = "ref can't be NULL") final String ref
        ) {
            return this.statuses.list(ref);
        }

        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.statuses.json();
        }
        @Override
        public int compareTo(
                @NotNull(message = "obj can't be NULL") final Statuses obj
        ) {
            return this.statuses.compareTo(obj);
        }
    }
}
