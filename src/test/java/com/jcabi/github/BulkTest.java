/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.request.FakeRequest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Bulk}.
 */
public final class BulkTest {

    /**
     * Bulk can cache JSON data.
     * @throws Exception If some problem inside
     */
    @Test
    public void cachesJsonData() throws Exception {
        final Comment origin = Mockito.mock(Comment.class);
        final Request request = new FakeRequest()
            .withBody("[{\"body\": \"hey you\"}]");
        final Iterable<Comment> comments = new Bulk<>(
            new RtPagination<>(
                request,
                object -> origin
            )
        );
        final Comment comment = comments.iterator().next();
        MatcherAssert.assertThat(
            new Comment.Smart(comment).body(),
            Matchers.equalTo("hey you")
        );
        comment.number();
        Mockito.verify(origin).number();
        Mockito.verify(origin, Mockito.never()).json();
    }

}
