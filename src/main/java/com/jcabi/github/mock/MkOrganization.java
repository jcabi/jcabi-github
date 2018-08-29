/**
 * Copyright (c) 2013-2018, jcabi.com
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
import com.jcabi.github.Github;
import com.jcabi.github.Organization;
import com.jcabi.github.PublicMembers;
import com.jcabi.github.User;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub organization.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self" })
public final class MkOrganization implements Organization {
    /**
     * Random generator.
     */
    private static final Random RAND = new SecureRandom();

    /**
     * Login key in key-value pair.
     */
    private static final String LOGIN_KEY = "login";

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Username of the organization.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login Username of organization
     */
    public MkOrganization(
        final MkStorage stg,
        final String login
    ) {
        this.storage = stg;
        this.self = login;
    }

    @Override
    public Github github() {
        return new MkGithub(this.storage, this.self);
    }

    @Override
    public String login() {
        return this.self;
    }

    @Override
    public JsonObject json() {
        return Json.createObjectBuilder()
            .add(LOGIN_KEY, this.self)
            .add("id", Integer.toString(RAND.nextInt()))
            .add("name", "github")
            .add("company", "GitHub")
            .add("blog", "https://github.com/blog")
            .add("location", "San Francisco")
            .add("email", "octocat@github.com")
            .add("public_repos", RAND.nextInt())
            .add("public_gists", RAND.nextInt())
            .add("total_private_repos", RAND.nextInt())
            .add("owned_private_repos", RAND.nextInt())
            .add("followers", RAND.nextInt())
            .add("following", RAND.nextInt())
            .add("url", "https://github.com/orgs/cat")
            .add("repos_url", "https://github.com/orgs/cat/repos")
            .add("events_url", "https://github.com/orgs/cat/events")
            .add("html_url", "https://github.com/cat")
            .add("created_at", new Github.Time().toString())
            .add("type", "Organization")
            .build();
    }

    @Override
    public int compareTo(final Organization obj) {
        return this.login().compareTo(obj.login());
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new JsonPatch(this.storage)
            .patch(this.xpath(), json);
    }

    @Override
    public PublicMembers publicMembers() {
        return new MkPublicMembers(this.storage, this);
    }

    /**
     * Add the given user to this organization.
     * @param user User to add to the organization
     * @todo #1107:30min Implement the "Add team membership" API (see
     *  https://developer.github.com/v3/orgs/teams/#add-team-membership )
     *  (per https://developer.github.com/v3/orgs/members/#add-a-member ,
     *  you can't add a user directly to an org; you instead add them to one
     *  of that org's teams) and replace uses of this method with uses of that
     *  API (or downgrade this method to a convenience method for unit tests).
     */
    public void addMember(final User user) {
        try {
            this.storage.apply(
                new Directives()
                    .xpath(String.format("%s/members", this.xpath()))
                    .add("member")
                    .add(LOGIN_KEY).set(user.login()).up()
                    .add("public").set("false").up()
            );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format("/github/orgs/org[login='%s']", this.self);
    }
}
