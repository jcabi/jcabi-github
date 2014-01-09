/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.jcabi.aspects.Loggable;
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;

/**
 * Github organizations.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "userobj", "organizations" })
final class RtOrganizations implements Organizations {

    /**
     * User.
     */
    private final transient User userobj;

    /**
     * Organizations.
     */
    private final transient Collection<Organization> organizations;

    /**
     * Public constructor.
     * @param usr User
     * @param orgs A java.util.Collection of Organization objects
     */
    public RtOrganizations(final User usr,
        final Collection<Organization> orgs) {
        this.userobj = usr;
        this.organizations = orgs;
    }

    @Override
    public User user() {
        return this.userobj;
    }

    @Override
    public Organization get(final int orgid) {
        Organization return_org = null;
        if (this.organizations != null) {
            for (Organization org : this.organizations) {
                if (org.orgId() == orgid) {
                    return_org = org;
                    break;
                }
            }
        }
        return return_org;
    }

    @Override
    public Iterable<Organization> iterate() {
        Collection<Organization> returnit = this.organizations;
        if (this.organizations == null) {
            returnit = new LinkedList<Organization>();
        }
        return returnit;
    }
}
