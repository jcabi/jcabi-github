/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an integration test case to indicate required OAuth scopes to run
 * such case.
 * @todo #975:30min Now all IT cases are annotated with OAuthScope annotation
 *  to marked down its required scopes. A checker needs to be implemented to
 *  check if the supplied account can fulfill the IT case requirement before
 *  it is run and make the case fail if the required OAuth scopes is not
 *  present.
 * @see <a href="https://developer.github.com/v3/oauth/#scopes">GitHub OAuth
 * scopes</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OAuthScope {
    OAuthScope.Scope[] value();

    enum Scope {
        /**
         * No scope.
         */
        NO_SCOPE,
        /**
         * User scope.
         */
        USER,
        /**
         * User email scope.
         */
        USER_EMAIL,
        /**
         * User follow scope.
         */
        USER_FOLLOW,
        /**
         * Public repo scope.
         */
        PUBLIC_REPO,
        /**
         * Repo scope.
         */
        REPO,
        /**
         * Repo deployment scope.
         */
        REPO_DEPLOYMENT,
        /**
         * Repo status scope.
         */
        REPO_STATUS,
        /**
         * Delete repo scope.
         */
        DELETE_REPO,
        /**
         * Notifications scope.
         */
        NOTIFICATIONS,
        /**
         * Gist scope.
         */
        GIST,
        /**
         * Read repo hook scope.
         */
        READ_REPO_HOOK,
        /**
         * Write repo hook scope.
         */
        WRITE_REPO_HOOK,
        /**
         * Admin repo hook scope.
         */
        ADMIN_REPO_HOOK,
        /**
         * Admin org hook scope.
         */
        ADMIN_ORG_HOOK,
        /**
         * Read org scope.
         */
        READ_ORG,
        /**
         * Write org scope.
         */
        WRITE_ORG,
        /**
         * Admin org scope.
         */
        ADMIN_ORG,
        /**
         * Read public key scope.
         */
        READ_PUBLIC_KEY,
        /**
         * Write public key scope.
         */
        WRITE_PUBLIC_KEY,
        /**
         * Admin public key scope.
         */
        ADMIN_PUBLIC_KEY;
    }
}
