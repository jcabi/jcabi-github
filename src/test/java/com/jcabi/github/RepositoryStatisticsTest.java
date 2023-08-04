package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import javax.json.Json;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RepositoryStatistics}.
 *
 * @since 1.8.0
 * @todo #1660:90min MkRepo returns only string values from json.
 *  MkRepo returns only strings values, it is why in all tests below we expect string values,
 *  which is wrong, of course, MkRepo should return different types like ineger, double, long,
 *  etc.
 */
public class RepositoryStatisticsTest {

    @Test
    public void gathersBasicStatisticsFromRepo() throws IOException {
        MatcherAssert.assertThat(
            "We expect to have basic statistics from repo",
            new RepositoryStatistics(
                new MkGithub()
                    .repos()
                    .create(
                        new Repos.RepoCreate("volodya-lombrozo", false)
                            .with("language", Json.createValue("java"))
                            .with("forks_count", Json.createValue(1))
                            .with("stargazers_count", Json.createValue(1))
                            .with("watchers_count", Json.createValue(0))
                            .with("size", Json.createValue(2))
                            .with("open_issues_count", Json.createValue(1))
                            .with("created_at", Json.createValue("2011-01-26T19:14:43Z"))
                            .with("updated_at", Json.createValue("2011-01-26T19:01:12Z"))
                    )
            ).toMap(),
            Matchers.<Map<String, ?>>allOf(
                Matchers.hasEntry("language", "java"),
                Matchers.hasEntry("forks_count", "1"),
                Matchers.hasEntry("stargazers_count", "1"),
                Matchers.hasEntry("watchers_count", "0"),
                Matchers.hasEntry("size", "2"),
                Matchers.hasEntry("open_issues_count", "1"),
                Matchers.hasEntry(
                    "created_at",
                    "2011-01-26T19:14:43Z"
                ),
                Matchers.hasEntry(
                    "updated_at",
                    "2011-01-26T19:01:12Z"
                )
            )
        );
    }
}