/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtForks}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtForksTest {

    /**
     * Fork's organization name in JSON object.
     */
    static final String ORGANIZATION = "organization";

    @Test
    void retrievesForks() {
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            new RtForks(
                new FakeRequest()
                    .withBody("[]"), RtForksTest.repo()
            ).iterate("newest"),
            Matchers.iterableWithSize(0)
        );
    }

    @Test
    void createsForkWithPost() throws IOException {
        final String organization =
            RandomStringUtils.secure().nextAlphanumeric(10);
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtForksTest.answer(organization))
                .start(RandomPort.port())
        ) {
            RtForksTest.forks(container).create(organization);
            MatcherAssert.assertThat(
                "Fork is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void createsForkForOrganization() throws IOException {
        final String organization =
            RandomStringUtils.secure().nextAlphanumeric(10);
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtForksTest.answer(organization))
                .next(RtForksTest.fetched(organization))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Fork belongs to a wrong organization",
                RtForksTest.forks(container).create(organization)
                    .json().getString(RtForksTest.ORGANIZATION),
                Matchers.equalTo(organization)
            );
        }
    }

    private static RtForks forks(final MkContainer container)
        throws IOException {
        final Repo owner = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test_user", "test_repo"))
            .when(owner).coordinates();
        return new RtForks(new JdkRequest(container.home()), owner);
    }

    private static MkAnswer answer(final String organization) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_ACCEPTED,
            RtForksTest.fork(organization).toString()
        );
    }

    private static MkAnswer fetched(final String organization) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtForksTest.fork(organization).toString()
        );
    }

    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "forks"))
            .when(repo).coordinates();
        return repo;
    }

    private static JsonObject fork(final String organization) {
        return Json.createObjectBuilder()
            .add("id", 1)
            .add(RtForksTest.ORGANIZATION, organization)
            .build();
    }
}
