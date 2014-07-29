/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.aspects.Tv;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtSearch}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (90 lines)
 */
public final class RtSearchITCase {

    /**
     * RtSearch can search for repos.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForRepos() throws Exception {
        MatcherAssert.assertThat(
            RtSearchITCase.github().search().repos("repo", "stars", "desc"),
            Matchers.not(Matchers.emptyIterableOf(Repo.class))
        );
    }

    /**
     * RtSearch can fetch multiple pages of a large result (more than 25 items).
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canFetchMultiplePages() throws Exception {
        final Iterator<Repo> iter = RtSearchITCase.github().search().repos(
            "java", "", ""
        ).iterator();
        int count = 0;
        while (iter.hasNext() && count < Tv.HUNDRED) {
            iter.next();
            count += 1;
        }
        MatcherAssert.assertThat(
            count,
            Matchers.greaterThanOrEqualTo(Tv.HUNDRED)
        );
    }

    /**
     * RtSearch can search for issues.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForIssues() throws Exception {
        MatcherAssert.assertThat(
            RtSearchITCase.github().search().issues("issue", "updated", "desc"),
            Matchers.not(Matchers.emptyIterableOf(Issue.class))
        );
    }

    /**
     * RtSearch can search for users.
     *
     * @throws Exception if a problem occurs
     */
    @Test
    public void canSearchForUsers() throws Exception {
        MatcherAssert.assertThat(
            RtSearchITCase.github().search().users("jcabi", "joined", "desc"),
            Matchers.not(Matchers.emptyIterableOf(User.class))
        );
    }

    /**
     * RtSearch can search for contents.
     *
     * @throws Exception if a problem occurs
     * @see <a href="https://developer.github.com/v3/search/#search-code">Search API</a> for details
     */
    @Test
    public void canSearchForContents() throws Exception {
        MatcherAssert.assertThat(
            RtSearchITCase.github().search().codes(
                "addClass repo:jquery/jquery", "joined", "desc"
            ),
            Matchers.not(Matchers.emptyIterableOf(Content.class))
        );
    }

    /**
     * Return github for test.
     * @return Github
     */
    private static Github github() {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key);
    }

}
