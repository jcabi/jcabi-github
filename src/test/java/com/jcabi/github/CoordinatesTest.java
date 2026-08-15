/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
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
final class CoordinatesTest {

    /**
     * HTTPS coordinates of the Kafka repository.
     */
    private static final String KAFKA = "https://github.com/apache/kafka.git";

    /**
     * HTTPS coordinates of the jcabi-github repository.
     */
    private static final String JCABI =
        "https://github.com/jcabi/jcabi-github";

    @Test
    void retrievesRepoFromHttpsCoordinates() {
        MatcherAssert.assertThat(
            "Repo is retrieved incorrectly from HTTPS coordinates",
            new Coordinates.Https(
                "https://github.com/yegor256/takes.git"
            ).repo(),
            Matchers.equalTo("takes")
        );
    }

    @Test
    void retrievesUserFromHttpsCoordinates() {
        MatcherAssert.assertThat(
            "User is retrieved incorrectly from HTTPS coordinates",
            new Coordinates.Https(
                "https://github.com/yegor256/takes.git"
            ).user(),
            Matchers.equalTo("yegor256")
        );
    }

    @Test
    void sameHttpsCoordinatesAreEqual() {
        final String same = "https://github.com/apache/tomcat.git";
        MatcherAssert.assertThat(
            "Same coordinates are equal",
            new Coordinates.Https(same),
            Matchers.equalTo(new Coordinates.Https(same))
        );
    }

    @Test
    void comparesSmallerHttpsCoordinates() {
        MatcherAssert.assertThat(
            "First coordinates are not less than second",
            new Coordinates.Https(CoordinatesTest.KAFKA).compareTo(
                new Coordinates.Https(CoordinatesTest.JCABI)
            ),
            Matchers.equalTo(-9)
        );
    }

    @Test
    void comparesBiggerHttpsCoordinates() {
        MatcherAssert.assertThat(
            "Second coordinates are not greater than first",
            new Coordinates.Https(CoordinatesTest.JCABI).compareTo(
                new Coordinates.Https(CoordinatesTest.KAFKA)
            ),
            Matchers.equalTo(9)
        );
    }

    @Test
    void comparesEqualHttpsCoordinates() {
        MatcherAssert.assertThat(
            "Same HTTPS coordinates are not equal",
            new Coordinates.Https(CoordinatesTest.KAFKA).compareTo(
                new Coordinates.Https(CoordinatesTest.KAFKA)
            ),
            Matchers.equalTo(0)
        );
    }

    @Test
    void comparesSimpleAndHttpsCoordinates() {
        MatcherAssert.assertThat(
            "Coordinates should be equal",
            new Coordinates.Simple("volodya-lombrozo/jtcop").compareTo(
                new Coordinates.Https(
                    "https://github.com/volodya-lombrozo/jtcop"
                )
            ),
            Matchers.equalTo(0)
        );
    }
}
