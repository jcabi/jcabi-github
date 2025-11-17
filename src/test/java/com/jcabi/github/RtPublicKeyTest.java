/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtPublicKey}.
 *
 */
public final class RtPublicKeyTest {

    /**
     * RtPublicKey can be described as a JSON object.
     *
     */
    @Test
    public void canRepresentAsJson() throws IOException {
        final RtPublicKey key = new RtPublicKey(
            new FakeRequest().withBody("{}"),
            Mockito.mock(User.class),
            1
        );
        MatcherAssert.assertThat(
            key.json(),
            Matchers.notNullValue()
        );
    }

    /**
     * RtPublicKey can obtain its own user.
     *
     */
    @Test
    public void canObtainUser() {
        final User user = Mockito.mock(User.class);
        final RtPublicKey key = new RtPublicKey(new FakeRequest(), user, 2);
        MatcherAssert.assertThat(
            key.user(),
            Matchers.sameInstance(user)
        );
    }

    /**
     * RtPublicKey can obtain its own number.
     *
     */
    @Test
    public void canObtainNumber() {
        final int number = 39;
        final RtPublicKey key = new RtPublicKey(
            new FakeRequest(),
            Mockito.mock(User.class),
            number
        );
        MatcherAssert.assertThat(
            key.number(),
            Matchers.equalTo(number)
        );
    }

}
