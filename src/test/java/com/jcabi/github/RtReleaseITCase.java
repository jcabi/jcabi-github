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

import com.jcabi.aspects.Tv;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtRelease}.
 * @author Haris Osmanagic (haris.osmanagic@gmail.com)
 * @version $Id$
 * @since 0.8
 */
public final class RtReleaseITCase {

    /**
     * RtRelease can edit a release.
     * @throws Exception If any problems during test execution occur.
     */
    @Test
    public void canEditRelease() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        final Github github = new RtGithub(key);
        final Repos repos = github.repos();
        final String name = "name";
        final Repo repo = repos.create(
            Json.createObjectBuilder().add(
                name, RandomStringUtils.randomNumeric(Tv.TEN)
            ).add("auto_init", true).build()
        );
        try {
            final Release release = repo.releases().create(
                RandomStringUtils.randomAlphanumeric(Tv.TEN)
            );
            final JsonObject patch = Json.createObjectBuilder()
                .add("tag_name", RandomStringUtils.randomAlphanumeric(Tv.TEN))
                .add(name, "jcabi Github test release")
                .add("body", "jcabi Github was here!")
                .build();
            release.patch(patch);
            final JsonObject json = repo.releases()
                .get(release.number()).json();
            for (final String property : patch.keySet()) {
                MatcherAssert.assertThat(
                    json.getString(property),
                    Matchers.equalTo(patch.getString(property))
                );
            }
        } finally {
            repos.remove(repo.coordinates());
        }
    }
}
