/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repos;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
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
     * Body of the comment.
     */
    private static final String BODY = "what's up?";

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
    void comparesSmallerComment() throws IOException {
        MatcherAssert.assertThat(
            "Smaller comment is not smaller",
            MkCommentTest.comment(1).compareTo(MkCommentTest.comment(2)),
            Matchers.lessThan(0)
        );
    }

    @Test
    void comparesBiggerComment() throws IOException {
        MatcherAssert.assertThat(
            "Bigger comment is not bigger",
            MkCommentTest.comment(2).compareTo(MkCommentTest.comment(1)),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkComment stores its own number.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesNumber() throws Exception {
        MatcherAssert.assertThat(
            "Comment has a wrong number",
            MkCommentTest.comment(MkCommentTest.BODY).number(),
            Matchers.greaterThan(0L)
        );
    }

    /**
     * MkComment stores the issue it belongs to.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesIssue() throws Exception {
        MatcherAssert.assertThat(
            "Comment belongs to a wrong issue",
            MkCommentTest.smart().issue().number(),
            Matchers.greaterThan(0)
        );
    }

    /**
     * MkComment stores its own author.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesAuthor() throws Exception {
        MatcherAssert.assertThat(
            "Comment has a wrong author",
            MkCommentTest.smart().author().login(),
            Matchers.equalTo("jeff")
        );
    }

    /**
     * MkComment stores its own body.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesBody() throws Exception {
        MatcherAssert.assertThat(
            "Comment has a wrong body",
            MkCommentTest.smart().body(),
            Matchers.equalTo(MkCommentTest.BODY)
        );
    }

    /**
     * MkComment stores its own URL.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesUrl() throws Exception {
        MatcherAssert.assertThat(
            "Comment has a wrong URL",
            MkCommentTest.smart().url(),
            Matchers.equalTo(
                new URI(
                    "https://api.jcabi-github.invalid/repos/jeff/blueharvest/issues/comments/1"
                ).toURL()
            )
        );
    }

    /**
     * MkComment stores the moment of its own creation.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesCreationTime() throws Exception {
        MatcherAssert.assertThat(
            "Comment is created at a wrong moment",
            MkCommentTest.smart().createdAt().toEpochMilli(),
            Matchers.allOf(
                Matchers.greaterThanOrEqualTo(
                    MkCommentTest.now() - TimeUnit.MINUTES.toMillis(1L)
                ),
                Matchers.lessThanOrEqualTo(MkCommentTest.now())
            )
        );
    }

    /**
     * MkComment stores the moment of its own update.
     * @throws Exception when a problem occurs.
     */
    @Test
    void storesUpdateTime() throws Exception {
        MatcherAssert.assertThat(
            "Comment is updated at a wrong moment",
            MkCommentTest.smart().updatedAt().toEpochMilli(),
            Matchers.allOf(
                Matchers.greaterThanOrEqualTo(
                    MkCommentTest.now() - TimeUnit.MINUTES.toMillis(1L)
                ),
                Matchers.lessThanOrEqualTo(MkCommentTest.now())
            )
        );
    }

    private static Comment comment(final String text) throws IOException {
        return new MkGitHub().repos().create(
            new Repos.RepoCreate("blueharvest", false)
        ).issues().create("hey", "how are you?").comments().post(text);
    }

    private static MkComment comment(final int number) throws IOException {
        return new MkComment(
            new MkStorage.InFile(),
            String.format("login-%d", number),
            Mockito.mock(Coordinates.class),
            number,
            number
        );
    }

    private static Comment.Smart smart() throws IOException {
        return new Comment.Smart(MkCommentTest.comment(MkCommentTest.BODY));
    }

    private static long now() {
        final long sinceepoch = Instant.now().toEpochMilli();
        return sinceepoch - sinceepoch % 1000;
    }
}
