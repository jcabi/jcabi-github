/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.jcabi.github.mock;

import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkTags.
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkTagsTest {

    /**
     * MkTags can create tags.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void createsMkTag() throws Exception {
        final JsonObject tagger = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "Scott@gmail.com").build();
        MatcherAssert.assertThat(
            new MkGithub().randomRepo().git().tags().create(
                Json.createObjectBuilder().add("name", "v.0.1")
                    .add("message", "test tag").add("sha", "abcsha12")
                    .add("tagger", tagger).build()
            ),
            Matchers.notNullValue()
        );
    }
}
