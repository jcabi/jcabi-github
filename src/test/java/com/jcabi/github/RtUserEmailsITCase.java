/**
 * Copyright (c) 2013-2022, jcabi.com
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
package com.jcabi.github;

import com.jcabi.github.OAuthScope.Scope;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtUserEmails}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
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
