/**
 * Copyright (c) 2013-2015, jcabi.com
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
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Github issues.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/">Issues API</a>
 */
@Immutable
public interface Issues {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Get specific issue by number.
     * @param number Issue number
     * @return Issue
     * @see <a href="http://developer.github.com/v3/issues/#get-a-single-issue">Get a Single Issue</a>
     */
    Issue get(int number);

    /**
     * Create new issue.
     * @param title Title
     * @param body Body of it
     * @return Issue just created
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/#create-an-issue">Create an Issue</a>
     */
    Issue create(String title, String body) throws IOException;

    /**
     * Iterate them all.
     * @param params Iterating parameters, as requested by API
     * @return Iterator of issues
     * @see <a href="http://developer.github.com/v3/issues/#list-issues">List Issues</a>
     */
    Iterable<Issue> iterate(Map<String, String> params);

    /**
     * Search for issues within the given repository.
     *
     * @param sort The sort field
     * @param direction The sort direction
     * @param qualifiers The search qualifier
     * @return Issues
     * @throws java.io.IOException If there is any I/O problem
     * @since 0.22.0
     * @see <a href="https://developer.github.com/v3/issues/#list-issues-for-a-repository">List issues for a repository</a>
     */
    Iterable<Issue> search(
        Sort sort, Search.Order direction,
        EnumMap<Qualifier, String> qualifiers)
        throws IOException;

    enum Qualifier implements StringEnum {
        /**
         * Filter issues based on which milestone they are assigned to.
         * "none" means no assigned milestone. "*" means any milestone.
         */
        MILESTONE("milestone"),
        /**
         * Filter issues based on whether they're open or closed.
         */
        STATE("state"),
        /**
         * Finds issues that are assigned to a certain user.
         * "none" means no assigned user. "*" means assigned to any user.
         */
        ASSIGNEE("assignee"),
        /**
         * Finds issues created by a certain user.
         */
        CREATOR("creator"),
        /**
         * Finds issues that mention a certain user.
         */
        MENTIONED("mentioned"),
        /**
         * Filters issues based on their labels,
         * as a comma-separated list of label names.
         * An issue must have all of the labels in the list in order to
         * appear in the search results.
         */
        LABELS("labels"),
        /**
         * Filters issues based on date last updated (as an ISO 8601 timestamp).
         */
        SINCE("since");

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

    enum Sort implements StringEnum {
        /**
         * Issue creation timestamp.
         */
        CREATED("created"),
        /**
         * Issue last updated timestamp.
         */
        UPDATED("updated"),
        /**
         * Number of comments on the issue.
         */
        COMMENTS("comments");

        /**
         * Search results sort field.
         */
        private final transient String sort;

        /**
         * Ctor.
         * @param field Search results sort field
         */
        Sort(final String field) {
            this.sort = field;
        }

        /**
         * Get search results sort field.
         * @return String
         */
        @Override
        public String identifier() {
            return this.sort;
        }
    }
}
