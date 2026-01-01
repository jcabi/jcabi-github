/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.request.FakeRequest;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Bulk}.
 * @since 0.17
 */
final class BulkTest {

    @Test
    void cachesJsonData() throws IOException {
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
            "Values are not equal",
            new Comment.Smart(comment).body(),
            Matchers.equalTo("hey you")
        );
        comment.number();
        Mockito.verify(origin).number();
        Mockito.verify(origin, Mockito.never()).json();
    }

}
