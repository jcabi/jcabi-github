/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.EqualsAndHashCode;

/**
 * GitHub user.
 *
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
@SuppressWarnings("PMD.TooManyMethods")
final class RtUser implements User {
    /**
     * Path for the notifications resource.
     */
    private static final String NOTIF_PATH = "notifications";

    /**
     * GitHub.
     */
    private final transient GitHub ghub;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Login of the user.
     */
    private final transient String self;

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     */
    RtUser(
        final GitHub github,
        final Request req
    ) {
        this(github, req, "");
    }

    /**
     * Public ctor.
     * @param github GitHub
     * @param req Request
     * @param login User identity/identity
     */
    RtUser(
        final GitHub github,
        final Request req,
        final String login
    ) {
        this.ghub = github;
        if (login.isEmpty()) {
            this.request = req.uri().path("/user").back();
        } else {
            this.request = req.uri().path("/users").path(login).back();
        }
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
    public String login() throws IOException {
        final String login;
        if (this.self.isEmpty()) {
            login = this.json().getString("login");
        } else {
            login = this.self;
        }
        return login;
    }

    @Override
    public UserOrganizations organizations() {
        return new RtUserOrganizations(this.ghub, this.ghub.entry(), this);
    }

    @Override
    public PublicKeys keys() {
        return new RtPublicKeys(this.ghub.entry(), this);
    }

    @Override
    public UserEmails emails() {
        return new RtUserEmails(this.ghub.entry());
    }

    @Override
    public Notifications notifications() {
        return new RtNotifications(
            this.github().entry().uri().path(RtUser.NOTIF_PATH).back()
        );
    }

    @Override
    public void markAsRead(final Date lastread) throws IOException {
        this.github().entry().uri()
            .path(RtUser.NOTIF_PATH)
            .queryParam(
                "last_read_at",
                DateTimeFormatter.ISO_INSTANT.format(lastread.toInstant())
            ).back()
            .method(Request.PUT)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_RESET);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(
        final JsonObject json)
        throws IOException {
        new RtJson(this.request).patch(json);
    }
}
