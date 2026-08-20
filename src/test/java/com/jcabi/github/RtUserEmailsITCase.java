/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.io.IOException;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtUserEmails}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.USER_EMAIL)
final class RtUserEmailsITCase {

    @Test
    void fetchesEmails() throws IOException {
        MatcherAssert.assertThat(
            "Collection is not empty",
            RtUserEmailsITCase.userEmails().iterate(),
            Matchers.not(Matchers.emptyIterableOf(String.class))
        );
    }

    /**
     * RtUserEmails can add emails. Note that you must use a real email address
     * (see http://mailinator.com/).
     */
    @Test
    void addsEmails() throws IOException {
        final String email = "test@mailtothis.com";
        final UserEmails emails = RtUserEmailsITCase.userEmails();
        try {
            MatcherAssert.assertThat(
                "Added email is not returned",
                emails.add(Collections.singletonList(email)),
                Matchers.hasItem(email)
            );
        } finally {
            emails.remove(Collections.singletonList(email));
        }
    }

    @Test
    void iteratesAddedEmails() throws IOException {
        final String email = "test@mailtothis.com";
        final UserEmails emails = RtUserEmailsITCase.userEmails();
        emails.add(Collections.singletonList(email));
        try {
            MatcherAssert.assertThat(
                "Added email is not iterated",
                emails.iterate(),
                Matchers.hasItem(email)
            );
        } finally {
            emails.remove(Collections.singletonList(email));
        }
    }

    /**
     * RtUserEmails can remove emails. Note that you must use a real email
     * address (see http://mailinator.com/).
     */
    @Test
    void removesEmails() throws IOException {
        final String email = "test1@mailtothis.com";
        final UserEmails emails = RtUserEmailsITCase.userEmails();
        emails.add(Collections.singletonList(email));
        emails.remove(Collections.singletonList(email));
        MatcherAssert.assertThat(
            "Removed email is still there",
            emails.iterate(),
            Matchers.not(Matchers.hasItem(email))
        );
    }

    private static UserEmails userEmails() {
        return GitHubIT.connect().users().self().emails();
    }
}
