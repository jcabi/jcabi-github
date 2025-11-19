/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.GitHub;
import com.jcabi.github.Organization;
import com.jcabi.github.PublicMembers;
import com.jcabi.github.User;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock GitHub organization.
 *
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
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
    public GitHub github() {
        return new MkGitHub(this.storage, this.self);
    }

    @Override
    public String login() {
        return this.self;
    }

    @Override
    public JsonObject json() {
        return Json.createObjectBuilder()
            .add(MkOrganization.LOGIN_KEY, this.self)
            .add("id", Integer.toString(MkOrganization.RAND.nextInt()))
            .add("name", "github")
            .add("company", "GitHub")
            .add("blog", "https://github.com/blog")
            .add("location", "San Francisco")
            .add("email", "octocat@github.com")
            .add("public_repos", MkOrganization.RAND.nextInt())
            .add("public_gists", MkOrganization.RAND.nextInt())
            .add("total_private_repos", MkOrganization.RAND.nextInt())
            .add("owned_private_repos", MkOrganization.RAND.nextInt())
            .add("followers", MkOrganization.RAND.nextInt())
            .add("following", MkOrganization.RAND.nextInt())
            .add("url", "https://github.com/orgs/cat")
            .add("repos_url", "https://github.com/orgs/cat/repos")
            .add("events_url", "https://github.com/orgs/cat/events")
            .add("html_url", "https://github.com/cat")
            .add("created_at", new GitHub.Time().toString())
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
                    .xpath(this.xpath().concat("/members"))
                    .add("member")
                    .add(MkOrganization.LOGIN_KEY).set(user.login()).up()
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
