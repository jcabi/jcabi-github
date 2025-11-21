/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Reference;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for {@link MkReference}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MkReferenceTest {

    /**
     * MkReference can return its name.
     * @throws Exception - If something goes wrong.
     */
    @Test
    void returnsName() throws Exception {
        MatcherAssert.assertThat(
            "Values are not equal",
            MkReferenceTest.reference().ref(),
            Matchers.is("refs/tags/hello")
        );
    }

    /**
     * MkReference can return its owner.
     * @throws Exception - If something goes wrong.
     */
    @Test
    void returnsRepo() throws Exception {
        MatcherAssert.assertThat(
            "Value is null",
            MkReferenceTest.reference().repo(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReference can fetch json.
     * @throws Exception - If something goes wrong.
     */
    @Test
    void fetchesJson() throws Exception {
        final Reference ref = MkReferenceTest.reference();
        final JsonObject json = ref.json();
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getString("ref"),
            Matchers.is("refs/tags/hello")
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            json.getString("sha"),
            Matchers.is("testsha")
        );
    }

    /**
     * MkReference should be able to patch itself.
     * @throws Exception - If something goes wrong.
     */
    @Test
    void patchesRef() throws Exception {
        final Reference ref = MkReferenceTest.reference();
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", "testshaPATCH")
            .build();
        ref.patch(json);
        MatcherAssert.assertThat(
            "Values are not equal",
            ref.json().getString("sha"),
            Matchers.is("testshaPATCH")
        );
    }

    /**
     * Return a Reference for testing.
     * @return Reference
     */
    private static Reference reference() throws IOException {
        return new MkGitHub().randomRepo().git()
            .references().create("refs/tags/hello", "testsha");
    }
}
