/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link RtReferences}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@OAuthScope(OAuthScope.Scope.REPO)
@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
public final class RtReferencesITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    private static final RepoRule rule = new RepoRule();

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     */
    @BeforeClass
    public static void setUp() throws IOException {
        final Github github = new GithubIT().connect();
        RtReferencesITCase.repos = github.repos();
        RtReferencesITCase.repo = RtReferencesITCase.rule.repo(RtReferencesITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterClass
    public static void tearDown() throws IOException {
        if (RtReferencesITCase.repos != null && RtReferencesITCase.repo != null) {
            RtReferencesITCase.repos.remove(RtReferencesITCase.repo.coordinates());
        }
    }

    /**
     * RtReference can create a reference.
     */
    @Test
    public void createsReference() throws IOException {
        final References refs = RtReferencesITCase.repo.git().references();
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final StringBuilder builder = new StringBuilder("refs/tags/")
            .append(name);
        final Reference reference = refs.create(
            builder.toString(),
            refs.get("refs/heads/master").json().getJsonObject("object")
                .getString("sha")
        );
        MatcherAssert.assertThat(
            reference,
            Matchers.notNullValue()
        );
        builder.delete(0, builder.length());
        builder.append("tags/").append(name);
        refs.remove(builder.toString());
    }

    /**
     * RtReference can iterate over references.
     */
    @Test
    public void iteratesReferences() throws IOException {
        final References refs = RtReferencesITCase.repo.git().references();
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final StringBuilder builder = new StringBuilder("refs/heads/")
            .append(name);
        refs.create(
            builder.toString(),
            refs.get("refs/heads/master").json().getJsonObject("object")
                .getString("sha")
        );
        MatcherAssert.assertThat(
            refs.iterate(),
            Matchers.notNullValue()
        );
        builder.delete(0, builder.length());
        builder.append("heads/").append(name);
        refs.remove(builder.toString());
    }

    /**
     * RtReference can iterate over references in sub-namespace.
     */
    @Test
    public void iteratesReferencesInSubNamespace() throws IOException {
        final References refs = RtReferencesITCase.repo.git().references();
        final String name = RandomStringUtils.randomAlphanumeric(Tv.TEN);
        final StringBuilder builder = new StringBuilder("refs/heads/")
            .append(name);
        refs.create(
            builder.toString(),
            refs.get("refs/heads/master").json().getJsonObject("object")
                .getString("sha")
        );
        MatcherAssert.assertThat(
            refs.iterate("heads"),
            Matchers.notNullValue()
        );
        builder.delete(0, builder.length());
        builder.append("heads/").append(name);
        refs.remove(builder.toString());
    }

}
