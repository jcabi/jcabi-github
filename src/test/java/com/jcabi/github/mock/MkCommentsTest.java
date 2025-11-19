/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Comments;
import java.io.IOException;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkComments}.
 * @since 0.7
 */
public final class MkCommentsTest {

    /**
     * MkComments can iterate comments.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesComments() throws Exception {
        final Comments comments = MkCommentsTest.comments();
        comments.post("hello, dude!");
        comments.post("hello again");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            comments.iterate(new Date(0L)),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * Create a comments to work with.
     * @return Comments just created
     */
    private static Comments comments() throws IOException {
        return new MkGitHub().randomRepo()
            .issues().create("hey", "how are you?")
            .comments();
    }
}
