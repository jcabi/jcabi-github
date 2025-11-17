/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.github.UserEmails;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkUserEmails}.
 *
 * @since 0.8
 */
public final class MkUserEmailsTest {

    /**
     * MkUserEmails should be able to add emails to a user.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canAddEmails() throws Exception {
        final UserEmails emails = new MkGithub().users().add("john").emails();
        final String email = "john@nowhere.com";
        final Iterable<String> added = emails.add(
            Collections.singleton(email)
        );
        MatcherAssert.assertThat(
            added,
            Matchers.allOf(
                Matchers.<String>iterableWithSize(1),
                Matchers.hasItems(email)
            )
        );
    }

    /**
     * MkUserEmails should be able to remove emails of a user.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRemoveEmails() throws Exception {
        final UserEmails emails = new MkGithub().users().add("joe").emails();
        final String removed = "joe@nowhere.com";
        final String retained = "joseph@somewhere.net";
        emails.add(
            Arrays.asList(
                new String[]{removed, retained}
            )
        );
        emails.remove(Collections.singleton(removed));
        MatcherAssert.assertThat(
            emails.iterate(),
            Matchers.allOf(
                Matchers.<String>iterableWithSize(1),
                Matchers.hasItems(retained),
                Matchers.not(Matchers.hasItems(removed))
            )
        );
    }

    /**
     * MkUserEmails should be able to iterate emails of a user.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canIterateEmails() throws Exception {
        final UserEmails emails = new MkGithub().users().add("matt").emails();
        final String[] added = new String[]{
            "matt@none.org",
            "matthew@somewhere.net",
        };
        emails.add(Arrays.asList(added));
        MatcherAssert.assertThat(
            emails.iterate(),
            Matchers.allOf(
                Matchers.<String>iterableWithSize(2),
                Matchers.hasItems(added)
            )
        );
    }

    /**
     * MkUserEmails can be represented in JSON format.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void canRepresentAsJson() throws Exception {
        final UserEmails emails = new MkGithub().users().add("jeff").emails();
        final String email = "jeff@something.net";
        emails.add(Collections.singleton(email));
        MatcherAssert.assertThat(
            emails.json().getString("email"),
            Matchers.is(email)
        );
    }

}
