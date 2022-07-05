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

import java.util.Arrays;
import java.util.Collections;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link IssueLabels}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
public final class IssueLabelsTest {

    /**
     * IssueLabels.Smart can fetch labels by color.
     * @throws Exception If some problem inside
     */
    @Test
    public void fetchesLabelsByColor() throws Exception {
        final Label first = Mockito.mock(Label.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("color", "c0c0c0").build()
        ).when(first).json();
        final Label second = Mockito.mock(Label.class);
        Mockito.doReturn(
            Json.createObjectBuilder().add("color", "fefefe").build()
        ).when(second).json();
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Arrays.asList(first, second)).when(labels).iterate();
        MatcherAssert.assertThat(
            new IssueLabels.Smart(labels).findByColor("c0c0c0"),
            Matchers.allOf(
                Matchers.<Label>iterableWithSize(1),
                Matchers.hasItems(first)
            )
        );
    }

    /**
     * IssueLabels.Smart can check label's existence by name.
     * @throws Exception If some problem inside
     */
    @Test
    public void checksLabelExistenceByName() throws Exception {
        final Label first = Mockito.mock(Label.class);
        Mockito.doReturn("first").when(first).name();
        final Label second = Mockito.mock(Label.class);
        Mockito.doReturn("second").when(second).name();
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Arrays.asList(first, second)).when(labels).iterate();
        MatcherAssert.assertThat(
            new IssueLabels.Smart(labels).contains("first"),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new IssueLabels.Smart(labels).contains("third"),
            Matchers.is(false)
        );
    }

    /**
     * IssueLabels.Smart can find label by name.
     * @throws Exception If some problem inside
     */
    @Test
    public void getsLabelByName() throws Exception {
        final Label first = Mockito.mock(Label.class);
        Mockito.doReturn("a").when(first).name();
        final Label second = Mockito.mock(Label.class);
        Mockito.doReturn("b").when(second).name();
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Arrays.asList(first, second)).when(labels).iterate();
        MatcherAssert.assertThat(
            new IssueLabels.Smart(labels).get("a").name(),
            Matchers.equalTo("a")
        );
    }

    /**
     * IssueLabels.Smart can throw when label is absent.
     * @throws Exception If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenLabelIsAbsent() throws Exception {
        final IssueLabels labels = Mockito.mock(IssueLabels.class);
        Mockito.doReturn(Collections.emptyList()).when(labels).iterate();
        new IssueLabels.Smart(labels).get("something");
    }

}
