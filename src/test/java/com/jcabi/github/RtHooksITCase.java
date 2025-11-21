/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtHooks}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.ADMIN_REPO_HOOK)
public final class RtHooksITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    @Test
    public void canFetchAllHooks() throws IOException {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            RtHooksITCase.createHook(repo);
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                repo.hooks().iterate(), Matchers.iterableWithSize(1)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    public void canCreateAHook() throws IOException {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                "Value is null",
                RtHooksITCase.createHook(repo), Matchers.notNullValue()
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    public void canFetchSingleHook() throws IOException {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final int number = RtHooksITCase.createHook(repo).number();
            MatcherAssert.assertThat(
                "Values are not equal",
                repo.hooks().get(number).json().getInt("id"),
                Matchers.equalTo(number)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    public void canRemoveHook() throws IOException {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final Hook hook = RtHooksITCase.createHook(repo);
            repo.hooks().remove(hook.number());
            MatcherAssert.assertThat(
                "Collection does not contain expected item",
                repo.hooks().iterate(), Matchers.not(Matchers.hasItem(hook))
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Return repos for tests.
     * @return Repos
     */
    private static Repos repos() {
        return GitHubIT.connect().repos();
    }

    /**
     * Create a new hook in a repository.
     * @param repo Repository
     * @return Hook
     * @throws IOException If there is any I/O problem
     */
    private static Hook createHook(final Repo repo) throws IOException {
        final ConcurrentHashMap<String, String> config =
            new ConcurrentHashMap<>();
        config.put(
            "url",
            String.format(
                "http://github.jcabi.invalid/hooks/%s",
                RandomStringUtils.random(Tv.TWENTY)
            )
        );
        config.put("content_type", "json");
        config.put("secret", "shibboleet");
        config.put("insecure_ssl", "0");
        return repo.hooks().create(
            "web",
            config,
            Collections.emptyList(),
            false
        );
    }
}
