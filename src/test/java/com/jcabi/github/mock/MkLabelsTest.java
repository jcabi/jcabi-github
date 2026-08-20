/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.Issue;
import com.jcabi.github.Label;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkLabels}.
 * @since 0.6
 */
final class MkLabelsTest {

    /**
     * Name of the label.
     */
    private static final String NAME = "label-0";

    @Test
    void iteratesLabels() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        repo.labels().create("bug", "e0e0e0");
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            repo.labels().iterate(),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void deletesLabels() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        MkLabelsTest.labeled(repo);
        repo.labels().delete(MkLabelsTest.NAME);
        MatcherAssert.assertThat(
            "Collection is not empty",
            repo.labels().iterate(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void deletesLabelsFromIssues() throws IOException {
        final Repo repo = new MkGitHub().randomRepo();
        final Issue issue = MkLabelsTest.labeled(repo);
        repo.labels().delete(MkLabelsTest.NAME);
        MatcherAssert.assertThat(
            "Collection is not empty",
            issue.labels().iterate(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void setsLabelColor() throws IOException {
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

    private static Issue labeled(final Repo repo) throws IOException {
        repo.labels().create(MkLabelsTest.NAME, "e1e1e1");
        final Issue issue = repo.issues().create("hey, you!", "");
        issue.labels().add(Collections.singletonList(MkLabelsTest.NAME));
        return issue;
    }
}
