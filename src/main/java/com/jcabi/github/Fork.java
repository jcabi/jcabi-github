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
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github fork.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/forks/">Forks API</a>
 * @todo #121 Add more Fork attributes to Smart decorator for this class. Don't forget to add them to unit test.
 */
@Immutable
public interface Fork extends JsonReadable, JsonPatchable {
    /**
     * Fork id.
     * @return Id
     */
    int number();

    /**
     * Smart Fork with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "fork", "jsn" })
    final class Smart implements Fork {
        /**
         * Encapsulated Fork.
         */
        private final transient Fork fork;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param frk Fork
         */
        public Smart(@NotNull(message = "fork can't be NULL") final Fork frk) {
            this.fork = frk;
            this.jsn = new SmartJson(frk);
        }

        /**
         * Get its name.
         * @return Name of fork
         * @throws java.io.IOException If there is any I/O problem
         */
        @NotNull(message = "name is never NULL")
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Get its organization.
         * @return Organization
         * @throws java.io.IOException If there is any I/O problem
         */
        @NotNull(message = "organization is never NULL")
        public String organization() throws IOException {
            return this.jsn.text("organization");
        }

        /**
         * Get its URL.
         * @return URL of fork
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "url is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }

        @Override
        public int number() {
            return this.fork.number();
        }

        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.fork.json();
        }

        @Override
        public void patch(
            @NotNull(message = "JSON is never NULL")final JsonObject json)
            throws IOException {
            this.fork.patch(json);
        }
    }
}
