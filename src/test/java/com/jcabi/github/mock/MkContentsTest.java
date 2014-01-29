/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.jcabi.github.Contents;
import com.jcabi.github.Repo;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link MkContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
public final class MkContentsTest {
    /**
     * MkContents can fetch the default branch readme file.
     * @throws Exception if some problem inside
     */
    @Test
    public void canFetchReadmeFile() throws Exception {
        final Contents contents = MkContentsTest.repo().contents();
        MatcherAssert.assertThat(
            contents.readme(),
            Matchers.notNullValue()
        );
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     * @todo #314 MkContents should support the creation of mock contents.
     *  This method should create a new instance of MkContent. Do not
     *  forget to implement a unit test for it here and remove the Ignore
     *  annotation.
     */
    @Test
    @Ignore
    public void canCreateFile() throws Exception {
        //To be implemented.
    }

    /**
     * MkContents should be able to create new files.
     *
     * @throws Exception if some problem inside
     * @todo #311 MkContents should support the removal of mock contents.
     *  This method should return a new instance of MkCommit. Do not
     *  forget to implement a unit test for it here and remove the Ignore
     *  annotation.
     */
    @Test
    @Ignore
    public void canRemoveFile() throws Exception {
        //To be implemented.
    }

    /**
     * Create a repo to work with.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        return new MkGithub().repos().create(
            Json.createObjectBuilder().add("name", "test").build()
        );
    }
}
