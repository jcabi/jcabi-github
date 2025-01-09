/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.FakeRequest;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Unit tests for {@link RtGitignores}.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="https://developer.github.com/v3/gitignore/">Gitignore API</a>
 */
@Immutable
public final class RtGitignoresTest {
    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtGitignores can iterate template names.
     * @throws Exception if there is any error
     */
    @Test
    public void iterateTemplateNames() throws Exception {
        try (
            final MkContainer container = new MkGrizzlyContainer().next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    Json.createArrayBuilder()
                        .add("C")
                        .add("Java")
                        .build()
                        .toString()
                )
            ).start(this.resource.port())
        ) {
            final RtGitignores gitignores = new RtGitignores(
                    new RtGithub(new JdkRequest(container.home()))
            );
            MatcherAssert.assertThat(
                    gitignores.iterate(),
                    Matchers.<String>iterableWithSize(2)
            );
            container.stop();
        }
    }

    /**
     * RtGitignores can get raw template by name.
     * @throws Exception if there is any error
     */
    @Test
    public void getRawTemplateByName() throws Exception {
        final RtGitignores gitignores = new RtGitignores(
            new RtGithub(new FakeRequest().withBody("# Object files"))
        );
        MatcherAssert.assertThat(
            gitignores.template("C#"),
            Matchers.startsWith("# Object")
        );
    }

}
