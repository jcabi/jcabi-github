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
 * @see <a href="https://developer.github.com/v3/oauth/#scopes">Github OAuth
 * scopes</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OAuthScope {
    /**
     * The Github OAuth scopes required.
     */
    Scope[] value();

    /**
     * The enum represents the available OAuth scopes.
     */
    public enum Scope {
        /**
         * Represents "no scope" scope.
         */
        NO_SCOPE,
        /**
         * Represents "user" scope.
         */
        USER,
        /**
         * Represents "user:email" scope.
         */
        USER_EMAIL,
        /**
         * Represents "user:follow" scope.
         */
        USER_FOLLOW,
        /**
         * Represents "public_repo" scope.
         */
        PUBLIC_REPO,
        /**
         * Represents "repo" scope.
         */
        REPO,
        /**
         * Represents "repo_deployment" scope.
         */
        REPO_DEPLOYMENT,
        /**
         * Represents "repo_status" scope.
         */
        REPO_STATUS,
        /**
         * Represents "delete_repo" scope.
         */
        DELETE_REPO,
        /**
         * Represents "notifications" scope.
         */
        NOTIFICATIONS,
        /**
         * Represents "gist" scope.
         */
        GIST,
        /**
         * Represents "read:repo_hook" scope.
         */
        READ_REPO_HOOK,
        /**
         * Represents "write:repo_hook" scope.
         */
        WRITE_REPO_HOOK,
        /**
         * Represents "admin:repo_hook" scope.
         */
        ADMIN_REPO_HOOK,
        /**
         * Represents "admin:org_hook" scope.
         */
        ADMIN_ORG_HOOK,
        /**
         * Represents "read:org" scope.
         */
        READ_ORG,
        /**
         * Represents "write:org" scope.
         */
        WRITE_ORG,
        /**
         * Represents "admin:org" scope.
         */
        ADMIN_ORG,
        /**
         * Represents "read:public_key" scope.
         */
        READ_PUBLIC_KEY,
        /**
         * Represents "write:public_key" scope.
         */
        WRITE_PUBLIC_KEY,
        /**
         * Represents "admin:public_key" scope.
         */
        ADMIN_PUBLIC_KEY;
    }
}
