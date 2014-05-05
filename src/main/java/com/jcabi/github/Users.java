/**
 * Copyright (c) 2013-2014, jcabi.com
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

import com.jcabi.aspects.Immutable;
import javax.validation.constraints.NotNull;

/**
 * Github users.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/gists/">Gists API</a>
 */
@Immutable
public interface Users {

    /**
     * Github we're in.
     * @return Github
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Get myself.
     * @return Myself
     */
    @NotNull(message = "user is never NULL")
    User self();

    /**
     * Get user by login.
     * @param login Login of it
     * @return User
     * @see <a href="http://developer.github.com/v3/users/#get-a-single-user">Get a Single User</a>
     */
    @NotNull(message = "user is never NULL")
    User get(@NotNull(message = "login is never NULL") String login);

    /**
     * Iterate all users, starting with the one you've seen already.
     * @param login Login you've seen already
     * @return Iterator of gists
     * @see <a href="http://developer.github.com/v3/users/#get-all-users">Get All Users</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<User> iterate(@NotNull(message = "login is never NULL")
        String login);

}
