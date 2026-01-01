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
        final RtPublicKey key = new RtPublicKey(
            new FakeRequest().withBody("{}"),
            Mockito.mock(User.class),
            1
        );
        MatcherAssert.assertThat(
            "Value is null",
            key.json(),
            Matchers.notNullValue()
        );
    }

    @Test
    void canObtainUser() {
        final User user = Mockito.mock(User.class);
        final RtPublicKey key = new RtPublicKey(new FakeRequest(), user, 2);
        MatcherAssert.assertThat(
            "Assertion failed",
            key.user(),
            Matchers.sameInstance(user)
        );
    }

    @Test
    void canObtainNumber() {
        final int number = 39;
        final RtPublicKey key = new RtPublicKey(
            new FakeRequest(),
            Mockito.mock(User.class),
            number
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            key.number(),
            Matchers.equalTo(number)
        );
    }

}
