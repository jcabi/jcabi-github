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
package com.jcabi.github.mock;

import com.jcabi.github.Repo;
import com.jcabi.github.Stars;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link com.jcabi.github.mock.MkStars}.
 *
 * @author Alexander Paderin (apocarteres@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (200 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class MkStarsTest {

    /**
     * Checks that repo is not starred by default.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void repoIsNotStarredByDefault() throws Exception {
        MatcherAssert.assertThat(
                true, Matchers.not(this.repo().stars().starred("user", "repo"))
        );
    }

    /**
     * Checks that repo is starred when user stars that.
     * @todo #957:Implement MkStars.star() and MkStars.unstar() operations.
     * @throws Exception If something goes wrong.
     */
    @Test
    @Ignore
    public void repoIsStarredIfUserStarredThat() throws Exception {
        final Stars stars = this.repo().stars();
        stars.star("user", "repo");
        MatcherAssert.assertThat(
                true, Matchers.is(stars.starred("user", "repo"))
        );
    }

    /**
     * Checks that repo is not starred when user "unstar" that.
     * @todo #957:Implement MkStars.star() and MkStars.unstar() operations.
     * @throws Exception If something goes wrong.
     */
    @Test
    @Ignore
    public void repoIsNotStarredIfUserUnStarredThat() throws Exception {
        this.repo().stars().star("user", "repo");
        this.repo().stars().unstar("user", "repo");
        MatcherAssert.assertThat(
                true, Matchers.not(this.repo().stars().starred("user", "repo"))
        );
    }

    /**
     * Repo for testing.
     * @return Repo
     * @throws Exception - if something goes wrong.
     */
    private Repo repo() throws Exception {
        return new MkGithub().repos().create(
                Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
