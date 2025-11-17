/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Unit tests for {@link MkNotification}.
 * @since 0.40
 * @checkstyle MagicNumberCheck (500 lines)
 */
public final class MkNotificationTest {
    /**
     * MkNotification can return its number.
     */
    @Test
    public void returnsNumber() {
        MatcherAssert.assertThat(
            "Assertion failed",
            new MkNotification(
                new XMLDocument(
                    new Xembler(
                        new Directives()
                            .add("notification")
                                .add("id").set("123")
                    ).xmlQuietly()
                )
            ).number(),
            Matchers.is(123L)
        );
    }
}
