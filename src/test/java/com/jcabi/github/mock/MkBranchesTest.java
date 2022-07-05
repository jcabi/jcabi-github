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
package com.jcabi.github.mock;

import com.jcabi.github.Branch;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Repo;
import java.io.IOException;
import java.util.Iterator;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link MkBranches}.
 *
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
 */
public final class MkBranchesTest {
    /**
     * MkBranches can create a new branch.
     * @throws IOException if there is any I/O problem
     */
    @Test
    public void createsBranch() throws IOException {
        final String name = "my-new-feature";
        final String sha = "590e188e3d52a8da38cf51d3f9bf598bb46911af";
        final Repo repo = new MkGithub().randomRepo();
        final Branch branch = ((MkBranches) (repo.branches()))
            .create(name, sha);
        MatcherAssert.assertThat(branch.name(), Matchers.equalTo(name));
        MatcherAssert.assertThat(
            branch.commit().sha(),
            Matchers.equalTo(sha)
        );
        final Coordinates coords = branch.commit().repo().coordinates();
        MatcherAssert.assertThat(
            coords.user(),
            Matchers.equalTo(repo.coordinates().user())
        );
        MatcherAssert.assertThat(
            coords.repo(),
            Matchers.equalTo(repo.coordinates().repo())
        );
    }

    /**
     * MkBranches can iterate over the repo's branches.
     * @throws IOException if there is any I/O problem
     */
    @Test
    public void iteratesOverBranches() throws IOException {
        final MkBranches branches = (MkBranches) (new MkGithub().randomRepo()
            .branches());
        final String onename = "narf";
        final String onesha = "a86da33b875e8ecbaf75cefcf6d8957cbecb654e";
        branches.create(onename, onesha);
        final String twoname = "zort";
        final String twosha = "ba00fa4fe331c59736b87f52f760e1ccfb293b5f";
        branches.create(twoname, twosha);
        MatcherAssert.assertThat(
            branches.iterate(),
            Matchers.<Branch>iterableWithSize(2)
        );
        final Iterator<Branch> iter = branches.iterate().iterator();
        final Branch one = iter.next();
        MatcherAssert.assertThat(one.name(), Matchers.equalTo(onename));
        MatcherAssert.assertThat(
            one.commit().sha(),
            Matchers.equalTo(onesha)
        );
        final Branch two = iter.next();
        MatcherAssert.assertThat(two.name(), Matchers.equalTo(twoname));
        MatcherAssert.assertThat(
            two.commit().sha(),
            Matchers.equalTo(twosha)
        );
    }
}
