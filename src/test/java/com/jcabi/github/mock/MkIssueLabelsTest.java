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
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import java.util.Collections;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkIssueLabels}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class MkIssueLabelsTest {

    /**
     * MkIssueLabels can list labels.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesIssues() throws Exception {
        final Repo repo = this.repo();
        final String name = "bug";
        repo.labels().create(name, "c0c0c0");
        final Issue issue = repo.issues().create("title", "body");
        issue.labels().add(Collections.singletonList(name));
        MatcherAssert.assertThat(
            issue.labels().iterate(),
            Matchers.<Label>iterableWithSize(1)
        );
    }

    /**
     * MkIssueLabels can create labels through Smart decorator.
     * @throws Exception If some problem inside
     */
    @Test
    public void createsLabelsThroughDecorator() throws Exception {
        final Repo repo = this.repo();
        final Issue issue = repo.issues().create("how are you?", "");
        final String name = "task";
        new IssueLabels.Smart(issue.labels()).addIfAbsent(name, "f0f0f0");
        MatcherAssert.assertThat(
            issue.labels().iterate(),
            Matchers.<Label>iterableWithSize(1)
        );
    }

    /**
     * Create an repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }

}
