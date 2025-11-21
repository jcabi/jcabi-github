/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.ApacheRequest;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriBuilderException;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtStars}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
public final class RtStarsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */


    @Test
    public void checkIfRepoStarred() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .start(RandomPort.port())
        ) {
            final Stars starred = new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo("someuser", "starredrepo")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                starred.starred(), Matchers.is(true)
            );
            final Stars unstarred = new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo("otheruser", "notstarredrepo")
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                unstarred.starred(), Matchers.is(false)
            );
            container.stop();
        }
    }

    @Test
    public void starRepository() throws IOException, IllegalArgumentException, UriBuilderException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            final String user = "staruser";
            final String repo = "starrepo";
            final Stars stars = new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo(user, repo)
            );
            stars.star();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
                "String does not contain expected value",
                query.uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath(user)
                        .path(repo)
                        .build()
                        .getPath()
                )
            );
            container.stop();
        }
    }

    @Test
    public void unstarRepository()
        throws IOException, IllegalArgumentException, UriBuilderException {
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(RandomPort.port())
        ) {
            final String user = "unstaruser";
            final String repo = "unstarrepo";
            final Stars stars = new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo(user, repo)
            );
            stars.unstar();
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                "Values are not equal",
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                "String does not contain expected value",
                query.uri().getPath(),
                Matchers.containsString(
                    UriBuilder.fromPath(user)
                        .path(repo)
                        .build()
                        .getPath()
                )
            );
            container.stop();
        }
    }

    /**
     * Create and return repo for testing.
     * @param user User
     * @param reponame Repository
     * @return Repo
     */
    private static Repo repo(final String user, final String reponame) {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple(user, reponame))
            .when(repo).coordinates();
        return repo;
    }
}
