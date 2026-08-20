/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.response.JsonResponse;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.IOException;
import java.util.EnumMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link RtSearch}.
 * @since 0.1
 */
@ExtendWith(RandomPort.class)
final class RtSearchTest {

    /**
     * The rule for skipping test if there's BindException.
     */
    @Test
    void canSearchForRepos() {
        final String coords = "test-user1/test-repo1";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(
                new FakeRequest().withBody(
                    RtSearchTest.search(
                        Json.createObjectBuilder().add("full_name", coords).build()
                    ).toString()
                )
            ).search().repos("test", "stars", Search.Order.DESC).iterator().next()
                .coordinates().toString(),
            Matchers.equalTo(coords)
        );
    }

    @Test
    void canSearchForIssues() {
        final int number = 1;
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(
                new FakeRequest().withBody(
                    RtSearchTest.search(
                        Json.createObjectBuilder().add(
                            "url", String.format(
                                "https://api.github.com/repos/user/repo/issues/%s",
                                number
                            )
                        ).add("number", number).build()
                    ).toString()
                )
            ).search().issues(
                "test2",
                "created",
                Search.Order.DESC,
                new EnumMap<>(Search.Qualifier.class)
            ).iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    @Test
    void canSearchForUsers() throws IOException {
        final String login = "test-user";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtGitHub(
                new FakeRequest().withBody(
                    RtSearchTest.search(
                        Json.createObjectBuilder()
                            .add("login", login).build()
                    ).toString()
                )
            ).search().users("test3", "joined", Search.Order.DESC)
                .iterator().next().login(),
            Matchers.equalTo(login)
        );
    }

    @Test
    void canSearchForContents() throws IOException {
        final JsonObject first = RtSearchTest.content(
            "test/unit/attributes.js",
            "attributes.js",
            "https://api.github.com/repos/user/repo/contents/test/unit/attributes.js?ref=f3b89ba0820882bd4ce4404b7e7c819e7b506de5"
        ).build();
        final JsonObject second = RtSearchTest.content(
            "src/attributes/classes.js",
            "classes.js",
            "https://api.github.com/repos/user/repo/contents/src/attributes/classes.js?ref=f3b89ba0820882bd4ce4404b7e7c819e7b506de5"
        ).build();
        try (
            MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    RtSearchTest.search(first, second).toString()
                )
            ).next(new MkAnswer.Simple(first.toString()))
                .next(new MkAnswer.Simple(second.toString()))
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                new RtGitHub(
                    new ApacheRequest(container.home())
                ).search().codes("test4", "joined", Search.Order.DESC),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtSearch can read non-unicode.
     */
    @Test
    void readNonUnicode() throws IOException {
        MatcherAssert.assertThat(
            "Values are not equal",
            new JsonResponse(
                new FakeRequest()
                    .withBody("{\"help\": \"\37blah\1cwhoa\0!\"}").fetch()
            ).json().readObject().getString("help"),
            Matchers.is("\37blah\1cwhoa\0!")
        );
    }

    private static JsonObjectBuilder content(
        final String path, final String name, final String url) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("name", name)
            .add("url", url);
    }

    private static JsonObject search(final JsonObject... contents) {
        final JsonArrayBuilder builder = Json.createArrayBuilder();
        for (final JsonObject content : contents) {
            builder.add(content);
        }
        return Json.createObjectBuilder()
            .add("total_count", contents.length)
            .add("items", builder).build();
    }
}
