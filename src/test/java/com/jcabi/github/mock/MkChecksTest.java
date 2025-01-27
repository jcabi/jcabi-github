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
package com.jcabi.github.mock;

import com.jcabi.github.Check;
import com.jcabi.github.Pull;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link MkChecks}.
 *
 * @since 1.6.1
 */
public final class MkChecksTest {

    /**
     * Pull request.
     */
    private transient Pull pull;

    /**
     * Set up.
     * @throws IOException If some problem with I/O.
     */
    @Before
    public void setUp() throws IOException {
        this.pull = new MkGithub()
            .randomRepo()
            .pulls()
            .create("Test PR", "abcdef8", "abcdef9");
    }

    /**
     * MkChecks can return empty checks by default.
     * @throws IOException If some problem with I/O.
     */
    @Test
    public void returnsEmptyChecksByDefault() throws IOException {
        MatcherAssert.assertThat(
            ((MkChecks) this.pull.checks()).all(),
            Matchers.empty()
        );
    }

    /**
     * MkChecks can create a check.
     * @throws IOException If some problem with I/O.
     */
    @Test
    public void createsCheck() throws IOException {
        final MkChecks checks = (MkChecks) this.pull.checks();
        final Check check = checks.create(
            Check.Status.COMPLETED,
            Check.Conclusion.SUCCESS
        );
        MatcherAssert.assertThat(
            checks.all(),
            Matchers.hasSize(1)
        );
        final Check next = checks.all().iterator().next();
        MatcherAssert.assertThat(
            check,
            Matchers.equalTo(next)
        );
        MatcherAssert.assertThat(
            next.successful(),
            Matchers.is(true)
        );
    }
}
