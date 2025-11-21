/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Response;
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
import org.junit.Rule;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtSearch}.
 * @since 0.1
 * @checkstyle MultipleStringLiterals (300 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (3 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtSearchTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    @Test
    public void canSearchForRepos() {
        final String coords = "test-user1/test-repo1";
        final Search search = new RtGitHub(
            new FakeRequest().withBody(
                RtSearchTest.search(
                    Json.createObjectBuilder().add("full_name", coords).build()
                ).toString()
            )
        ).search();
        MatcherAssert.assertThat(
            "Values are not equal",
            search.repos("test", "stars", Search.Order.DESC).iterator().next()
                .coordinates().toString(),
            Matchers.equalTo(coords)
        );
    }

    @Test
    public void canSearchForIssues() {
        final int number = 1;
        final Search search = new RtGitHub(
            new FakeRequest().withBody(
                RtSearchTest.search(
                    Json.createObjectBuilder().add(
                        "url", String.format(
                            // @checkstyle LineLength (1 line)
                            "https://api.github.com/repos/user/repo/issues/%s",
                            number
                        )
                    ).add("number", number).build()
                ).toString()
            )
        ).search();
        MatcherAssert.assertThat(
            "Values are not equal",
            search.issues(
                "test2",
                "created",
                Search.Order.DESC,
                new EnumMap<>(Search.Qualifier.class)
            ).iterator().next().number(),
            Matchers.equalTo(number)
        );
    }

    @Test
    public void canSearchForUsers() throws IOException {
        final String login = "test-user";
        final Search search = new RtGitHub(
            new FakeRequest().withBody(
                RtSearchTest.search(
                    Json.createObjectBuilder()
                        .add("login", login).build()
                ).toString()
            )
        ).search();
        MatcherAssert.assertThat(
            "Values are not equal",
            search.users("test3", "joined", Search.Order.DESC)
                .iterator().next().login(),
            Matchers.equalTo(login)
        );
    }

    @Test
    public void canSearchForContents() throws IOException {
        final JsonObject first = RtSearchTest.content(
            "test/unit/attributes.js",
            "attributes.js",
            // @checkstyle LineLength (1 line)
            "https://api.github.com/repos/user/repo/contents/test/unit/attributes.js?ref=f3b89ba0820882bd4ce4404b7e7c819e7b506de5"
        ).build();
        final JsonObject second = RtSearchTest.content(
            "src/attributes/classes.js",
            "classes.js",
            // @checkstyle LineLength (1 line)
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
            final Search search = new RtGitHub(
                new ApacheRequest(container.home())
            ).search();
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                search.codes("test4", "joined", Search.Order.DESC),
                Matchers.iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtSearch can read non-unicode.
     */
    @Test
    public void readNonUnicode() throws IOException {
        final Response resp = new FakeRequest()
            .withBody("{\"help\": \"\u001Fblah\u0001cwhoa\u0000!\"}").fetch();
        final JsonResponse response = new JsonResponse(resp);
        MatcherAssert.assertThat(
            "Values are not equal",
            response.json().readObject().getString("help"),
            Matchers.is("\u001Fblah\u0001cwhoa\u0000!")
        );
    }

    /**
     * Create content JsonObject.
     * @param path Content path
     * @param name Content name
     * @param url Content url
     * @return JsonObjectBuilder
     */
    private static JsonObjectBuilder content(
        final String path, final String name, final String url) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("name", name)
            .add("url", url);
    }

    /**
     * Create search response JsonObjectBuilder.
     * @param contents Contents to add
     * @return JsonObject
     */
    private static JsonObject search(
        final JsonObject... contents) {
        final JsonArrayBuilder builder = Json.createArrayBuilder();
        for (final JsonObject content : contents) {
            builder.add(content);
        }
        return Json.createObjectBuilder()
            .add("total_count", contents.length)
            .add("items", builder).build();
    }
}
