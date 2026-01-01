/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Tests for {@link MkHook}.
 * @since 0.42
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MkHookTest {
    /**
     * Test if {@link MkHook} is being created with the correct number.
     */
    @Test
    void createWithCorrectNumber() {
        final int number = 5;
        MatcherAssert.assertThat(
            "Hook returned wrong number",
            new MkHook(null, null, null, number).number(),
            new IsEqual<>(number)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct repository.
     */
    @Test
    void createWithCorrectRepo() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        final String login = "login";
        final Coordinates coords = new Coordinates.Simple("user/repo");
        MatcherAssert.assertThat(
            "Hook returned wrong repository",
            new MkHook(storage, login, coords, 0).repo(),
            new IsEqual<>(new MkRepo(storage, login, coords))
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct id.
     */
    @Test
    void createWithCorrectId() throws IOException {
        final int number = 5;
        final MkStorage storage = new MkStorage.InFile();
        final Coordinates coords = new Coordinates.Simple("user/repo");
        storage.apply(
            MkHookTest.hookDirs(number, coords)
        );
        MatcherAssert.assertThat(
            "Hook json returned wrong id",
            new MkHook(storage, "login", coords, number).json().getString("id"),
            new IsEqual<>(String.valueOf(number))
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct url.
     */
    @Test
    void createWithCorrectUrl() throws IOException {
        final String url = "https://github.com/user/repo/hooks/hook/5";
        final int number = 5;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            MkHookTest.hookDirs(number, coords)
                .add("url").set(url).up()
        );
        MatcherAssert.assertThat(
            "Hook json returned wrong url",
            new MkHook(
                storage, "login", coords, number
            ).json().getString("url"),
            new IsEqual<>(url)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct test url.
     */
    @Test
    void createWithCorrectTestUrl() throws IOException {
        final String test = "https://github.com/user/repo/hooks/hook/5";
        final int number = 5;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            MkHookTest.hookDirs(number, coords)
                .add("test_url").set(test).up()
        );
        MatcherAssert.assertThat(
            "Hook json returned wrong test_url",
            new MkHook(
                storage, "login", coords, number
            ).json().getString("test_url"),
            new IsEqual<>(test)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct ping url.
     */
    @Test
    void createWithCorrectPingUrl() throws IOException {
        final String ping = "https://github.com/user/repo/hooks/hook/5";
        final int number = 5;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            MkHookTest.hookDirs(number, coords)
                .add("ping_url").set(ping).up()
        );
        MatcherAssert.assertThat(
            "Hook json returned wrong ping_url",
            new MkHook(
                storage, "login", coords, number
            ).json().getString("ping_url"),
            new IsEqual<>(ping)
        );
    }

    /**
     * MkHook.json() should return the "events" json array with the given
     * event names.
     */
    @Test
    void createWithCorrectEvents() throws IOException {
        final Iterable<String> events = Arrays.asList("event1", "event2");
        final int number = 123;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final Directives xml = MkHookTest.hookDirs(number, coords).add("events")
            .attr("array", "true");
        events.forEach(e -> xml.add("event").set(e).up());
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(xml);
        MatcherAssert.assertThat(
            "Hook events are incorrect",
            new MkHook(
                storage, "", coords, number
            ).json().getJsonArray("events")
                .stream()
                .map(JsonValue::toString)
                .map(event -> event.replace("\"", ""))
                .collect(Collectors.toList()),
                new IsIterableContainingInAnyOrder<>(
                    Arrays.asList(
                        new IsEqual<>("event1"),
                        new IsEqual<>("event2")
                    )
                )
        );
    }

    /**
     * Directives to build a hook XML that only includes its id.
     * @param number Hook number
     * @param coords Repo coords
     * @return Hook directives
     */
    private static Directives hookDirs(final int number, final Coordinates coords) {
        return new Directives().xpath("/github")
            .add("repos").add("repo").attr("coords", coords.toString())
            .add("hooks")
            .add("hook")
            .add("id").set(String.valueOf(number)).up();
    }
}
