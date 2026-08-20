/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Fork}.
 * @since 0.8
 */
final class ForkTest {

    @Test
    void fetchesName() throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        final String name = "this is some name";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("name", name)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Fork.Smart(fork).name(),
            Matchers.is(name)
        );
    }

    @Test
    void fetchesFullName() throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        final String name = "test full name";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("full_name", name)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Fork.Smart(fork).fullName(),
            Matchers.is(name)
        );
    }

    @Test
    void fetchesDescription() throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        final String description = "test description";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", description)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Fork.Smart(fork).description(),
            Matchers.is(description)
        );
    }

    @Test
    void fetchesSize() throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        final int prop = 100;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("size", prop)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Fork.Smart(fork).size(),
            Matchers.is(prop)
        );
    }

    @Test
    void fetchesUrl() throws IOException {
        final String url = "https://api.github.com/repos/octocat/Hello-World";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("url", url).url().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesHtmlUrl() throws IOException {
        final String url = "https://github.com/octocat/Hello-World";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("html_url", url).htmlUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesCloneUrl() throws IOException {
        final String url = "https://github.com/octocat/Hello-World.git";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("clone_url", url).cloneUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesGitUrl() throws IOException {
        final String url = "git://github.com/octocat/Hello-World.git";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("git_url", url).gitUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesSshUrl() throws IOException {
        final String url = "git@github.com:octocat/Hello-World.git";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("ssh_url", url).sshUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesSvnUrl() throws IOException {
        final String url = "https://svn.github.com/octocat/Hello-World";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("svn_url", url).svnUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesMirrorUrl() throws IOException {
        final String url = "git://git.example.com/octocat/Hello-World";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("mirror_url", url).mirrorUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesHomeUrl() throws IOException {
        final String url = "https://github.com";
        MatcherAssert.assertThat(
            "Fork URL is not fetched",
            ForkTest.smart("homepage", url).homeUrl().toString(),
            Matchers.is(url)
        );
    }

    @Test
    void fetchesForks() throws IOException {
        MatcherAssert.assertThat(
            "Fork counter is not fetched",
            ForkTest.smart("forks_count", 10).forks(),
            Matchers.is(10)
        );
    }

    @Test
    void fetchesStargazers() throws IOException {
        MatcherAssert.assertThat(
            "Fork counter is not fetched",
            ForkTest.smart("stargazers_count", 20).stargazers(),
            Matchers.is(20)
        );
    }

    @Test
    void fetchesWatchers() throws IOException {
        MatcherAssert.assertThat(
            "Fork counter is not fetched",
            ForkTest.smart("watchers_count", 30).watchers(),
            Matchers.is(30)
        );
    }

    @Test
    void openIssues() throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        final int issues = 10;
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("open_issues_count", issues)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Fork.Smart(fork).openIssues(),
            Matchers.is(issues)
        );
    }

    @Test
    void fetchesDefaultBranches() throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        final String master = "master";
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("default_branch", master)
                .build()
        ).when(fork).json();
        MatcherAssert.assertThat(
            "Values are not equal",
            new Fork.Smart(fork).defaultBranch(),
            Matchers.is(master)
        );
    }

    private static Fork.Smart smart(final String key, final String value)
        throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add(key, value)
                .build()
        ).when(fork).json();
        return new Fork.Smart(fork);
    }

    private static Fork.Smart smart(final String key, final int value)
        throws IOException {
        final Fork fork = Mockito.mock(Fork.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add(key, value)
                .build()
        ).when(fork).json();
        return new Fork.Smart(fork);
    }
}
