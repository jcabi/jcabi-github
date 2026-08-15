/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPublicKey}.
 * @since 0.1
 */
final class RtPublicKeyTest {

    @Test
    void canRepresentAsJson() throws IOException {
        MatcherAssert.assertThat(
            "Value is null",
            new RtPublicKey(
                new FakeRequest().withBody("{}"),
                Mockito.mock(User.class),
                1
            ).json(),
            Matchers.notNullValue()
        );
    }

    @Test
    void canObtainUser() {
        final User user = Mockito.mock(User.class);
        MatcherAssert.assertThat(
            "Assertion failed",
            new RtPublicKey(new FakeRequest(), user, 2).user(),
            Matchers.sameInstance(user)
        );
    }

    @Test
    void canObtainNumber() {
        final int number = 39;
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtPublicKey(
                new FakeRequest(),
                Mockito.mock(User.class),
                number
            ).number(),
            Matchers.equalTo(number)
        );
    }
}
