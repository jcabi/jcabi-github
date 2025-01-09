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

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link RtChecks}.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @version $Id$
 * @since 1.5.0
 */
public final class RtChecksTest {

    /**
     * Conclusion key in json check.
     */
    private static final String CONCLUSION_KEY = "conclusion";

    /**
     * Status key in json check.
     */
    private static final String STATUS_KEY = "status";

    /**
     * The rule for skipping test if there's BindException.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RandomPort resource = new RandomPort();

    /**
     * Checks whether RtChecks can get all checks.
     *
     * @throws IOException If some problem happens.
     */
    @Test
    public void getsAllChecks() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonWithCheckRuns()
                )
            )
            .start(this.resource.port())) {
            final Checks checks = new RtChecks(
                new JdkRequest(container.home()),
                this.repo().pulls().get(0)
            );
            MatcherAssert.assertThat(
                checks.all(),
                Matchers.iterableWithSize(1)
            );
        }
    }

    /**
     * Checks whether RtChecks can return empty checks if they are absent.
     *
     * @throws IOException If some I/O problem happens.
     */
    @Test
    public void returnsEmptyChecksIfTheyAreAbsent() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.empty()
                )
            )
            .start(this.resource.port())) {
            MatcherAssert.assertThat(
                ((Checks) new RtChecks(
                    new JdkRequest(container.home()),
                    this.repo().pulls().get(0)
                )).all(),
                Matchers.iterableWithSize(0)
            );
        }
    }

    /**
     * Checks whether RtChecks can throw an exception
     * if response code is not 200.
     *
     * @throws IOException If some I/O problem happens.
     */
    @Test
    public void assertsOkResponse() throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    RtChecksTest.jsonWithCheckRuns()
                )
            ).start(this.resource.port())
        ) {
            final Checks checks = new RtChecks(
                new JdkRequest(container.home()),
                this.repo().pulls().get(0)
            );
            Assert.assertThrows(
                AssertionError.class,
                checks::all
            );
        }
    }

    /**
     * Checks that library can handle unfinished checks.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    public void retrievesUnfinishedChecksWithoutConclusion()
        throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(
                        RtChecksTest.jsonCheck()
                            .add(
                                RtChecksTest.CONCLUSION_KEY,
                                Check.Conclusion.SUCCESS.value()
                            )
                    )
                )
            ).start(this.resource.port())
        ) {
            final Checks checks = new RtChecks(
                new JdkRequest(container.home()),
                this.repo().pulls().get(0)
            );
            final Collection<? extends Check> all = checks.all();
            MatcherAssert.assertThat(all, Matchers.hasSize(1));
            for (final Check check : all) {
                MatcherAssert.assertThat(
                    check.successful(),
                    Matchers.is(false)
                );
            }
        }
    }

    /**
     * Checks that library can handle unfinished checks.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    public void retrievesUnfinishedChecksWithNullableConclusion()
        throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(
                        RtChecksTest.jsonCheck()
                            .add(
                                RtChecksTest.CONCLUSION_KEY,
                                JsonValue.NULL
                            ).add(
                                RtChecksTest.STATUS_KEY,
                                Check.Status.QUEUED.value()
                            )
                    )
                )
            ).start(this.resource.port())
        ) {
            final Checks checks = new RtChecks(
                new JdkRequest(container.home()),
                this.repo().pulls().get(0)
            );
            final Collection<? extends Check> all = checks.all();
            MatcherAssert.assertThat(all, Matchers.hasSize(1));
            for (final Check check : all) {
                MatcherAssert.assertThat(
                    check.successful(),
                    Matchers.is(false)
                );
            }
        }
    }

    /**
     * Checks that library can handle unfinished checks.
     * @throws IOException If some I/O problem happens.
     */
    @Test
    public void retrievesUnfinishedChecksWithoutStatusAndConclusion()
        throws IOException {
        try (final MkContainer container = new MkGrizzlyContainer()
            .next(
                new MkAnswer.Simple(
                    HttpURLConnection.HTTP_OK,
                    RtChecksTest.jsonChecks(
                        RtChecksTest.jsonCheck()
                    )
                )
            ).start(this.resource.port())
        ) {
            final Checks checks = new RtChecks(
                new JdkRequest(container.home()),
                this.repo().pulls().get(0)
            );
            final Collection<? extends Check> all = checks.all();
            MatcherAssert.assertThat(all, Matchers.hasSize(1));
            for (final Check check : all) {
                MatcherAssert.assertThat(
                    check.successful(),
                    Matchers.is(false)
                );
            }
        }
    }

    /**
     * Creates json response body.
     *
     * @return Json response body.
     */
    private static String jsonWithCheckRuns() {
        return RtChecksTest.jsonChecks(
            RtChecksTest.jsonCheck()
                .add(
                    RtChecksTest.STATUS_KEY,
                    Check.Status.COMPLETED.value()
                )
                .add(
                    RtChecksTest.CONCLUSION_KEY,
                    Check.Conclusion.SUCCESS.value()
                )
        );
    }

    /**
     * Creates Json Check Builder.
     * @return JsonObjectBuilder.
     */
    private static JsonObjectBuilder jsonCheck() {
        return Json.createObjectBuilder()
            .add("id", Json.createValue(new Random().nextInt()));
    }

    /**
     * Creates json checks.
     * @param checks All checks that have to be included.
     * @return Json.
     */
    private static String jsonChecks(final JsonObjectBuilder... checks) {
        final JsonArrayBuilder all = Json.createArrayBuilder();
        Arrays.stream(checks).map(JsonObjectBuilder::build).forEach(all::add);
        return Json.createObjectBuilder()
            .add("total_count", Json.createValue(1))
            .add("check_runs", all.build())
            .build()
            .toString();
    }

    /**
     * Creates json response body without check runs.
     * @return Json response body.
     */
    private static String empty() {
        return Json.createObjectBuilder()
            .build()
            .toString();
    }

    /**
     * Create and return repo for testing.
     * @return Repo
     * @throws IOException If some problem happens.
     */
    private Repo repo() throws IOException {
        final Repo repo = Mockito.mock(Repo.class);
        final Pulls pulls = Mockito.mock(Pulls.class);
        final Pull pull = Mockito.mock(Pull.class);
        final PullRef ref = Mockito.mock(PullRef.class);
        Mockito.doReturn(
                new Coordinates.Simple("volodya-lombrozo", "jtcop")
            ).when(repo)
            .coordinates();
        Mockito.doReturn(pulls).when(repo).pulls();
        Mockito.doReturn(pull).when(pulls).get(0);
        Mockito.doReturn(repo).when(pull).repo();
        Mockito.doReturn(ref).when(pull).head();
        Mockito.doReturn("abcdef1").when(ref).sha();
        return repo;
    }
}
