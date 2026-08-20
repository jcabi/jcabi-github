/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Reference;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Testcase for {@link MkReference}.
 * @since 0.1
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
        MatcherAssert.assertThat(
            "Reference has a wrong name in JSON",
            MkReferenceTest.reference().json().getString("ref"),
            Matchers.is("refs/tags/hello")
        );
    }

    /**
     * MkReference can fetch its own SHA.
     * @throws Exception - If something goes wrong.
     */
    @Test
    void fetchesSha() throws Exception {
        MatcherAssert.assertThat(
            "Reference has a wrong SHA in JSON",
            MkReferenceTest.reference().json().getString("sha"),
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
        ref.patch(
            Json.createObjectBuilder()
                .add("sha", "testshaPATCH")
                .build()
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            ref.json().getString("sha"),
            Matchers.is("testshaPATCH")
        );
    }

    private static Reference reference() throws IOException {
        return new MkGitHub().randomRepo().git()
            .references().create("refs/tags/hello", "testsha");
    }
}
