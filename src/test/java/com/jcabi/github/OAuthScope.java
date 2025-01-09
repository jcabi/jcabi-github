/**
 * Copyright (c) 2013-2025, jcabi.com
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
 * @author Jason Wong (super132j@yahoo.com)
 * @version $Id$
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
