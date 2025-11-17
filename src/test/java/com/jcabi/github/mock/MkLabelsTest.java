/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
