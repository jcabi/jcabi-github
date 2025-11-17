/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Comment;
import com.jcabi.github.Comments;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkComments}.
 */
public final class MkCommentsTest {

    /**
     * MkComments can iterate comments.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesComments() throws Exception {
        final Comments comments = this.comments();
        comments.post("hello, dude!");
        comments.post("hello again");
        MatcherAssert.assertThat(
            comments.iterate(new Date(0L)),
            Matchers.<Comment>iterableWithSize(2)
        );
    }

    /**
     * Create a comments to work with.
     * @return Comments just created
     * @throws Exception If some problem inside
     */
    private Comments comments() throws Exception {
        return new MkGithub().randomRepo()
            .issues().create("hey", "how are you?")
            .comments();
    }
}
