/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Coordinates}.
 */
public final class CoordinatesTest {

    /**
     * Coordinates.Https can retrieve user and repo from https coordinates.
     */
    @Test
    public void retrievesUserAndRepoFromHttpsCoordinates() {
        final Coordinates coords = new Coordinates.Https(
            "https://github.com/yegor256/takes.git"
        );
        final String repo = "takes";
        final String user = "yegor256";
        MatcherAssert.assertThat(
            String.format(
                "Repo is retrieved incorrectly, we expect '%s', but was '%s'",
                repo,
                coords.repo()
            ),
            coords.repo(),
            Matchers.equalTo(repo)
        );
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

    /**
     * Coordinates.Https equal if they have the same url.
     */
    @Test
    public void sameHttpsCoordinatesAreEqual() {
        final String same = "https://github.com/apache/tomcat.git";
        MatcherAssert.assertThat(
            "Same coordinates are equal",
            new Coordinates.Https(same),
            Matchers.equalTo(new Coordinates.Https(same))
        );
    }

    /**
     * Coordinates.Https can be compared.
     */
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

    /**
     * Coordinates.Simple can be compared with Coordinates.Https.
     */
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
