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
package com.jcabi.github.mock;

import com.jcabi.github.Issue;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MkPulls}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (100 lines)
 */
public final class MkPullsTest {

    /**
     * MkPulls can create a pull.
     * It should create an issue first, and then pull with the same number
     * @throws Exception if some problem inside
     */
    @Test
    public void canCreateAPull() throws Exception {
        final Repo repo = MkPullsTest.repo();
        final Pull pull = repo.pulls().create("hello", "", "");
        final Issue.Smart issue = new Issue.Smart(
            repo.issues().get(pull.number())
        );
        MatcherAssert.assertThat(
            issue.title(),
            Matchers.is("hello")
        );
    }

    /**
     * MkPulls can fetch empty list of pulls.
     * @throws Exception if some problem inside
     */
    @Test
    @Ignore
    public void canFetchEmptyListOfPulls() throws Exception {
        // to be implemented
    }

    /**
     * MkPulls can fetch single pull.
     * @throws Exception if some problem inside
     */
    @Test
    @Ignore
    public void canFetchSinglePull() throws Exception {
        // to be implemented
    }

    /**
     * Create a repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
