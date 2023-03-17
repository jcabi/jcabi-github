/**
 * Copyright (c) 2013-2023, jcabi.com
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
import com.jcabi.log.Logger;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Utility class which provides convenient methods for repo managing.
 * @author Andrej Istomin (andrej.istomin.ikeen@gmail.com)
 * @version $Id$
 */
public final class RepoRule implements TestRule {

    @Override
    public Statement apply(final Statement statement,
        final Description description) {
        return new Statement() {
            @Override
            // @checkstyle IllegalThrowsCheck (1 line)
            public void evaluate() throws Throwable {
                statement.evaluate();
            }
        };
    }

    /**
     * Create new repo for tests.
     * @param repos Repos
     * @return Repo
     * @throws IOException If error occurred.
     */
    public Repo repo(final Repos repos) throws IOException {
        final Repos.RepoCreate settings = new Repos.RepoCreate(
            "foo",
            false
        ).withAutoInit(true);
        int attempts = 0;
        Repo repo = null;
        while (repo == null) {
            final Repos.RepoCreate request = settings.withName(
                RandomStringUtils.randomAlphanumeric(Tv.TWENTY)
            );
            try {
                repo = repos.create(request);
            } catch (final AssertionError ex) {
                Logger.warn(
                    this, "Create repository failed. Message: %s",
                    ex.getMessage()
                );
                ++attempts;
                if (attempts > Tv.FIVE) {
                    throw new IllegalStateException(
                        String.format(
                            "Failed to created repository %s",
                            request.name()
                        ),
                        ex
                    );
                }
            }
        }
        return repo;
    }
}
