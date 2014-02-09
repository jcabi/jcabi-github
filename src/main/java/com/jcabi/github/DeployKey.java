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
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github deploy key.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
 * @todo #231 Deploy key object should be able to edit a deploy key. Let's
 *  create a test for for this method, declare it here, implement it in
 *  RtDeployKey and MkDeployKey, and add an integration test for it. See
 *  http://developer.github.com/v3/repos/keys/#edit. When done, remove this
 *  puzzle.
 * @todo #231 Deploy key object should be able to remove a deploy key. Let's
 *  create a test for for this method, declare it here, implement it in
 *  RtDeployKey and MkDeployKey, and add an integration test for it. See
 *  http://developer.github.com/v3/repos/keys/#delete. When done, remove this
 *  puzzle.
 * @todo #356:1hr There should be get() method implemented for reading the value
 *  of title and key. Implement method at RtDeployKey, MkDeployKey and write an
 *  integration test for it. See http://developer.github.com/v3/repos/keys/#get
 *  When done remove this puzzle and finish #356 (write test for edit method)
 */
@Immutable
public interface DeployKey extends JsonReadable {

    /**
     * Get id of a deploy key.
     * @return Id
     */
    int number();

    /**
     * Edits a key.
     * @see <a href="http://developer.github.com/v3/repos/keys/#edit">Deploy keys API</a>
     * @param title New title
     * @param value New value
     * @throws IOException if any I/O problem occurs86
     */
    void edit(String title, String value) throws IOException;

    /**
     * Delete a deploy key.
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/keys/#delete">Remove a deploy key</a>
     */
    void remove() throws IOException;

    /**
     * Smart deploy key.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "key", "json" })
    final class Smart  implements DeployKey {
        /**
         * Encapsulated DeployKey.
         */
        private final transient DeployKey key;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson json;

        /**
         * Public constructor.
         * @param encapsulated Encapsulated DeployKey.
         */
        public Smart(final DeployKey encapsulated) {
            this.key = encapsulated;
            this.json = new SmartJson(encapsulated);
        }

        @Override
        public int number() {
            return this.key.number();
        }

        @Override
        public void edit(final String title, final String value)
            throws IOException {
            this.key.edit(title, value);
        }

        @Override
        public void remove() throws IOException {
            this.key.remove();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.key.json();
        }

        /**
         * Returns the value of the "key" field.
         * @return The value of the "key" field.
         * @throws IOException If ani I/O problem occurs.
         */
        public String value() throws IOException {
            return this.json.text("key");
        }

        /**
         * Returns the value of the "title" field.
         * @return The value of the "title" field.
         * @throws IOException If any I/O problem occurs.
         */
        public String title() throws IOException {
            return this.json.text("title");
        }
    }
}
