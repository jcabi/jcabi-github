/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repos;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkComment}.
 * @since 0.1
 */
final class MkCommentTest {
    /**
     * MkComment can change body.
     * @throws Exception If some problem inside
     */
    @Test
    void changesBody() throws Exception {
        final Comment comment = MkCommentTest.comment("hey buddy");
        new Comment.Smart(comment).body("hello, this is a new body");
        MatcherAssert.assertThat(
            "String does not start with expected value",
            new Comment.Smart(comment).body(),
            Matchers.startsWith("hello, this ")
        );
    }

    @Test
    void canCompareInstances() throws IOException {
        final MkComment less = new MkComment(
            new MkStorage.InFile(),
            "login-less",
            Mockito.mock(Coordinates.class),
            1,
            1
        );
        final MkComment greater = new MkComment(
            new MkStorage.InFile(),
            "login-greater",
            Mockito.mock(Coordinates.class),
            2,
            2
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            greater.compareTo(less),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkComment should store all its data properly.
     * We should get the proper data back when accessing its properties.
     * @throws Exception when a problem occurs.
     */
    @Test
    void dataStoredProperly() throws Exception {
        final String cmt = "what's up?";
        final long before = MkCommentTest.now();
        final Comment comment = MkCommentTest.comment(cmt);
        final long after = MkCommentTest.now();
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            comment.number(),
            Matchers.greaterThan(0L)
        );
        final Comment.Smart smart = new Comment.Smart(comment);
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            smart.issue().number(),
            Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.author().login(),
            Matchers.equalTo("jeff")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.body(),
            Matchers.equalTo(cmt)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            smart.url(),
            Matchers.equalTo(
                new URI(
                    // @checkstyle LineLength (1 line)
                    "https://api.jcabi-github.invalid/repos/jeff/blueharvest/issues/comments/1"
                ).toURL()
            )
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            smart.createdAt().getTime(),
            Matchers.greaterThanOrEqualTo(before)
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            smart.createdAt().getTime(),
            Matchers.lessThanOrEqualTo(after)
        );
        MatcherAssert.assertThat(
            "Value is not greater than expected",
            smart.updatedAt().getTime(),
            Matchers.greaterThanOrEqualTo(before)
        );
        MatcherAssert.assertThat(
            "Value is not less than expected",
            smart.updatedAt().getTime(),
            Matchers.lessThanOrEqualTo(after)
        );
    }

    /**
     * Create a comment to work with.
     * @param text Text of comment
     * @return Comment just created
     */
    private static Comment comment(final String text) throws IOException {
        return new MkGitHub().repos().create(
            new Repos.RepoCreate("blueharvest", false)
        ).issues().create("hey", "how are you?").comments().post(text);
    }

    /**
     * Obtains the current time.
     * @return Current time (in milliseconds since epoch) truncated to the nearest second
     */
    private static long now() {
        final long sinceepoch = new Date().getTime();
        return sinceepoch - sinceepoch % Tv.THOUSAND;
    }
}
