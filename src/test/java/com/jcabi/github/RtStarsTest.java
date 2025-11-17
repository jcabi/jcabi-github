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
import java.net.HttpURLConnection;
import jakarta.ws.rs.core.UriBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtStars}.
 *
 */
public final class RtStarsTest {

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtStars can check if repo is starred.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void checkIfRepoStarred() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                    new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
                ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_NOT_FOUND))
                .start(this.resource.port())
        ) {
            final Stars starred = new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo("someuser", "starredrepo")
            );
            MatcherAssert.assertThat(
                starred.starred(), Matchers.is(true)
            );
            final Stars unstarred = new RtStars(
                new ApacheRequest(container.home()),
                RtStarsTest.repo("otheruser", "notstarredrepo")
            );
            MatcherAssert.assertThat(
                unstarred.starred(), Matchers.is(false)
            );
            container.stop();
        }
    }

    /**
     * RtStars can star repository.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void starRepository() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(this.resource.port());
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
                query.method(),
                Matchers.equalTo(Request.PUT)
            );
            MatcherAssert.assertThat(
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
     * RtStars can unstar repository.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void unstarRepository() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT)
            ).start(this.resource.port())
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
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
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
