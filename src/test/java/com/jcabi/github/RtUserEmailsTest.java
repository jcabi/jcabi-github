/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import java.net.HttpURLConnection;
import java.util.Collections;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RtUserEmails}.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
public final class RtUserEmailsTest {

    /**
     * RtUserEmails can fetch emails.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesEmails() throws Exception {
        final String email = "test@email.com";
        final UserEmails emails = new RtUserEmails(
            new FakeRequest().withBody(
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder().add("email", email))
                    .build().toString()
            )
        );
        MatcherAssert.assertThat(
            emails.iterate().iterator().next(), Matchers.equalTo(email)
        );
    }

    /**
     * RtUserEmails can add emails.
     * @throws Exception If some problem inside
     */
    @Test
    public void addsEmails() throws Exception {
        final String email = "test1@email.com";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_CREATED,
                String.format("[{\"email\":\"%s\"}]", email)
            )
        );
        container.start();
        try {
            final UserEmails emails = new RtUserEmails(
                new ApacheRequest(container.home())
            );
            MatcherAssert.assertThat(
                emails.add(Collections.singletonList(email)).iterator().next(),
                Matchers.equalTo(email)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtUserEmails can remove emails.
     * @throws Exception If some problem inside
     */
    @Test
    public void removesEmails() throws Exception {
        final UserEmails emails = new RtUserEmails(
            new FakeRequest().withStatus(HttpURLConnection.HTTP_NO_CONTENT)
        );
        emails.remove(Collections.singletonList("test2@email.com"));
    }

}
