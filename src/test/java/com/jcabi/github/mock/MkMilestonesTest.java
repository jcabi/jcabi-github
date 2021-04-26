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
package com.jcabi.github.mock;

import com.jcabi.github.Milestone;
import com.jcabi.github.Milestones;
import com.jcabi.github.Repo;
import com.jcabi.immutable.ArrayMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test class for MkMilestones.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 */
final class MkMilestonesTest {

    /**
     * This tests that MkMilestones can return its owner repo.
     * @throws Exception - if something goes wrong.
     */
    @Test
    void returnsRepo() throws Exception {
        final Repo repo = new MkGithub().randomRepo();
        final Repo owner = repo.milestones().repo();
        MatcherAssert.assertThat(repo, Matchers.is(owner));
    }

    /**
     * This tests that MkMilestones can create a MkMilestone.
     * @throws Exception - if something goes wrong.
     */
    @Test
    void createsMilestone() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        final Milestone milestone = milestones.create("test milestone");
        MatcherAssert.assertThat(milestone, Matchers.notNullValue());
        MatcherAssert.assertThat(
            milestones.create("another milestone"),
            Matchers.notNullValue()
        );
    }

    /**
     * This tests that MkMilestones can return a certain MkMilestone, by number.
     * @throws Exception - if something goes wrong.
     */
    @Test
    void getsMilestone() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        final Milestone created = milestones.create("test");
        MatcherAssert.assertThat(
            milestones.get(created.number()),
            Matchers.notNullValue()
        );
    }
    /**
     * This tests that MkMilestones can remove a certain MkMilestone, by number.
     * @throws Exception - if something goes wrong.
     */
    @Test
    void removesMilestone() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        final Milestone created = milestones.create("testTitle");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<String, String>()),
            Matchers.<Milestone>iterableWithSize(1)
        );
        milestones.remove(created.number());
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<String, String>()),
            Matchers.<Milestone>iterableWithSize(0)
        );
    }
    /**
     * This tests that the iterate(Map<String, String> params)
     * method in MkMilestones works fine.
     * @throws Exception - if something goes wrong.
     */
    @Test
    void iteratesMilestones() throws Exception {
        final Milestones milestones = new MkGithub().randomRepo()
            .milestones();
        milestones.create("testMilestone");
        MatcherAssert.assertThat(
            milestones.iterate(new ArrayMap<String, String>()),
            Matchers.<Milestone>iterableWithSize(1)
        );
    }
}
