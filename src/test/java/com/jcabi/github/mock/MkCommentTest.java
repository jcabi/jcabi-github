/**
 * Copyright (c) 2013-2024, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Tv;
import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repos;
import java.net.URL;
import java.util.Date;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link MkComment}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 */
public final class MkCommentTest {
    /**
     * MkComment can change body.
     * @throws Exception If some problem inside
     */
    @Test
    public void changesBody() throws Exception {
        final Comment comment = this.comment("hey buddy");
        new Comment.Smart(comment).body("hello, this is a new body");
        MatcherAssert.assertThat(
            new Comment.Smart(comment).body(),
            Matchers.startsWith("hello, this ")
        );
    }

    /**
     * MkComment should be able to compare different instances.
     *
     * @throws Exception when a problem occurs.
     */
    @Test
    public void canCompareInstances() throws Exception {
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
            less.compareTo(greater),
            Matchers.lessThan(0)
        );
        MatcherAssert.assertThat(
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
    public void dataStoredProperly() throws Exception {
        final String cmt = "what's up?";
        final long before = MkCommentTest.now();
        final Comment comment = this.comment(cmt);
        final long after = MkCommentTest.now();
        MatcherAssert.assertThat(
            comment.number(),
            Matchers.greaterThan(0)
        );
        final Comment.Smart smart = new Comment.Smart(comment);
        MatcherAssert.assertThat(
            smart.issue().number(),
            Matchers.greaterThan(0)
        );
        MatcherAssert.assertThat(
            smart.author().login(),
            Matchers.equalTo("jeff")
        );
        MatcherAssert.assertThat(
            smart.body(),
            Matchers.equalTo(cmt)
        );
        MatcherAssert.assertThat(
            smart.url(),
            Matchers.equalTo(
                new URL(
                    // @checkstyle LineLength (1 line)
                    "https://api.jcabi-github.invalid/repos/jeff/blueharvest/issues/comments/1"
                )
            )
        );
        MatcherAssert.assertThat(
            smart.createdAt().getTime(),
            Matchers.greaterThanOrEqualTo(before)
        );
        MatcherAssert.assertThat(
            smart.createdAt().getTime(),
            Matchers.lessThanOrEqualTo(after)
        );
        MatcherAssert.assertThat(
            smart.updatedAt().getTime(),
            Matchers.greaterThanOrEqualTo(before)
        );
        MatcherAssert.assertThat(
            smart.updatedAt().getTime(),
            Matchers.lessThanOrEqualTo(after)
        );
    }

    /**
     * Create a comment to work with.
     * @param text Text of comment
     * @return Comment just created
     * @throws Exception If some problem inside
     */
    private Comment comment(final String text) throws Exception {
        return new MkGithub().repos().create(
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
