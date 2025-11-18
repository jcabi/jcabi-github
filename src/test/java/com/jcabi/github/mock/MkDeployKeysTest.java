/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.DeployKey;
import com.jcabi.github.DeployKeys;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkDeployKeys}.
 * @since 0.8
 */
public final class MkDeployKeysTest {
    /**
     * MkDeployKeys can fetch empty list of deploy keys.
     */
    @Test
    public void canFetchEmptyListOfDeployKeys() throws IOException {
        final DeployKeys deployKeys = new MkGitHub().randomRepo().keys();
        MatcherAssert.assertThat(
            "Collection is not empty",
            deployKeys.iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkDeployKeys can fetch a single deploy key.
     */
    @Test
    public void canFetchSingleDeployKey() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        final DeployKey key = keys.create("Title", "Key");
        MatcherAssert.assertThat(
            "Values are not equal", keys.get(key.number()), Matchers.equalTo(key));
    }

    /**
     * MkDeployKeys can create a deploy key.
     */
    @Test
    public void canCreateDeployKey() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        final DeployKey key = keys.create("Title1", "Key1");
        MatcherAssert.assertThat(
            "Values are not equal", key, Matchers.equalTo(keys.get(key.number())));
    }

    /**
     * MkDeployKeys can create distinct deploy keys.
     * Reproduces bug described in issue #346.
     */
    @Test
    public void canCreateDistinctDeployKeys() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        final DeployKey first = keys.create("Title2", "Key2");
        final DeployKey second = keys.create("Title3", "Key3");
        MatcherAssert.assertThat(
            "Values are not equal",
            first,
            Matchers.not(Matchers.is(second))
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            first.number(),
            Matchers.not(Matchers.is(second.number()))
        );
    }

    /**
     * MkDeployKeys can be represented in JSON format.
     * Reproduces bug described in issue #346.
     */
    @Test
    public void canRepresentAsJson() throws IOException {
        final DeployKeys keys = new MkGitHub().randomRepo().keys();
        final DeployKey first = keys.create("Title4", "Key4");
        MatcherAssert.assertThat(
            "String does not contain expected value",
            first.json().toString(),
            Matchers.allOf(
                Matchers.containsString("\"title\":\"Title4\""),
                Matchers.containsString("\"key\":\"Key4\"")
            )
        );
    }
}
