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
        NO_SCOPE,
        USER,
        USER_EMAIL,
        USER_FOLLOW,
        PUBLIC_REPO,
        REPO,
        REPO_DEPLOYMENT,
        REPO_STATUS,
        DELETE_REPO,
        NOTIFICATIONS,
        GIST,
        READ_REPO_HOOK,
        WRITE_REPO_HOOK,
        ADMIN_REPO_HOOK,
        ADMIN_ORG_HOOK,
        READ_ORG,
        WRITE_ORG,
        ADMIN_ORG,
        READ_PUBLIC_KEY,
        WRITE_PUBLIC_KEY,
        ADMIN_PUBLIC_KEY;
    }
}
