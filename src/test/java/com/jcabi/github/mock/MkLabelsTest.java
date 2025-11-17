/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Issue;
import com.jcabi.github.Label;
import com.jcabi.github.Labels;
import com.jcabi.github.Repo;
import java.io.IOException;
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
     */
    @Test
    public void iteratesLabels() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        repo.labels().create("bug", "e0e0e0");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.labels().iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * MkLabels can delete labels.
     */
    @Test
    public void deletesLabels() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Labels labels = repo.labels();
        final String name = "label-0";
        labels.create(name, "e1e1e1");
        final Issue issue = repo.issues().create("hey, you!", "");
        issue.labels().add(Collections.singletonList(name));
        labels.delete(name);
        MatcherAssert.assertThat(
            "Collection is not empty",
            repo.labels().iterate(),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            "Collection is not empty",
            issue.labels().iterate(),
            Matchers.emptyIterable()
        );
    }

    /**
     * MkLabels can set label color.
     */
    @Test
    public void setsLabelColor() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final String color = "f0f0f0";
        final String name = "task";
        repo.labels().create(name, color);
        MatcherAssert.assertThat(
            "Values are not equal",
            new Label.Smart(repo.labels().get(name)).color(),
            Matchers.equalTo(color)
        );
    }
}
