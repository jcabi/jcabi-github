/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtHooks}.
 * @since 0.8
 */
@OAuthScope(Scope.ADMIN_REPO_HOOK)
public final class RtHooksITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    /**
     * RtHooks can iterate hooks.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchAllHooks() throws Exception {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            RtHooksITCase.createHook(repo);
            MatcherAssert.assertThat(
                repo.hooks().iterate(), Matchers.<Hook>iterableWithSize(1)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtHooks can create a hook.
     * @throws Exception If some problem inside
     */
    @Test
    public void canCreateAHook() throws Exception {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                RtHooksITCase.createHook(repo), Matchers.notNullValue()
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtHooks can fetch a single hook.
     *
     * @throws Exception If some problem inside.
     */
    @Test
    public void canFetchSingleHook() throws Exception {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final int number = RtHooksITCase.createHook(repo).number();
            MatcherAssert.assertThat(
                repo.hooks().get(number).json().getInt("id"),
                Matchers.equalTo(number)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtHooks can remove a hook by ID.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void canRemoveHook() throws Exception {
        final Repos repos = RtHooksITCase.repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final Hook hook = RtHooksITCase.createHook(repo);
            repo.hooks().remove(hook.number());
            MatcherAssert.assertThat(
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
        return new GithubIT().connect().repos();
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
            Collections.<Event>emptyList(),
            false
        );
    }
}
