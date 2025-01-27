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

import com.jcabi.github.Issue;
import com.jcabi.github.Label;
import com.jcabi.github.Labels;
import com.jcabi.github.Repo;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkLabels}.
 */
public final class MkLabelsTest {

    /**
     * MkLabels can list labels.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesLabels() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        repo.labels().create("bug", "e0e0e0");
        MatcherAssert.assertThat(
            repo.labels().iterate(),
            Matchers.<Label>iterableWithSize(1)
        );
    }

    /**
     * MkLabels can delete labels.
     * @throws Exception If some problem inside
     */
    @Test
    public void deletesLabels() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Labels labels = repo.labels();
        final String name = "label-0";
        labels.create(name, "e1e1e1");
        final Issue issue = repo.issues().create("hey, you!", "");
        issue.labels().add(Collections.singletonList(name));
        labels.delete(name);
        MatcherAssert.assertThat(
            repo.labels().iterate(),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            issue.labels().iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkLabels can set label color.
     * @throws Exception If some problem inside
     */
    @Test
    public void setsLabelColor() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final String color = "f0f0f0";
        final String name = "task";
        repo.labels().create(name, color);
        MatcherAssert.assertThat(
            new Label.Smart(repo.labels().get(name)).color(),
            Matchers.equalTo(color)
        );
    }
}
