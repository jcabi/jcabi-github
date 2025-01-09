/**
 * Copyright (c) 2013-2025, jcabi.com
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
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Rate Limit API, one resource limit.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.6
 * @see <a href="https://developer.github.com/v3/rate_limit/">Rate Limit API</a>
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
public interface Limit extends JsonReadable {

    /**
     * Github we're in.
     * @return Github
     */
    Github github();

    /**
     * Smart limits with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "origin")
    final class Smart implements Limit {
        /**
         * Encapsulated limit.
         */
        private final transient Limit origin;
        /**
         * Public ctor.
         * @param limit Limit
         */
        public Smart(
            final Limit limit
        ) {
            this.origin = limit;
        }
        /**
         * Limit of number of requests.
         * @return Number of requests you can make in total
         * @throws IOException If it fails
         */
        public int limit() throws IOException {
            return new SmartJson(this.origin).number("limit");
        }
        /**
         * Remaining number of requests.
         * @return Number of requests you can still make
         * @throws IOException If it fails
         */
        public int remaining() throws IOException {
            return new SmartJson(this.origin).number("remaining");
        }
        /**
         * When will the limit be reset.
         * @return Date when this will happen
         * @throws IOException If it fails
         */
        public Date reset() throws IOException {
            return new Date(
                TimeUnit.MILLISECONDS.convert(
                    (long) new SmartJson(this.origin).number("reset"),
                    TimeUnit.SECONDS
                )
            );
        }
        @Override
        public JsonObject json() throws IOException {
            return this.origin.json();
        }
        @Override
        public Github github() {
            return this.origin.github();
        }
    }

    /**
     * Throttled Limit.
     * @since 0.6
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "origin", "jsn" })
    final class Throttled implements Limit {
        /**
         * Original.
         */
        private final transient Limit origin;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Maximum allowed, instead of default 5000.
         */
        private final transient int max;
        /**
         * Public ctor.
         * @param limit Original limit
         * @param allowed Maximum allowed
         */
        public Throttled(
            final Limit limit,
            final int allowed
        ) {
            this.origin = limit;
            this.max = allowed;
            this.jsn = new SmartJson(limit);
        }
        @Override
        public JsonObject json() throws IOException {
            final int limit = new SmartJson(this.origin).number("limit");
            final int remaining = this.max - (
                limit - new SmartJson(this.origin).number("remaining")
                );
            return Json.createObjectBuilder()
                .add("limit", limit)
                .add("remaining", remaining)
                .add("reset", new SmartJson(this.origin).number("reset"))
                .build();
        }
        @Override
        public Github github() {
            return this.origin.github();
        }
    }

}
