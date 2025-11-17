/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import com.jcabi.github.Reference;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for {@link MkReference}.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkReferenceTest {

    /**
     * MkReference can return its name.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsName() throws Exception {
        MatcherAssert.assertThat(
            this.reference().ref(),
            Matchers.is("refs/tags/hello")
        );
    }

    /**
     * MkReference can return its owner.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsRepo() throws Exception {
        MatcherAssert.assertThat(
            this.reference().repo(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReference can fetch json.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void fetchesJson() throws Exception {
        final Reference ref = this.reference();
        final JsonObject json = ref.json();
        MatcherAssert.assertThat(
            json.getString("ref"),
            Matchers.is("refs/tags/hello")
        );
        MatcherAssert.assertThat(
            json.getString("sha"),
            Matchers.is("testsha")
        );
    }

    /**
     * MkReference should be able to patch itself.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void patchesRef() throws Exception {
        final Reference ref = this.reference();
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", "testshaPATCH")
            .build();
        ref.patch(json);
        MatcherAssert.assertThat(
            ref.json().getString("sha"),
            Matchers.is("testshaPATCH")
        );
    }

    /**
     * Return a Reference for testing.
     * @return Reference
     * @throws Exception - if something goes wrong.
     */
    private Reference reference() throws Exception {
        return new MkGithub().randomRepo().git()
            .references().create("refs/tags/hello", "testsha");
    }
}
