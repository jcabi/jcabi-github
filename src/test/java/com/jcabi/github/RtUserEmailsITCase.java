/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtUserEmails}.
 */
@OAuthScope(Scope.USER_EMAIL)
public final class RtUserEmailsITCase {

    /**
     * RtUserEmails can fetch emails.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesEmails() throws Exception {
        MatcherAssert.assertThat(
            RtUserEmailsITCase.userEmails().iterate(),
            Matchers.not(Matchers.emptyIterableOf(String.class))
        );
    }

    /**
     * RtUserEmails can add emails. Note that you must use a real email address
     * (see http://mailinator.com/).
     * @throws Exception If some problem inside
     */
    @Test
    public void addsEmails() throws Exception {
        final String email = "test@mailtothis.com";
        final UserEmails emails = RtUserEmailsITCase.userEmails();
        try {
            MatcherAssert.assertThat(
                emails.add(Collections.singletonList(email)),
                Matchers.hasItem(email)
            );
            MatcherAssert.assertThat(emails.iterate(), Matchers.hasItem(email));
        } finally {
            emails.remove(Collections.singletonList(email));
        }
    }

    /**
     * RtUserEmails can remove emails. Note that you must use a real email
     * address (see http://mailinator.com/).
     * @throws Exception If some problem inside
     */
    @Test
    public void removesEmails() throws Exception {
        final String email = "test1@mailtothis.com";
        final UserEmails emails = RtUserEmailsITCase.userEmails();
        emails.add(Collections.singletonList(email));
        try {
            MatcherAssert.assertThat(emails.iterate(), Matchers.hasItem(email));
        } finally {
            emails.remove(Collections.singletonList(email));
        }
        MatcherAssert.assertThat(
            emails.iterate(), Matchers.not(Matchers.hasItem(email))
        );
    }

    /**
     * Return UserEmails for tests.
     * @return UserEmails
     */
    private static UserEmails userEmails() {
        return new GithubIT().connect().users().self().emails();
    }

}
