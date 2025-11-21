/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtDeployKeys}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.ADMIN_PUBLIC_KEY)
final class RtDeployKeysITCase {

    /**
     * Test repos.
     */
    private static Repos repos;

    /**
     * Test repo.
     */
    private static Repo repo;

    /**
     * Set up test fixtures.
     */
    @BeforeAll
    static void setUp() throws IOException {
        final GitHub github = GitHubIT.connect();
        RtDeployKeysITCase.repos = github.repos();
        RtDeployKeysITCase.repo = new RepoRule().repo(RtDeployKeysITCase.repos);
    }

    /**
     * Tear down test fixtures.
     */
    @AfterAll
    static void tearDown() throws IOException {
        if (RtDeployKeysITCase.repos != null && RtDeployKeysITCase.repo != null) {
            RtDeployKeysITCase.repos.remove(RtDeployKeysITCase.repo.coordinates());
        }
    }

    /**
     * RtDeployKeys can iterate deploy keys.
     * @throws Exception If some problem inside
     */
    @Test
    void canFetchAllDeployKeys() throws Exception {
        final DeployKeys keys = RtDeployKeysITCase.repo.keys();
        final String title = "Test Iterate Key";
        final DeployKey key = keys.create(title, RtDeployKeysITCase.key());
        try {
            MatcherAssert.assertThat(
                "Collection does not contain expected item",
                keys.iterate(),
                Matchers.hasItem(key)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can create a deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    void createsDeployKey() throws Exception {
        final DeployKeys keys = RtDeployKeysITCase.repo.keys();
        final String title = "Test Create Key";
        final DeployKey key = keys.create(title, RtDeployKeysITCase.key());
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                new DeployKey.Smart(key).title(),
                Matchers.is(title)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can get a single deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    void getsDeployKey() throws Exception {
        final DeployKeys keys = RtDeployKeysITCase.repo.keys();
        final String title = "Test Get Key";
        final DeployKey key = keys.create(title, RtDeployKeysITCase.key());
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                keys.get(key.number()),
                Matchers.is(key)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can remove a deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    void removesDeployKey() throws Exception {
        final DeployKeys keys = RtDeployKeysITCase.repo.keys();
        final String title = "Test Remove Key";
        final DeployKey key = keys.create(title, RtDeployKeysITCase.key());
        try {
            MatcherAssert.assertThat(
                "Value is null",
                keys.get(key.number()),
                Matchers.notNullValue()
            );
        } finally {
            key.remove();
        }
        MatcherAssert.assertThat(
            "Assertion failed",
            keys.iterate(),
            Matchers.not(Matchers.contains(key))
        );
    }

    /**
     * Generates a random public key for test.
     *
     * @return The encoded SSH public key.
     */
    private static String key() throws JSchException, IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            final KeyPair kpair = KeyPair.genKeyPair(new JSch(), KeyPair.DSA);
            kpair.writePublicKey(stream, "");
            kpair.dispose();
            return new String(stream.toByteArray());
        }
    }
}
