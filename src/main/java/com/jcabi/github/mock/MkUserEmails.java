/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.aspects.Loggable;
import com.jcabi.github.UserEmails;
import com.jcabi.xml.XML;
import java.io.IOException;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github User Emails.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
final class MkUserEmails implements UserEmails {

    /**
     * Mapping.
     */
    private static final MkIterable.Mapping<String> MAPPING =
        new MkIterable.Mapping<String>() {
            @Override
            public String map(final XML xml) {
                return xml.xpath("./text()").get(0);
            }
        };

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @throws IOException If there is any I/O problem
     */
    MkUserEmails(
        @NotNull(message = "stg can't be NULL") final MkStorage stg,
        @NotNull(message = "login can't be NULL") final String login
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.storage.apply(
            new Directives().xpath(this.userXpath()).addIf("emails")
        );
    }

    @Override
    @NotNull(message = "JSON is never NULL")
    public JsonObject json() throws IOException {
        return new JsonNode(
            this.storage.xml().nodes(this.xpath()).get(0)
        ).json();
    }

    @Override
    @NotNull(message = "Iterable is never NULL")
    public Iterable<String> iterate() {
        return new MkIterable<String>(
            this.storage,
            String.format("%s/email", this.xpath()),
            MkUserEmails.MAPPING
        );
    }

    @Override
    @NotNull(message = "Iterable can't be NULL")
    public Iterable<String> add(
        @NotNull(message = "emails can't be NULL") final Iterable<String> emails
    ) throws IOException {
        this.storage.lock();
        try {
            final Directives directives = new Directives().xpath(this.xpath());
            for (final String email : emails) {
                directives.add("email").set(email).up();
            }
            this.storage.apply(directives);
        } finally {
            this.storage.unlock();
        }
        return emails;
    }

    @Override
    public void remove(
        @NotNull(message = "emails should not be NULL")
        final Iterable<String> emails
    ) throws IOException {
        final Directives directives = new Directives();
        for (final String email : emails) {
            directives.xpath(
                String.format("%s/email[.='%s']", this.xpath(), email)
            ).remove();
        }
        this.storage.apply(directives);
    }

    /**
     * XPath of user element in XML tree.
     * @return XPath
     */
    @NotNull(message = "User's xpath is never NULL")
    private String userXpath() {
        return String.format("/github/users/user[login='%s']", this.self);
    }

    /**
     * XPath of user emails element in XML tree.
     * @return XPath
     */
    @NotNull(message = "xpath is never NULL")
    private String xpath() {
        return String.format("%s/emails", this.userXpath());
    }
}
