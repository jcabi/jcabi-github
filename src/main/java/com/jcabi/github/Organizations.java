/**
 * Copyright (c) 2013-2014, JCabi.com
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
 * Github organizations.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 */
@Immutable
public interface Organizations {

    /**
     * Github we're in.
     * @return Github
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Get its owner.
     * @return User
     */
    @NotNull(message = "user is never NULL")
    User user();

    /**
     * Get specific organization by name.
     * @param login Login name of the organization.
     * @return Organization
     * @see <a href="http://developer.github.com/v3/orgs/#get-an-organization">Get a Single Organization</a>
     */
    @NotNull(message = "organization is never NULL")
    Organization get(String login);

    /**
     * Iterate them all.
     * @return Iterator of Organizations
     * @see <a href="http://developer.github.com/v3/orgs/#list-user-organizations">List Organizations</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Organization> iterate();

    /**
     * Iterate organizations of particular user.
     * All public organizations for an unauthenticated user or
     * private and public organizations for authenticated users
     * @param username Name of user
     * @return Iterator of Organizations
     * @see <a href="http://developer.github.com/v3/orgs/#list-user-organizations">List Organizations</a>
     * @since 0.8
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Organization> iterate(String username);
}
