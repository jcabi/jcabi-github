/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.mock.MkGitHub;
import jakarta.json.Json;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RepositoryStatistics}.
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
final class RepositoryStatisticsTest {

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
    private static String languageValue = "java";

    /**
     * Stargazers key in JSON.
     */
    private static String stargazersKey = "stargazers_count";

    /**
     * Stargazers value in JSON.
     */
    private static int stargazersValue = 2;

    /**
     * Watchers key in JSON.
     */
    private static String watchersKey = "watchers_count";

    /**
     * Watchers value in JSON.
     */
    private static int watchersValue = 3;

    /**
     * Size key in JSON.
     */
    private static String sizeKey = "size";

    /**
     * Size value in JSON.
     */
    private static int sizeValue = 4;

    /**
     * Issues key in JSON.
     */
    private static String issuesKey = "open_issues_count";

    /**
     * Issues value in JSON.
     */
    private static int issuesValue = 5;

    /**
     * Created key in JSON.
     */
    private static String createdKey = "created_at";

    /**
     * Created value in JSON.
     */
    private static String createdValue = "2011-01-26T19:14:43Z";

    /**
     * Updated key in JSON.
     */
    private static String updatedKey = "updated_at";

    /**
     * Updated value in JSON.
     */
    private static String updatedValue = "2012-01-26T19:14:43Z";

    /**
     * Checks that RepositoryStatistics can convert all values to a map.
     * @throws IOException If some problem with I/O happened.
     */
    @Test
    void retrievesBasicStatisticsFromRepo()
        throws IOException {
        MatcherAssert.assertThat(
            "We expect to have basic statistics from repo",
            new RepositoryStatistics(RepositoryStatisticsTest.repo()).toMap(),
            Matchers.<Map<String, ?>>allOf(
                Matchers.hasEntry(
                    RepositoryStatisticsTest.LANGUAGE_KEY,
                    RepositoryStatisticsTest.languageValue
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.FORKS_KEY,
                    String.valueOf(
                        RepositoryStatisticsTest.FORKS_VALUE
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.stargazersKey,
                    String.valueOf(
                        RepositoryStatisticsTest.stargazersValue
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.watchersKey,
                    String.valueOf(
                        RepositoryStatisticsTest.watchersValue
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.sizeKey,
                    String.valueOf(
                        RepositoryStatisticsTest.sizeValue
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.issuesKey,
                    String.valueOf(
                        RepositoryStatisticsTest.issuesValue
                    )
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.createdKey,
                    RepositoryStatisticsTest.createdValue
                ),
                Matchers.hasEntry(
                    RepositoryStatisticsTest.updatedKey,
                    RepositoryStatisticsTest.updatedValue
                )
            )
        );
    }

    /**
     * Checks that RepositoryStatistics.Smart can retrieve all values.
     * @throws IOException If some problem with I/O happened.
     */
    @Test
    void retrievesSmartStatistics() throws IOException {
        final RepositoryStatistics.Smart smart =
            new RepositoryStatistics.Smart(RepositoryStatisticsTest.repo());
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
                RepositoryStatisticsTest.stargazersValue
            )
        );
        MatcherAssert.assertThat(
            "Watchers should be equal to 3",
            smart.watchers(),
            Matchers.equalTo(
                RepositoryStatisticsTest.watchersValue
            )
        );
        MatcherAssert.assertThat(
            "Size should be equal to 4",
            smart.size(),
            Matchers.equalTo(
                RepositoryStatisticsTest.sizeValue
            )
        );
        MatcherAssert.assertThat(
            "Issues should be equal to 5",
            smart.openIssues(),
            Matchers.equalTo(
                RepositoryStatisticsTest.issuesValue
            )
        );
        MatcherAssert.assertThat(
            "Created date should be equal to 2011-01-26T19:14:43Z",
            smart.created(),
            Matchers.equalTo(
                ZonedDateTime.parse(
                    RepositoryStatisticsTest.createdValue
                )
            )
        );
    }

    /**
     * Creates mock repo.
     * @return Repo
     * @throws IOException If some problem with I/O happened.
     */
    private static Repo repo() throws IOException {
        return new MkGitHub()
            .repos()
            .create(
                new Repos.RepoCreate("volodya-lombrozo", false)
                    .with(
                        RepositoryStatisticsTest.LANGUAGE_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.languageValue
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.FORKS_KEY,
                        Json.createValue(
                            RepositoryStatisticsTest.FORKS_VALUE
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.stargazersKey,
                        Json.createValue(
                            RepositoryStatisticsTest.stargazersValue
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.watchersKey,
                        Json.createValue(
                            RepositoryStatisticsTest.watchersValue
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.sizeKey,
                        Json.createValue(
                            RepositoryStatisticsTest.sizeValue
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.issuesKey,
                        Json.createValue(
                            RepositoryStatisticsTest.issuesValue
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.createdKey,
                        Json.createValue(
                            RepositoryStatisticsTest.createdValue
                        )
                    )
                    .with(
                        RepositoryStatisticsTest.updatedKey,
                        Json.createValue(
                            RepositoryStatisticsTest.updatedValue
                        )
                    )
            );
    }
}
