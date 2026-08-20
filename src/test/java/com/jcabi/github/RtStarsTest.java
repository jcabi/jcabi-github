/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import jakarta.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtStars}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtStarsTest {

    @Test
    void findsStarredRepo() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Starred repo is not starred",
                new RtStars(
                    new ApacheRequest(container.home()),
                    RtStarsTest.repo("someuser", "starredrepo")
                ).starred(),
                Matchers.is(true)
            );
        }
    }

    @Test
    void findsNotStarredRepo() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND)
            ).start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Not starred repo is starred",
                new RtStars(
                    new ApacheRequest(container.home()),
                    RtStarsTest.repo("otheruser", "notstarredrepo")
                ).starred(),
                Matchers.is(false)
            );
        }
    }

    @Test
    void starsRepositoryWithPut() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo("staruser", "starrepo")
            ).star();
            MatcherAssert.assertThat(
                "Repo is not starred with PUT",
                container.take().method(),
                Matchers.equalTo(Request.PUT)
            );
        }
    }

    @Test
    void starsRepositoryAtCorrectUri() throws IOException {
        final String user = "staruser";
        final String repo = "starrepo";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo(user, repo)
            ).star();
            MatcherAssert.assertThat(
                "Repo is starred at a wrong URI",
                container.take().uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath(user).path(repo).build().getPath()
                )
            );
        }
    }

    @Test
    void unstarsRepositoryWithDelete() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo("unstaruser", "unstarrepo")
            ).unstar();
            MatcherAssert.assertThat(
                "Repo is not unstarred with DELETE",
                container.take().method(),
                Matchers.equalTo(Request.DELETE)
            );
        }
    }

    @Test
    void unstarsRepositoryAtCorrectUri() throws IOException {
        final String user = "unstaruser";
        final String repo = "unstarrepo";
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo(user, repo)
            ).unstar();
            MatcherAssert.assertThat(
                "Repo is unstarred at a wrong URI",
                container.take().uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath(user).path(repo).build().getPath()
                )
            );
        }
    }

    private static Repo repo(final String user, final String reponame) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple(user, reponame))
            .when(repo).coordinates();
        return repo;
    }
}
