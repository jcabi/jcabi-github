/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
