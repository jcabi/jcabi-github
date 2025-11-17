/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DeployKey}.
 *
 * @checkstyle MultipleStringLiterals (150 lines)
 */
public final class DeployKeyTest {

    /**
     * DeployKey.Smart can update the key value of DeployKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesKey() throws Exception {
        final DeployKey key = Mockito.mock(DeployKey.class);
        final String value = "sha-rsa BBB...";
        new DeployKey.Smart(key).key(value);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("key", value).build()
        );
    }

    /**
     * DeployKey.Smart can update the title property of DeployKey.
     * @throws Exception If a problem occurs.
     */
    @Test
    public void updatesTitle() throws Exception {
        final DeployKey key = Mockito.mock(DeployKey.class);
        final String prop = "octocat@octomac";
        new DeployKey.Smart(key).title(prop);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("title", prop).build()
        );
    }

}
