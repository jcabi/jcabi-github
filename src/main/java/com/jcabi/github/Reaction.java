/**
 * Copyright (c) 2013-2018, jcabi.com
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

/**
 * Reaction for issue / comment.
 *
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 1.0
 * @see <a href="https://developer.github.com/v3/reactions">Reactions API</a>
 * @todo #1451:30min Check reaction values. At the moment only a few types of
 *  reactions are allowed (full list at
 *  https://developer.github.com/v3/reactions/#reaction-types). Reaction API
 *  implementation should somehow validate these inputs and do not add an
 *  invalid reaction to a Comment or Issue
 * @todo #1451:30min Create reaction values constants in Reaction. As reaction
 *  values are in a few number they must be created as constants in Reaction.
 *  Then replace all existing code in tests and application to use the new
 *  created constants.
 * @todo #1451:30min Add reaction support to other Github elements.
 *  Reactions API is supported / implemented by other github elements besides
 *  Issues and Issue Comments. Add reactions support to all these other items as
 *  well so jcabi-github can provide full reactions API support. See all
 *  possible reactions API interactions at
 *  https://developer.github.com/v3/reactions/
 */
public interface Reaction {

    /**
     * The reaction type.
     * @return The type of the reaction.
     */
    String type();

    /**
     * Simple reaction.
     */
    final class Simple implements Reaction {

        /**
         * Reaction type.
         */
        private final String type;

        /**
         * Constructor.
         * @param reaction Reaction type.
         */
        Simple(final String reaction) {
            this.type = reaction;
        }

        /**
         * Returns the reaction type.
         * @return Reaction type.
         */
        public String type() {
            return this.type;
        }

    }
}