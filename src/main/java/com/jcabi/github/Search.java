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
import java.util.EnumMap;

/**
 * Github search.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/search/">Search API</a>
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface Search {

    /**
     * Github we're in.
     *
     * @return Github
     */
    Github github();

    /**
     * Search repositories.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Repos
     * @see <a href="http://developer.github.com/v3/search/#search-repositories">Search repositories</a>
     */
    Iterable<Repo> repos(
        String keywords,
        String sort,
        Order order
    );

    /**
     * Search issues.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @param qualifiers The search qualifier
     * @return Issues
     * @see <a href="http://developer.github.com/v3/search/#search-issues">Search issues</a>
     * @checkstyle ParameterNumberCheck (7 lines)
     */
    Iterable<Issue> issues(
        String keywords,
        String sort,
        Order order,
        EnumMap<Qualifier, String> qualifiers);

    /**
     * Search users.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Users
     * @see <a href="http://developer.github.com/v3/search/#search-users">Search users</a>
     */
    Iterable<User> users(
        String keywords,
        String sort,
        Order order);

    /**
     * Search code.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Contents
     * @see <a href="http://developer.github.com/v3/search/#search-code">Search code</a>
     */
    Iterable<Content> codes(
        String keywords,
        String sort,
        Order order);

    enum Qualifier implements StringEnum {
        /**
         * The search by issues or pull request only.
         */
        TYPE("type"),
        /**
         * Qualifies which fields are searched.
         * <p>With this qualifier you can restrict the search to just
         * the title, body, comments, or any combination of these.</p>
         */
        IN("in"),
        /**
         * Finds issues created by a certain user.
         */
        AUTHOR("author"),
        /**
         * Finds issues that are assigned to a certain user.
         */
        ASSIGNEE("assignee"),
        /**
         * Finds issues that mention a certain user.
         */
        MENTIONS("mentions"),
        /**
         * Finds issues that a certain user commented on.
         */
        COMMENTER("commenter"),
        /**
         * Finds issues that were either created by a certain user.
         * <p>Or assigned to that user, mention that user,
         *  or were commented on by that user.</p>
         */
        INVOLVES("involves"),
        /**
         * Finds issues or pull requests which mention a particular team within
         * an organization which the user is a member of.
        */
        TEAM("team"),
        /**
         * Filter issues based on whether they’re open or closed.
         */
        STATE("state"),
        /**
         * Filters issues based on their labels.
         */
        LABEL("label"),
        /**
         * Filters items missing certain metadata.
         */
        NO("no"),
        /**
         * Searches for issues within repositories matching a certain language.
         */
        LANGUAGE("language"),
        /**
         * Searches for items within repositories that match a certain state.
         */
        IS("is"),
        /**
         * Filters issues based on date of creation.
         */
        CREATED("created"),
        /**
         * Filters issues based on date last updated.
         */
        UPDATED("updated"),
        /**
         * Filters pull requests based on the date when they were merged.
         */
        MERGED("merged"),
        /**
         * Filters issues based on the date when they were closed.
         */
        CLOSED("closed"),
        /**
         * Filters issues based on the quantity of comments.
         */
        COMMENTS("comments"),
        /**
         * Limits searches to a specific user.
         */
        USER("user"),
        /**
         * Limits searches to a specific repository.
         */
        REPO("repo");

        /**
         * Search qualifier.
         */
        private final transient String qualifier;

        /**
         * Ctor.
         * @param key Search qualifier
         */
        Qualifier(final String key) {
            this.qualifier = key;
        }

        /**
         * Get search qualifier.
         * @return String
         */
        @Override
        public String identifier() {
            return this.qualifier;
        }
    }

    enum Order implements StringEnum {
        /**
         * Sorting ascending.
         */
        ASC("asc"),
        /**
         * Sorting descending.
         */
        DESC("desc");

        /**
         * The sort order.
         */
        private final transient String order;

        /**
         * Ctor.
         * @param key The sort order
         */
        Order(final String key) {
            this.order = key;
        }

        /**
         * Get sort order.
         * @return String
         */
        @Override
        public String identifier() {
            return this.order;
        }
    }
}
