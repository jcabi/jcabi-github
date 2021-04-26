/**
 * Copyright (c) 2013-2020, jcabi.com
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

import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Milestone}.
 * @author Paul Polischuk (ppol@ua.fm)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MilestoneTest {
    /**
     * Milestone.Smart can fetch title property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesTitle() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("title", "this is some title")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).title(),
            Matchers.notNullValue()
        );
    }

    /**
     * Milestone.Smart can fetch description property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesDescription() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("description", "description of the milestone")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).description(),
            Matchers.notNullValue()
        );
    }

    /**
     * Milestone.Smart can fetch state property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesState() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("state", "state of the milestone")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).state(),
            Matchers.notNullValue()
        );
    }

    /**
     * Milestone.Smart can fetch due_on property from Milestone.
     * @throws Exception If some problem inside
     */
    @Test
    void fetchesDueOn() throws Exception {
        final Milestone milestone = Mockito.mock(Milestone.class);
        Mockito.doReturn(
            Json.createObjectBuilder()
                .add("due_on", "2011-04-10T20:09:31Z")
                .build()
        ).when(milestone).json();
        MatcherAssert.assertThat(
            new Milestone.Smart(milestone).dueOn(),
            Matchers.notNullValue()
        );
    }
}
