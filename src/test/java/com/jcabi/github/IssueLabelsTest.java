/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link IssueLabels}.
 * @since 0.7
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class IssueLabelsTest {

    @Test
    void fetchesLabelsByColor() throws IOException {
        final Label first = Mockito.mock(Label.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("color", "c0c0c0").build()
        ).when(first).json();
        final Label second = Mockito.mock(Label.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("color", "fefefe").build()
        ).when(second).json();
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Arrays.asList(first, second)).when(labels).iterate();
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            new IssueLabels.Smart(labels).findByColor("c0c0c0"),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItems(first)
            )
        );
    }

    /**
     * IssueLabels.Smart can check label's existence by name.
     */
    @Test
    void checksLabelExistenceByName() {
        final Label first = Mockito.mock(Label.class);
        Mockito.doReturn("first").when(first).name();
        final Label second = Mockito.mock(Label.class);
        Mockito.doReturn("second").when(second).name();
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Arrays.asList(first, second)).when(labels).iterate();
        MatcherAssert.assertThat(
            "Values are not equal",
            new IssueLabels.Smart(labels).contains("first"),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Values are not equal",
            new IssueLabels.Smart(labels).contains("third"),
            Matchers.is(false)
        );
    }

    @Test
    void getsLabelByName() {
        final Label first = Mockito.mock(Label.class);
        Mockito.doReturn("a").when(first).name();
        final Label second = Mockito.mock(Label.class);
        Mockito.doReturn("b").when(second).name();
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Arrays.asList(first, second)).when(labels).iterate();
        MatcherAssert.assertThat(
            "Values are not equal",
            new IssueLabels.Smart(labels).get("a").name(),
            Matchers.equalTo("a")
        );
    }

    @Test
    void throwsWhenLabelIsAbsent() {
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Collections.emptyList()).when(labels).iterate();
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new IssueLabels.Smart(labels).get("something"),
            "Should throw when label is absent"
        );
    }

}
