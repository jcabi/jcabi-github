/**
 * Copyright (c) 2013-2024, jcabi.com
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
 * @todo #1469:30min Add support to team discussion and team discussion comments
 *  The API does not supports team discussion and team discussion comments (
 *  https://developer.github.com/changes/2018-02-07-team-discussions-api/ )
 *  After this implementation, add reaction support to these elements.
 */
public interface Reaction {

    /**
     * Thumbs up reaction constant.
     */
    String THUMBSUP = "+1";

    /**
     * Thumbs down reaction constant.
     */
    String THUMBSDOWN = "-1";

    /**
     * Laugh reaction constant.
     */
    String LAUGH = "laugh";

    /**
     * Confused reaction constant.
     */
    String CONFUSED = "confused";

    /**
     * Heart reaction constant.
     */
    String HEART = "heart";

    /**
     * Hooray reaction constant.
     */
    String HOORAY = "hooray";

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
