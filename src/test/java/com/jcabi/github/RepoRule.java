/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Tv;
import com.jcabi.log.Logger;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class which provides convenient methods for repo managing.
 * @since 0.1
 */
public final class RepoRule {

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
