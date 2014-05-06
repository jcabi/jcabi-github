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

import com.jcabi.immutable.ArrayMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration case for {@link Milestones}.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 *
 * @todo #1:30min Implement integration tests for Milestones.
 *  Now these tests are ignored
 */
public final class RtMilestonesITCase {

    /**
     * RtMilestones can iterate milestones.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void iteratesIssues() throws Exception {
        //
    }

    /**
     * RtMilestones can create a new milestone.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void createsNewMilestone() throws Exception {
        //
    }

    /**
     * RtMilestones can remove a milestone.
     * @throws Exception if some problem inside
     */
    @Test
    public void deleteMilestone() throws Exception {
        final Milestones milestones = milestones();
        final Milestone milestone = milestones.create("a milestones");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<String, String>()),
            Matchers.hasItem(milestone)
        );
        milestones.remove(milestone.number());
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<String, String>()),
            Matchers.not(Matchers.hasItem(milestone))
        );
    }
    /**
     * Create and return milestones to test.
     * @return Milestones
     * @throws Exception If some problem inside
     */
    private static Milestones milestones() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).repos().get(
            new Coordinates.Simple(System.getProperty("failsafe.github.repo"))
        ).milestones();
    }
}

