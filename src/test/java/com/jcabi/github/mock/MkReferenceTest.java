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

package com.jcabi.github.mock;

import com.jcabi.github.Reference;
import javax.json.Json;
import javax.json.JsonObject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Testcase for {@link MkReference}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class MkReferenceTest {

    /**
     * MkReference can return its name.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsName() throws Exception {
        MatcherAssert.assertThat(
            this.reference().ref(),
            Matchers.is("refs/tags/hello")
        );
    }

    /**
     * MkReference can return its owner.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void returnsRepo() throws Exception {
        MatcherAssert.assertThat(
            this.reference().repo(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkReference can fetch json.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void fetchesJson() throws Exception {
        final Reference ref = this.reference();
        final JsonObject json = ref.json();
        MatcherAssert.assertThat(
            json.getString("ref"),
            Matchers.is("refs/tags/hello")
        );
        MatcherAssert.assertThat(
            json.getString("sha"),
            Matchers.is("testsha")
        );
    }

    /**
     * MkReference should be able to patch itself.
     * @throws Exception - If something goes wrong.
     */
    @Test
    public void patchesRef() throws Exception {
        final Reference ref = this.reference();
        final JsonObject json = Json.createObjectBuilder()
            .add("sha", "testshaPATCH")
            .build();
        ref.patch(json);
        MatcherAssert.assertThat(
            ref.json().getString("sha"),
            Matchers.is("testshaPATCH")
        );
    }

    /**
     * Return a Reference for testing.
     * @return Reference
     * @throws Exception - if something goes wrong.
     */
    private Reference reference() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        ).git().references().create("refs/tags/hello", "testsha");
    }
}
