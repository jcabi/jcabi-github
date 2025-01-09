/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.jcabi.github.Commit;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for MkTags.
 * @author Ed Hillmann (edhillmann@yahoo.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public class MkCommitsTest {

    /**
     * MkCommits can create commits.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void createsMkCommit() throws Exception {
        final JsonObject author = Json.createObjectBuilder()
            .add("name", "Scott").add("email", "Scott@gmail.com")
            .add("date", "2008-07-09T16:13:30+12:00").build();
        final JsonArray tree = Json.createArrayBuilder()
            .add("xyzsha12").build();
        final Commit newCommit = new MkGithub().randomRepo()
            .git().commits().create(
                Json.createObjectBuilder().add("message", "my commit message")
                    .add("sha", "12ahscba")
                    .add("tree", "abcsha12")
                    .add("parents", tree)
                    .add("author", author).build()
            );
        MatcherAssert.assertThat(
            newCommit,
            Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            newCommit.sha(),
            Matchers.equalTo("12ahscba")
        );
    }
}
