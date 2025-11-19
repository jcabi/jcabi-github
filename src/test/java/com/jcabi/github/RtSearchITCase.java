/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import java.util.EnumMap;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtSearch}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (140 lines)
 */
@OAuthScope({ OAuthScope.Scope.REPO, OAuthScope.Scope.USER })
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtSearchITCase {

    @Test
    public void canSearchForRepos() {
        MatcherAssert.assertThat(
            "Collection is not empty",
            GitHubIT.connect()
                .search().repos("repo", "stars", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterableOf(Repo.class))
        );
    }

    /**
     * RtSearch can fetch multiple pages of a large result (more than 25 items).
     */
    @Test
    public void canFetchMultiplePages() {
        final Iterator<Repo> iter = GitHubIT.connect().search().repos(
            "java", "", Search.Order.DESC
        ).iterator();
        int count = 0;
        while (iter.hasNext() && count < Tv.HUNDRED) {
            iter.next().coordinates();
            count += 1;
        }
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            count,
            Matchers.greaterThanOrEqualTo(Tv.HUNDRED)
        );
    }

    @Test
    public void canSearchForIssues() {
        final EnumMap<Search.Qualifier, String> qualifiers =
            new EnumMap<>(Search.Qualifier.class);
        qualifiers.put(Search.Qualifier.LABEL, "bug");
        MatcherAssert.assertThat(
            "Collection is not empty",
            GitHubIT.connect().search().issues(
                "qualifiers",
                "updated",
                Search.Order.DESC,
                qualifiers
            ),
            Matchers.not(Matchers.emptyIterableOf(Issue.class))
        );
    }

    @Test
    public void canSearchForUsers() {
        MatcherAssert.assertThat(
            "Collection is not empty",
            GitHubIT.connect()
                .search().users("jcabi", "joined", Search.Order.DESC),
            Matchers.not(Matchers.emptyIterableOf(User.class))
        );
    }

    /**
     * RtSearch can search for contents.
     * @see <a href="https://developer.github.com/v3/search/#search-code">Search API</a> for details
     */
    @Test
    public void canSearchForContents() {
        MatcherAssert.assertThat(
            "Collection is not empty",
            GitHubIT.connect().search().codes(
                "addClass repo:jquery/jquery", "joined", Search.Order.DESC
            ),
            Matchers.not(Matchers.emptyIterableOf(Content.class))
        );
    }

}
