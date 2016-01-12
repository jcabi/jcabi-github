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

import com.jcabi.aspects.Immutable;
import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.mock.MkQuery;
import com.jcabi.http.request.JdkRequest;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtHooks}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Immutable
public final class RtHooksTest {

    /**
     * The rule for skipping test if there's BindException.
     *  and make MkGrizzlyContainers use port() given by this resource to avoid
     *  tests fail with BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * RtHooks can fetch empty list of hooks.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchEmptyListOfHooks() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_OK, "[]")
        ).start(this.resource.port());
        final Hooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        try {
            MatcherAssert.assertThat(
                hooks.iterate(),
                Matchers.emptyIterable()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtHooks can fetch non empty list of hooks.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchNonEmptyListOfHooks() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(hook("hook 1", Collections.<String, String>emptyMap()))
                    .add(hook("hook 2", Collections.<String, String>emptyMap()))
                    .build().toString()
            )
        ).start(this.resource.port());
        final RtHooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        MatcherAssert.assertThat(
            hooks.iterate(),
            Matchers.<Hook>iterableWithSize(2)
        );
        container.stop();
    }

    /**
     * RtHooks can fetch single hook.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchSingleHook() throws Exception {
        final String name = "hook name";
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                RtHooksTest.hook(
                    name,
                    Collections.<String, String>emptyMap()
                ).toString()
            )
        ).start(this.resource.port());
        final Hooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        final Hook hook = hooks.get(1);
        MatcherAssert.assertThat(
            new Hook.Smart(hook).name(),
            Matchers.equalTo(name)
        );
        container.stop();
    }

    /**
     * RtHooks can create a hook.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canCreateHook() throws Exception {
        final String name = "hook name";
        final ConcurrentHashMap<String, String> config =
            new ConcurrentHashMap<String, String>(2);
        config.put("url", "http://example.com");
        config.put("content_type", "json");
        final String body = RtHooksTest.hook(name, config).toString();
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_CREATED, body)
        ).next(new MkAnswer.Simple(HttpURLConnection.HTTP_OK, body))
            .start(this.resource.port());
        final Hooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        try {
            final Hook hook = hooks.create(name, config, true);
            MatcherAssert.assertThat(
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
            MatcherAssert.assertThat(
                new Hook.Smart(hook).name(),
                Matchers.equalTo(name)
            );
        } finally {
            container.stop();
        }
    }

    /**
     * RtHooks can delete a hook.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void canDeleteHook() throws Exception {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(HttpURLConnection.HTTP_NO_CONTENT, "")
        ).start(this.resource.port());
        final Hooks hooks = new RtHooks(
            new JdkRequest(container.home()),
            RtHooksTest.repo()
        );
        hooks.remove(1);
        try {
            final MkQuery query = container.take();
            MatcherAssert.assertThat(
                query.method(),
                Matchers.equalTo(Request.DELETE)
            );
            MatcherAssert.assertThat(
                query.body(),
                Matchers.isEmptyString()
            );
        } finally {
            container.stop();
        }
    }

    /**
     * Create and return JsonObject to test.
     * @param name Name of the hook
     * @param config Config of hook
     * @return JsonObject
     * @throws Exception If some problem inside
     */
    private static JsonObject hook(final String name,
        final Map<String, String> config)
        throws Exception {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        for (final Map.Entry<String, String> entry : config.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return Json.createObjectBuilder()
            .add("id", 1)
            .add("name", name)
            .add("config", builder)
            .build();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("test", "hooks"))
            .when(repo).coordinates();
        return repo;
    }
}
