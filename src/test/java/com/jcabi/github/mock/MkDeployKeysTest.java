/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.DeployKey;
import com.jcabi.github.DeployKeys;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkDeployKeys}.
 * @since 0.8
 */
final class MkDeployKeysTest {

    @Test
    void canFetchEmptyListOfDeployKeys() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            new MkGitHub().randomRepo().keys().iterate(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void canFetchSingleDeployKey() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        final DeployKey key = keys.create("Title", "Key");
        MatcherAssert.assertThat(
            "Values are not equal",
            keys.get(key.number()),
            Matchers.equalTo(key)
        );
    }

    @Test
    void canCreateDeployKey() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        final DeployKey key = keys.create("Title1", "Key1");
        MatcherAssert.assertThat(
            "Values are not equal",
            key,
            Matchers.equalTo(keys.get(key.number()))
        );
    }

    /**
     * MkDeployKeys can create distinct deploy keys.
     * Reproduces bug described in issue #346.
     */
    @Test
    void canCreateDistinctDeployKeys() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        MatcherAssert.assertThat(
            "Deploy keys are not distinct",
            keys.create("Title2", "Key2"),
            Matchers.not(Matchers.is(keys.create("Title3", "Key3")))
        );
    }

    /**
     * MkDeployKeys can number deploy keys distinctly.
     * Reproduces bug described in issue #346.
     */
    @Test
    void canNumberDeployKeysDistinctly() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        MatcherAssert.assertThat(
            "Deploy keys have equal numbers",
            keys.create("Title2", "Key2").number(),
            Matchers.not(Matchers.is(keys.create("Title3", "Key3").number()))
        );
    }

    /**
     * MkDeployKeys can be represented in JSON format.
     * Reproduces bug described in issue #346.
     */
    @Test
    void canRepresentAsJson() throws IOException {
        MatcherAssert.assertThat(
            "String does not contain expected value",
            new MkGitHub().randomRepo().keys()
                .create("Title4", "Key4").json().toString(),
            Matchers.allOf(
                Matchers.containsString("\"title\":\"Title4\""),
                Matchers.containsString("\"key\":\"Key4\"")
            )
        );
    }
}
