/**
 * Copyright (c) 2013-2024, jcabi.com
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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link RtCheck}.
 *
 * @author Volodya Lombrozo (volodya.lombrozo@gmail.com)
 * @version $Id$
 * @since 1.5.0
 */
public final class RtCheckTest {

    /**
     * RtCheck can check successful state.
     */
    @Test
    public void checksSuccessfulState() {
        MatcherAssert.assertThat(
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(true)
        );
    }

    /**
     * RtCheck can check not successful state if in progress.
     */
    @Test
    public void checksNotSuccessfulStateIfInProgress() {
        MatcherAssert.assertThat(
            new RtCheck(
                Check.Status.IN_PROGRESS,
                Check.Conclusion.SUCCESS
            ).successful(),
            Matchers.is(false)
        );
    }

    /**
     * RtCheck can check not successful state if cancelled.
     */
    @Test
    public void checksNotSuccessfulState() {
        MatcherAssert.assertThat(
            new RtCheck(
                Check.Status.COMPLETED,
                Check.Conclusion.CANCELLED
            ).successful(),
            Matchers.is(false)
        );
    }

    /**
     * Can not create RtCheck with unexisting status.
     */
    @Test
    public void createsWithUnexistingStatus() {
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> new RtCheck(
                "unexisting",
                "success"
            ).successful()
        );
    }

    /**
     * Can not create RtCheck with unexisting conclusion.
     */
    @Test
    public void createsWithUnexistingConclusion() {
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> new RtCheck(
                "completed",
                "unexist"
            ).successful()
        );
    }
}
