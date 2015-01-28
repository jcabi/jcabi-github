/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.github.mock.MkGithub;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtFork}.
 *
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RtForkTest {
    /**
     * The rule for skipping test if there's BindException.
     * @todo 989 Apply this rule to other classes that use MkGrizzlyContainer
     *  and make MkGrizzlyContainers use port() given by this resource to avoid
     *  tests fail with BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtFork can patch comment and return new json.
     * @throws IOException if has some problems with json parsing.
     */
    @Test
    public void patchAndCheckJsonFork() throws IOException {
        final String original = "some organization";
        final String patched = "some patched organization";
        final MkContainer container =
            new MkGrizzlyContainer().next(this.answer(original))
                .next(this.answer(patched)).next(this.answer(original)).start(
                    this.resource.port()
                );
        final MkContainer forksContainer = new MkGrizzlyContainer().start(
            this.resource.port()
        );
        final RtRepo repo =
            new RtRepo(
                new MkGithub(),
                new ApacheRequest(forksContainer.home()),
                new Coordinates.Simple("test_user", "test_repo")
            );
        final RtFork fork = new RtFork(
            new ApacheRequest(container.home()), repo, 1
        );
        fork.patch(RtForkTest.fork(patched));
        MatcherAssert.assertThat(
            new Fork.Smart(fork).organization(),
            Matchers.equalTo(patched)
        );
        MatcherAssert.assertThat(
            new Fork.Smart(fork).name(),
            Matchers.notNullValue()
        );
        container.stop();
        forksContainer.stop();
    }

    /**
     * Create and return success MkAnswer object to test.
     * @param organization The organization of the fork
     * @return Success MkAnswer
     */
    private MkAnswer.Simple answer(final String organization) {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_OK,
            RtForkTest.fork(organization).toString()
        );
    }

    /**
     * Create and return JsonObject to test.
     * @param organization The organization of the fork
     * @return JsonObject
     */
    private static JsonObject fork(final String organization) {
        return Json.createObjectBuilder()
            .add("organization", organization)
            .add("name", "nm")
            .build();
    }

}
