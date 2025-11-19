/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Tag;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkTag.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkTagTest {

    /**
     * MkTag should return its json.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void fetchesContent() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            this.tag().json().getString("message"),
            Matchers.is("\"test tag\"")
        );
    }

    /**
     * Return a Tag for testing.
     * @return Tag
     */
    private Tag tag() throws IOException {
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", "abcsha12").add("message", "test tag")
            .add("name", "v.0.1").build();
        return new MkGitHub().randomRepo().git().tags().create(json);
    }

}
