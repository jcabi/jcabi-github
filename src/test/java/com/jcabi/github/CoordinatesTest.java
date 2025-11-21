/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link Coordinates}.
 * @since 0.1
 */
public final class CoordinatesTest {

    @Test
    public void retrievesUserAndRepoFromHttpsCoordinates() {
        final Coordinates coords = new Coordinates.Https(
            "https://github.com/yegor256/takes.git"
        );
        final String repo = "takes";
        MatcherAssert.assertThat(
            String.format(
                "Repo is retrieved incorrectly, we expect '%s', but was '%s'",
                repo,
                coords.repo()
            ),
            coords.repo(),
            Matchers.equalTo(repo)
        );
        final String user = "yegor256";
        MatcherAssert.assertThat(
            String.format(
                "User is retrieved incorrectly, we expect '%s', but was '%s'",
                user,
                coords.user()
            ),
            coords.user(),
            Matchers.equalTo(user)
        );
    }

    @Test
    public void sameHttpsCoordinatesAreEqual() {
        final String same = "https://github.com/apache/tomcat.git";
        MatcherAssert.assertThat(
            "Same coordinates are equal",
            new Coordinates.Https(same),
            Matchers.equalTo(new Coordinates.Https(same))
        );
    }

    @Test
    public void comparesHttpsCoordinates() {
        final String first = "https://github.com/apache/kafka.git";
        final String second = "https://github.com/jcabi/jcabi-github";
        final int difference = 9;
        MatcherAssert.assertThat(
            "First coordinates are less than second",
            new Coordinates.Https(first)
                .compareTo(new Coordinates.Https(second)),
            Matchers.equalTo(-difference)
        );
        MatcherAssert.assertThat(
            "Second coordinates are greater than first",
            new Coordinates.Https(second)
                .compareTo(new Coordinates.Https(first)),
            Matchers.equalTo(difference)
        );
        MatcherAssert.assertThat(
            "Same https coordinates are equal",
            new Coordinates.Https(first)
                .compareTo(new Coordinates.Https(first)),
            Matchers.equalTo(0)
        );
    }

    @Test
    public void comparesSimpleAndHttpsCoordinates() {
        MatcherAssert.assertThat(
            "Coordinates should be equal",
            new Coordinates.Simple("volodya-lombrozo/jtcop")
                .compareTo(
                    new Coordinates.Https(
                        "https://github.com/volodya-lombrozo/jtcop"
                    )
                ),
            Matchers.equalTo(0)
        );
    }
}
