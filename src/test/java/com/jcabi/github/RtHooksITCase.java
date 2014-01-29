/**
 * Copyright (c) 2012-2013, JCabi.com
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
package com.jcabi.github;

import java.util.Collections;
import javax.json.Json;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RtHooks}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 * @todo #165 RtHooks should be able to create a hook in real repository
 *  When done, remove this puzzle and Ignore annotation from the method.
 * @todo #159 Need to implement integration test case where RtHooks can obtain
 *  a list of hooks from a real repository. Add the implementation in
 *  canFetchAllHooks(). When done, remove this puzzle and Ignore annotation from
 *  the method.
 */
public final class RtHooksITCase {

    /**
     * RtHooks can iterate hooks.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void canFetchAllHooks() throws Exception {
        // to be implemented
    }

    /**
     * RtHooks can create a hook.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void canCreateAHook() throws Exception {
        // to be implemented
    }

    /**
     * RtHooks can fetch a single hook.
     *
     * @throws Exception If some problem inside.
     */
    @Test
    public void canFetchSingleHook() throws Exception {
        final Repos repos = RtHooksITCase.github().repos();
        final Repo repo = repos.create(
            Json.createObjectBuilder().add(
                // @checkstyle MagicNumber (1 line)
                "name", RandomStringUtils.randomNumeric(5)
            ).build()
        );
        try {
            final Hooks hooks = repo.hooks();
            final int number = hooks.create(
                "geocommit", Collections.<String, String>emptyMap()
            ).number();
            MatcherAssert.assertThat(
                hooks.get(number).json().getInt("id"),
                Matchers.equalTo(number)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtHooks can remove a hook by ID.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void canRemoveHook() throws Exception {
        final Repos repos = RtHooksITCase.github().repos();
        final Repo repo = repos.create(
            Json.createObjectBuilder().add(
                // @checkstyle MagicNumber (1 line)
                "name", RandomStringUtils.randomNumeric(5)
            ).build()
        );
        try {
            final Hooks hooks = repo.hooks();
            final Hook hook = hooks.create(
                "geocommit", Collections.<String, String>emptyMap()
            );
            hooks.remove(hook.number());
            MatcherAssert.assertThat(
                hooks.iterate(),
                Matchers.not(Matchers.hasItem(hook))
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Return github for tests.
     * @return Github
     */
    private static Github github() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key);
    }

}
