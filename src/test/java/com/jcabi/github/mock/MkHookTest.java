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
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MkHook}.
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.42
 */
public final class MkHookTest {

    /**
     * Hook to be tested.
     */
    private transient Hook hook;

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
        this.hook =  new MkHook(
            storage,
            login,
            coordinates,
            Tv.FIVE
        );
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
    @Test(expected = UnsupportedOperationException.class)
    public void createWithCorrectId() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong id",
            this.hook.json().getInt("id"),
            new IsEqual<Integer>(Tv.FIVE)
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct url.
     * @throws Exception If something goes wrong
     */
    @Test(expected = UnsupportedOperationException.class)
    public void createWithCorrectUrl() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong url",
            this.hook.json().getString("url"),
            new IsEqual<String>(
                "https://api.github.com/repos/jcabi/jcabi-github/hooks/1"
            )
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct test url.
     * @throws Exception If something goes wrong
     */
    @Test(expected = UnsupportedOperationException.class)
    public void createWithCorrectTestUrl() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong test_url",
            this.hook.json().getString("test_url"),
            new IsEqual<String>(
                "https://api.github.com/repos/jcabi/jcabi-github/hooks/1/test"
            )
        );
    }

    /**
     * Test if {@link MkHook} is being created with the correct ping url.
     * @throws Exception If something goes wrong
     */
    @Test(expected = UnsupportedOperationException.class)
    public void createWithCorrectPingUrl() throws Exception {
        MatcherAssert.assertThat(
            "Hook json returned wrong ping_url",
            this.hook.json().getString("ping_url"),
            new IsEqual<String>(
                "https://api.github.com/repos/jcabi/jcabi-github/hooks/1/pings"
            )
        );
    }
}
