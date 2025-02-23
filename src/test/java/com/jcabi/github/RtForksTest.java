/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtForks}.
 *
 */
public final class RtForksTest {

    /**
     * Fork's organization name in JSON object.
     */
    public static final String ORGANIZATION = "organization";

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtForks should be able to iterate its forks.
     *
     */
    @Test
    public void retrievesForks() {
        final RtForks forks = new RtForks(
            new FakeRequest()
                .withBody("[]"), this.repo()
        );
        MatcherAssert.assertThat(
            forks.iterate("newest"),
            Matchers.<Fork>iterableWithSize(0)
        );
    }

    /**
     * RtForks should be able to create a new fork.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void createsFork() throws Exception {
        final String organization = RandomStringUtils.randomAlphanumeric(10);
        final MkAnswer answer = new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            fork(organization).toString()
        );
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_ACCEPTED,
                    fork(organization).toString()
                )
            ).next(answer).start(this.resource.port())) {
            final Repo owner = Mockito.mock(Repo.class);
            final Coordinates coordinates = new Coordinates.Simple(
                "test_user", "test_repo"
            );
            Mockito.doReturn(coordinates).when(owner).coordinates();
            final RtForks forks = new RtForks(
                new JdkRequest(container.home()),
                owner
            );
            final Fork fork = forks.create(organization);
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                fork.json().getString(ORGANIZATION),
                Matchers.equalTo(organization)
            );
        }
    }

    /**
     * Create and return repo for testing.
     *
     * @return Repo
     */
    private Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "forks"))
            .when(repo).coordinates();
        return repo;
    }

    /**
     * Create and return JsonObject to test.
     * @param organization The organization of the fork
     * @return JsonObject
     */
    private static JsonObject fork(
        final String organization) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add(ORGANIZATION, organization)
            .build();
    }
}
