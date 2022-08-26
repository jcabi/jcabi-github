/**
 * Copyright (c) 2013-2022, jcabi.com
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

import com.jcabi.github.Organization;
import com.jcabi.github.PublicMembers;
import com.jcabi.github.User;
import java.io.IOException;
import org.xembly.Directives;

/**
 * Mock for public members of a GitHub organization.
 *
 * @author Chris Rebert (github@chrisrebert.com)
 * @version $Id$
 * @see <a href="https://developer.github.com/v3/orgs/members/">Organization Members API</a>
 * @since 0.24
 */
public final class MkPublicMembers implements PublicMembers {
    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Organization.
     */
    private final transient Organization organization;

    /**
     * Public ctor.
     * @param stg Storage
     * @param organ Organization
     */
    public MkPublicMembers(
        final MkStorage stg,
        final Organization organ
    ) {
        this.storage = stg;
        this.organization = organ;
    }

    @Override
    public Organization org() {
        return this.organization;
    }

    @Override
    public void conceal(
        final User user
    ) throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath(user))
                .set("false")
        );
    }

    @Override
    public void publicize(
        final User user
    ) throws IOException {
        this.storage.apply(
            new Directives()
                .xpath(this.xpath(user))
                .set("true")
        );
    }

    @Override
    public Iterable<User> iterate() {
        return new MkIterable<>(
            this.storage,
            String.format("%s/member[public='true']/login", this.xpath()),
            xml -> new MkUser(
                this.storage,
                xml.xpath("text()").get(0)
            )
        );
    }

    @Override
    public boolean contains(
        final User user
    ) {
        boolean result = false;
        for (final User member : this.iterate()) {
            if (member.equals(user)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * XPath of publicity of user's membership in the organization.
     * @param user User
     * @return XPath
     * @throws IOException If there is an I/O problem
     */
    private String xpath(final User user) throws IOException {
        return String.format(
            "%s/member[login='%s']/public",
            this.xpath(),
            user.login()
        );
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/orgs/org[login='%s']/members",
            this.organization.login()
        );
    }
}
