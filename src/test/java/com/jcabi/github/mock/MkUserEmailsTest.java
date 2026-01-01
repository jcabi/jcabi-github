/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.UserEmails;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link MkUserEmails}.
 * @since 0.8
 */
final class MkUserEmailsTest {

    @Test
    void canAddEmails() throws IOException {
        final UserEmails emails = new MkGitHub().users().add("john").emails();
        final String email = "john@nowhere.com";
        final Iterable<String> added = emails.add(
            Collections.singleton(email)
        );
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            added,
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItems(email)
            )
        );
    }

    @Test
    void canRemoveEmails() throws IOException {
        final UserEmails emails = new MkGitHub().users().add("joe").emails();
        final String removed = "joe@nowhere.com";
        final String retained = "joseph@somewhere.net";
        emails.add(
            Arrays.asList(
                new String[]{removed, retained}
            )
        );
        emails.remove(Collections.singleton(removed));
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            emails.iterate(),
            Matchers.allOf(
                Matchers.iterableWithSize(1),
                Matchers.hasItems(retained),
                Matchers.not(Matchers.hasItems(removed))
            )
        );
    }

    @Test
    void canIterateEmails() throws IOException {
        final UserEmails emails = new MkGitHub().users().add("matt").emails();
        final String[] added = {
            "matt@none.org",
            "matthew@somewhere.net",
        };
        emails.add(Arrays.asList(added));
        MatcherAssert.assertThat(
            "Collection size is incorrect",
            emails.iterate(),
            Matchers.allOf(
                Matchers.iterableWithSize(2),
                Matchers.hasItems(added)
            )
        );
    }

    @Test
    void canRepresentAsJson() throws IOException {
        final UserEmails emails = new MkGitHub().users().add("jeff").emails();
        final String email = "jeff@something.net";
        emails.add(Collections.singleton(email));
        MatcherAssert.assertThat(
            "Values are not equal",
            emails.json().getString("email"),
            Matchers.is(email)
        );
    }

}
