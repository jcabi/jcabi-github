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

import com.jcabi.aspects.Immutable;
import com.jcabi.github.Assignees;
import com.jcabi.github.Coordinates;
import com.jcabi.github.User;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Mock for Github Assignees.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.7
 */
@Immutable
final class MkAssignees implements Assignees {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     */
    MkAssignees(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
    }

    @Override
    public Iterable<User> iterate() {
        final Set<User> assignees = new HashSet<User>();
        assignees.add(new MkUser(this.storage, this.self));
        final Iterable<User> collaborators = new MkIterable<User>(
            this.storage,
            String.format("%s/user", this.xpath()),
            new MkIterable.Mapping<User>() {
                @Override
                public User map(final XML xml) {
                    return new MkUser(
                            MkAssignees.this.storage,
                            xml.xpath("login/text()").get(0)
                        );
                }
            }
        );
        for (final User collab : collaborators) {
            assignees.add(collab);
        }
        return assignees;
    }

    @Override
    public boolean check(
        final String login
    ) {
        try {
            final List<String> xpath = this.storage.xml().xpath(
                String.format("%s/user/login/text()", this.xpath())
            );
            return this.self.equalsIgnoreCase(login) || (
                !xpath.isEmpty()
                    && StringUtils.equalsIgnoreCase(login, xpath.get(0))
                );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * XPath of the Collaborators element in the XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/collaborators",
            this.coords
        );
    }
}
