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
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
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
