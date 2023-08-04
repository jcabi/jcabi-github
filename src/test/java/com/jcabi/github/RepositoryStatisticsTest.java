/**
 * Copyright (c) 2013-2023, jcabi.com
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

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import java.util.Map;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RepositoryStatistics}.
 *
 * @version $Id$
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @since 1.8.0
 * @todo #1660:90min MkRepo returns only string values from json.
 *  MkRepo returns only strings values, it is why in all tests
 *  below we expect string values, which is wrong, of course.
 *  MkRepo should return different types like integer, double, long,
 *  etc. When it is implemented, we can replace strings with concrete
 *  types and remove that puzzle.
 */
public final class RepositoryStatisticsTest {

    /**
     * Checks that RepositryStatistics can convert all values to a map.
     * @throws IOException If some problem with I/O happened.
     */
    @Test
    public void retrievesBasicStatisticsFromRepo()
        throws IOException {
        final String language = "language";
        final String forks = "forks_count";
        final String stargazers = "stargazers_count";
        final String watchers = "watchers_count";
        final String size = "size";
        final String issues = "open_issues_count";
        final String created = "created_at";
        final String updated = "updated_at";
        final String date = "2011-01-26T19:14:43Z";
        final String java = "java";
        MatcherAssert.assertThat(
            "We expect to have basic statistics from repo",
            new RepositoryStatistics(
                new MkGithub()
                    .repos()
                    .create(
                        new Repos.RepoCreate("volodya-lombrozo", false)
                            .with(language, Json.createValue(java))
                            .with(forks, Json.createValue(1))
                            .with(stargazers, Json.createValue(2))
                            .with(watchers, Json.createValue(Integer.MAX_VALUE))
                            .with(size, Json.createValue(Integer.MIN_VALUE))
                            .with(issues, Json.createValue(1 + 1 + 1))
                            .with(created, Json.createValue(date))
                            .with(updated, Json.createValue(date))
                    )
            ).toMap(),
            Matchers.<Map<String, ?>>allOf(
                Matchers.hasEntry(language, java),
                Matchers.hasEntry(forks, "1"),
                Matchers.hasEntry(stargazers, "2"),
                Matchers.hasEntry(
                    watchers,
                    String.valueOf(Integer.MAX_VALUE)
                ),
                Matchers.hasEntry(
                    size,
                    String.valueOf(Integer.MIN_VALUE)
                ),
                Matchers.hasEntry(issues, "3"),
                Matchers.hasEntry(created, date),
                Matchers.hasEntry(updated, date)
            )
        );
    }
}
