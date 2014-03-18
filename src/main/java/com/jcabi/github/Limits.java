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
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Rate Limit API.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.6
 * @see <a href="http://developer.github.com/v3/rate_limit/">Rate Limit API</a>
 */
@Immutable
public interface Limits {

    /**
     * Resource name.
     */
    String CORE = "core";

    /**
     * Resource name.
     */
    String SEARCH = "search";

    /**
     * Github we're in.
     * @return Github
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Get limit for the given resource.
     * @param resource Name of resource
     * @return Limit
     */
    @NotNull(message = "Limit si never NULL")
    Limit get(@NotNull(message = "resource is never NULL") String resource);

    /**
     * Throttled Limits.
     * @since 0.6
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "origin")
    final class Throttled implements Limits {
        /**
         * Original.
         */
        private final transient Limits origin;
        /**
         * Maximum allowed, instead of default 5000.
         */
        private final transient int max;
        /**
         * Public ctor.
         * @param limits Original limits
         * @param allowed Maximum allowed
         */
        public Throttled(
            @NotNull(message = "limits can't be NULL") final Limits limits,
            final int allowed
        ) {
            this.origin = limits;
            this.max = allowed;
        }
        @Override
        @NotNull(message = "github is never NULL")
        public Github github() {
            return this.origin.github();
        }
        @Override
        @NotNull(message = "limit is never NULL")
        public Limit get(
            @NotNull(message = "resource can't be NULL") final String resource
        ) {
            return new Limit.Throttled(this.origin.get(resource), this.max);
        }
    }

}
