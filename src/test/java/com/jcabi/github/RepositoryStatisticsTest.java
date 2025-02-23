/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RepositoryStatistics}.
 *
 * @since 1.8.0
 * @todo #1660:90min MkRepo returns only string values from json.
 *  MkRepo returns only strings values, it is why in all tests
 *  below we expect string values, which is wrong, of course.
 *  MkRepo should return different types like integer, double, long,
 *  etc. When it is implemented, we can replace strings with concrete
 *  types and remove that puzzle.
 * @todo #1663:90min Refactor RepositoryStatisticsTest.
 *  RepositoryStatisticsTest has too many boilerplate code. Also it
 *  has repeated variables and constants. Refactor it to make it more
 *  readable and maintainable. Moreover all this variables don't follow
 *  PMD and Checkstyle rules. When it is done, remove this puzzle and
 *  all the checkstyle and PMD suppressions.
 * @checkstyle StaticVariableNameCheck (1000 lines)
 * @checkstyle MagicNumberCheck (1000 lines)
 * @checkstyle LineLengthCheck (2 lines)
 */
@SuppressWarnings({"PMD.SuspiciousConstantFieldName", "PMD.VariableNamingConventions"})
public final class RepositoryStatisticsTest {

    /**
     * Forks key in JSON.
     */
    private static final String FORKS_KEY = "forks_count";

    /**
     * Forks JSON value.
     */
    private static final int FORKS_VALUE = 1;

    /**
     * Language key in JSON.
     */
    private static final String LANGUAGE_KEY = "language";

    /**
     * Language value in JSON.
     */
    private static String LANGUAGE_VALUE = "java";

    /**
     * Stargazers key in JSON.
     */
    private static String STARGAZERS_KEY = "stargazers_count";

    /**
     * Stargazers value in JSON.
     */
    private static int STARGAZERS_VALUE = 2;

    /**
     * Watchers key in JSON.
     */
    private static String WATCHERS_KEY = "watchers_count";

    /**
     * Watchers value in JSON.
     */
    private static int WATCHERS_VALUE = 3;

    /**
     * Size key in JSON.
     */
    private static String SIZE_KEY = "size";

    /**
     * Size value in JSON.
     */
    private static int SIZE_VALUE = 4;

    /**
     * Issues key in JSON.
     */
    private static String ISSUES_KEY = "open_issues_count";

    /**
     * Issues value in JSON.
     */
    private static int ISSUES_VALUE = 5;

    /**
     * Created key in JSON.
     */
    private static String CREATED_KEY = "created_at";

    /**
     * Created value in JSON.
     */
    private static String CREATED_VALUE = "2011-01-26T19:14:43Z";

    /**
     * Updated key in JSON.
     */
    private static String UPDATED_KEY = "updated_at";

    /**
     * Updated value in JSON.
     */
    private static String UPDATED_VALUE = "2012-01-26T19:14:43Z";

    /**
     * Checks that RepositryStatistics can convert all values to a map.
     * @throws IOException If some problem with I/O happened.
     */
    @Test
    public void retrievesBasicStatisticsFromRepo()
        throws IOException {
        MatcherAssert.assertThat(
            "We expect to have basic statistics from repo",
            new RepositoryStatistics(this.repo()).toMap(),
            Matchers.<Map<String, ?>>allOf(
                Matchers.hasEntry(
                    RepositoryStatisticsTest.LANGUAGE_KEY,
                    RepositoryStatisticsTest.LANGUAGE_VALUE
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.FORKS_KEY,
                    String.valueOf(
                        RepositoryStatisticsTest.FORKS_VALUE
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.STARGAZERS_KEY,
                    String.valueOf(
                        RepositoryStatisticsTest.STARGAZERS_VALUE
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.WATCHERS_KEY,
                    String.valueOf(
                        RepositoryStatisticsTest.WATCHERS_VALUE
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.SIZE_KEY,
                    String.valueOf(
                        RepositoryStatisticsTest.SIZE_VALUE
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.ISSUES_KEY,
                    String.valueOf(
                        RepositoryStatisticsTest.ISSUES_VALUE
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.CREATED_KEY,
                    RepositoryStatisticsTest.CREATED_VALUE
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.UPDATED_KEY,
                    RepositoryStatisticsTest.UPDATED_VALUE
                )
            )
        );
    }

    /**
     * Checks that RepositryStatistics.Smart can retrieve all values.
     * @throws IOException If some problem with I/O happened.
     */
    @Test
    public void retrievesSmartStatistics() throws IOException {
        final RepositoryStatistics.Smart smart =
            new RepositoryStatistics.Smart(this.repo());
        MatcherAssert.assertThat(
            "Forks should be equal to 1",
            smart.forks(),
            Matchers.equalTo(
                RepositoryStatisticsTest.FORKS_VALUE
            )
        );
        MatcherAssert.assertThat(
            "Stargazers should be equal to 2",
            smart.stargazers(),
            Matchers.equalTo(
                RepositoryStatisticsTest.STARGAZERS_VALUE
            )
        );
        MatcherAssert.assertThat(
            "Watchers should be equal to 3",
            smart.watchers(),
            Matchers.equalTo(
                RepositoryStatisticsTest.WATCHERS_VALUE
            )
        );
        MatcherAssert.assertThat(
            "Size should be equal to 4",
            smart.size(),
            Matchers.equalTo(
                RepositoryStatisticsTest.SIZE_VALUE
            )
        );
        MatcherAssert.assertThat(
            "Issues should be equal to 5",
            smart.openIssues(),
            Matchers.equalTo(
                RepositoryStatisticsTest.ISSUES_VALUE
            )
        );
        MatcherAssert.assertThat(
            "Created date should be equal to 2011-01-26T19:14:43Z",
            smart.created(),
            Matchers.equalTo(
                ZonedDateTime.parse(
                    RepositoryStatisticsTest.CREATED_VALUE
                )
            )
        );
    }

    /**
     * Creates mock repo.
     * @return Repo
     * @throws IOException If some problem with I/O happened.
     */
    private Repo repo() throws IOException {
        return new MkGithub()
            .repos()
            .create(
                new Repos.RepoCreate("volodya-lombrozo", false)
                    .with(
                        RepositoryStatisticsTest.LANGUAGE_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.LANGUAGE_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.FORKS_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.FORKS_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.STARGAZERS_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.STARGAZERS_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.WATCHERS_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.WATCHERS_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.SIZE_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.SIZE_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.ISSUES_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.ISSUES_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.CREATED_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.CREATED_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.UPDATED_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.UPDATED_VALUE
                        )
                    )
            );
    }
}
