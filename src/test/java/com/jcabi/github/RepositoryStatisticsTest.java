package com.jcabi.github;

import com.jcabi.github.mock.MkGithub;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RepositoryStatistics}.
 *
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
                    )
            ).toMap(),
            Matchers.allOf(
                Matchers.hasKey("language"),
                Matchers.hasKey("forks_count"),
                Matchers.hasKey("stargazers_count"),
                Matchers.hasKey("watchers_count"),
                Matchers.hasKey("size"),
                Matchers.hasKey("open_issues_count"),
                Matchers.hasKey("created_at"),
                Matchers.hasKey("updated_at")
            )
        );
    }
}