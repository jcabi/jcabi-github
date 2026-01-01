/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import java.util.EnumMap;

/**
 * GitHub search.
 * @see <a href="https://developer.github.com/v3/search/">Search API</a>
 * @since 0.8
 */
@Immutable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface Search {

    /**
     * GitHub we're in.
     * @return GitHub
     */
    GitHub github();

    /**
     * Search repositories.
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Repos
     * @see <a href="https://developer.github.com/v3/search/#search-repositories">Search repositories</a>
     */
    Iterable<Repo> repos(
        String keywords,
        String sort,
        Search.Order order
    );

    /**
     * Search issues.
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @param qualifiers The search qualifier
     * @return Issues
     * @see <a href="https://developer.github.com/v3/search/#search-issues">Search issues</a>
     * @checkstyle ParameterNumberCheck (7 lines)
     */
    Iterable<Issue> issues(
        String keywords,
        String sort,
        Search.Order order,
        EnumMap<Search.Qualifier, String> qualifiers);

    /**
     * Search users.
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Users
     * @see <a href="https://developer.github.com/v3/search/#search-users">Search users</a>
     */
    Iterable<User> users(
        String keywords,
        String sort,
        Search.Order order);

    /**
     * Search code.
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Contents
     * @see <a href="https://developer.github.com/v3/search/#search-code">Search code</a>
     */
    Iterable<Content> codes(
        String keywords,
        String sort,
        Search.Order order);

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

        @Override
        public String identifier() {
            return this.order;
        }
    }
}
