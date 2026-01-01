/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link DeployKey}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (150 lines)
 */
final class DeployKeyTest {

    @Test
    void updatesKey() throws IOException {
        final DeployKey key = Mockito.mock(DeployKey.class);
        final String value = "sha-rsa BBB...";
        new DeployKey.Smart(key).key(value);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("key", value).build()
        );
    }

    @Test
    void updatesTitle() throws IOException {
        final DeployKey key = Mockito.mock(DeployKey.class);
        final String prop = "octocat@octomac";
        new DeployKey.Smart(key).title(prop);
        Mockito.verify(key).patch(
            Json.createObjectBuilder().add("title", prop).build()
        );
    }

}
