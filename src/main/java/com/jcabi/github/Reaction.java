/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

/**
 * Reaction for issue / comment.
 *
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
