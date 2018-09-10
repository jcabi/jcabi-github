/**
 * Copyright (c) 2013-2018, jcabi.com
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

import com.jcabi.aspects.Tv;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Hook;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.xembly.Directives;

/**
 * Tests for {@link MkHook}.
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.42
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
public final class MkHookTest {

    /**
     * Hook to be tested.
     */
    private transient Hook hook;

    /**
     * XML encapsulated by the hook.
     */
    private transient XML xml;

    /**
     * {@link Repo} for testing.
     */
    private transient Repo repo;

    /**
     * Set up variables for tests.
     * @throws Exception if something goes wrong
     */
    @Before
    public void setUp() throws Exception {
        final String login = "paulodamaso";
        final Coordinates coordinates = new Coordinates.Simple(
            "jcabi",
            "jcabi-github"
        );
        final MkStorage storage = new MkStorage.InFile();
        storage.apply(
            new Directives().xpath("/github")
                .add("repos").add("repo").attr("coords", coordinates.toString())
                .add("hooks")
                .add("hook")
                .add("id").set(String.valueOf(Tv.FIVE)).up()
                .add("url").set("http://github.com/url").up()
                .add("test_url").set("http://github.com/test_url").up()
                .add("ping_url").set("http://github.com/ping_url").up()
                .add("name").set("testname").up()
                .add("events")
                    .add("push").up()
                    .add("pull_request").up()
                    .up()
                .add("active").set("true").up()
                .add("config")
                    .add("url").set("http://example.com/webhook").up()
                    .add("content_type").set("json").up()
                    .up()
                .add("updated_at").set("2011-09-06T20:39:23Z").up()
                .add("created_at").set("2011-09-06T17:26:27Z").up()
        );
        this.hook =  new MkHook(
            storage,
            login,
            coordinates,
            Tv.FIVE
        );
        this.xml = storage.xml()
            .nodes(String.format("//hook[id=%s]", Tv.FIVE)).get(0);
        this.repo = new MkRepo(storage, login, coordinates);
    }

    /**
     * Test if {@link MkHook} is being created with the correct number.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createWithCorrectNumber() throws Exception {
        MatcherAssert.assertThat(
            "Hook returned wrong number",
            new Integer(this.hook.number()),
            new IsEqual<Integer>(Tv.FIVE)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct repository.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createWithCorrectRepo() throws Exception {
        MatcherAssert.assertThat(
            "Hook returned wrong repository",
            this.hook.repo(),
            new IsEqual<Repo>(this.repo)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct id.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createWithCorrectId() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong id",
            this.hook.json().getInt("id"),
            new IsEqual<Integer>(
                Integer.parseInt(this.xml.xpath("id/text()").get(0))
            )
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct url.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createWithCorrectUrl() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong url",
            this.hook.json().getString("url"),
            new IsEqual<String>(
                this.xml.xpath("url/text()").get(0)
            )
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct test url.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createWithCorrectTestUrl() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong test_url",
            this.hook.json().getString("test_url"),
            new IsEqual<String>(
                this.xml.xpath("test_url/text()").get(0)
            )
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct ping url.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createWithCorrectPingUrl() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong ping_url",
            this.hook.json().getString("ping_url"),
            new IsEqual<String>(
                this.xml.xpath("ping_url/text()").get(0)
            )
        );
    }
}
