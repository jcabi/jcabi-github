/**
 * Copyright (c) 2013-2020, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import com.jcabi.github.Coordinates;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.xembly.Directives;

/**
 * Tests for {@link MkHook}.
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.42
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class MkHookTest {
    /**
     * Test if {@link MkHook} is being created with the correct number.
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectNumber() throws Exception {
        final int number = 5;
        MatcherAssert.assertThat(
            "Hook returned wrong number",
            new MkHook(null, null, null, number).number(),
            new IsEqual<>(number)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct repository.
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectRepo() throws Exception {
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
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectId() throws Exception {
        final int number = 5;
        final MkStorage storage = new MkStorage.InFile();
        final Coordinates coords = new Coordinates.Simple("user/repo");
        storage.apply(
            this.hookDirs(number, coords)
        );
        MatcherAssert.assertThat(
            "Hook json returned wrong id",
            new MkHook(storage, "login", coords, number).json().getString("id"),
            new IsEqual<>(String.valueOf(number))
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct url.
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectUrl() throws Exception {
        final String url = "https://github.com/user/repo/hooks/hook/5";
        final int number = 5;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            this.hookDirs(number, coords)
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
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectTestUrl() throws Exception {
        final String test = "https://github.com/user/repo/hooks/hook/5";
        final int number = 5;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            this.hookDirs(number, coords)
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
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectPingUrl() throws Exception {
        final String ping = "https://github.com/user/repo/hooks/hook/5";
        final int number = 5;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            this.hookDirs(number, coords)
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
     * @throws Exception If something goes wrong
     */
    @Test
    void createWithCorrectEvents() throws Exception {
        final Iterable<String> events = Arrays.asList("event1", "event2");
        final int number = 123;
        final Coordinates coords = new Coordinates.Simple("user/repo");
        final Directives xml = this.hookDirs(number, coords).add("events")
            .attr("array", "true");
        events.forEach(e -> xml.add("event").set(e).up());
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(xml);
        MatcherAssert.assertThat(
            "",
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
    private Directives hookDirs(final int number, final Coordinates coords) {
        return new Directives().xpath("/github")
            .add("repos").add("repo").attr("coords", coords.toString())
            .add("hooks")
            .add("hook")
            .add("id").set(String.valueOf(number)).up();
    }
}
