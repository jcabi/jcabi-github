/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Tag;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for MkTag.
 * @since 0.1
 */
final class MkTagTest {

    /**
     * MkTag should return its json.
     * @throws Exception If something goes wrong.
     */
    @Test
    void fetchesContent() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            MkTagTest.tag().json().getString("message"),
            Matchers.is("\"test tag\"")
        );
    }

    /**
     * Return a Tag for testing.
     * @return Tag
     */
    private static Tag tag() throws IOException {
        return new MkGitHub().randomRepo().git().tags().create(
            Json.createObjectBuilder()
                .add("sha", "abcsha12").add("message", "test tag")
                .add("name", "v.0.1").build()
        );
    }
}
