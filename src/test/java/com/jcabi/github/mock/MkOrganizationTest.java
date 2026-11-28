/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.GitHub;
import com.jcabi.github.Organization;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkOrganization}.
 *
 * @since 2.0
 */
final class MkOrganizationTest {

    @Test
    void listsRepos() throws IOException {
        final GitHub github = new MkGitHub("jeff");
        final Organization org = github.organizations().get("cats");
        MkOrganizationTest.repo(github, "alpha", "cats");
        MkOrganizationTest.repo(github, "beta", "cats");
        MatcherAssert.assertThat(
            "Repositories of the organization are not listed",
            MkOrganizationTest.coordinates(org.repos()),
            Matchers.contains("cats/alpha", "cats/beta")
        );
    }

    @Test
    void listsOnlyReposOfOrganization() throws IOException {
        final GitHub github = new MkGitHub("jeff");
        final Organization org = github.organizations().get("cats");
        github.repos().create(new Repos.RepoCreate("own", false));
        MkOrganizationTest.repo(github, "bone", "catsfans");
        MkOrganizationTest.repo(github, "gamma", "cats");
        MatcherAssert.assertThat(
            "Repositories of other owners are listed",
            MkOrganizationTest.coordinates(org.repos()),
            Matchers.contains("cats/gamma")
        );
    }

    @Test
    void startsWithoutRepos() throws IOException {
        MatcherAssert.assertThat(
            "New organization has repositories",
            new MkGitHub().organizations().get("dogs").repos(),
            Matchers.emptyIterable()
        );
    }

    private static void repo(final GitHub github, final String name,
        final String org) throws IOException {
        github.repos().create(
            new Repos.RepoCreate(name, false).withOrganization(org)
        );
    }

    private static List<String> coordinates(final Iterable<Repo> repos) {
        final List<String> coords = new ArrayList<>(0);
        for (final Repo repo : repos) {
            coords.add(repo.coordinates().toString());
        }
        return coords;
    }
}
