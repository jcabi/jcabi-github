/**
 * Copyright (c) 2013-2023, jcabi.com
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
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github hook.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/repos/hooks/">Hooks API</a>
 */
@Immutable
public interface Hook extends JsonReadable {

    /**
     * Repository we're in.
     * @return Repo
     */
    Repo repo();

    /**
     * Get its number.
     * @return Hook number
     */
    int number();

    /**
     * Smart Hook with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "hook", "jsn" })
    final class Smart implements Hook {
        /**
         * Encapsulated Hook.
         */
        private final transient Hook hook;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param hoo Hook
         */
        public Smart(final Hook hoo) {
            this.hook = hoo;
            this.jsn = new SmartJson(hoo);
        }
        /**
         * Get its name.
         * @return Name of hook
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }
        @Override
        public Repo repo() {
            return this.hook.repo();
        }
        @Override
        public int number() {
            return this.hook.number();
        }
        @Override
        public JsonObject json() throws IOException {
            return this.hook.json();
        }
    }
}
