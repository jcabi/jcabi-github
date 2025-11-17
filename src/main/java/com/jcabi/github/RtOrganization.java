/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * GitHub organization.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
final class RtOrganization implements Organization {

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Login of the organization.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     * @param login Organization login name
     */
    public RtOrganization(
        final GitHub github,
        final Request req,
        final String login
    ) {
        this.ghub = github;
        this.entry = req;
        this.request = req.uri()
            .path("/orgs")
            .path(login)
            .back();
        this.self = login;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public GitHub github() {
        return this.ghub;
    }

    @Override
    public String login() {
        return this.self;
    }

    @Override
    public PublicMembers publicMembers() {
        return new RtPublicMembers(this.entry, this);
    }

    @Override
    public int compareTo(
        final Organization other
    ) {
        return this.login().compareTo(other.login());
    }

    @Override
    public void patch(
        final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

}
