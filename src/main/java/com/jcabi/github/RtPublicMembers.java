/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Public members of a GitHub organization.
 * @see <a href="https://developer.github.com/v3/orgs/members/">Organization Members API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "organization" })
public final class RtPublicMembers implements PublicMembers {
    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Organization.
     */
    private final transient Organization organization;

    /**
     * Public ctor.
     * @param req Request
     * @param organ Organization
     */
    public RtPublicMembers(
        final Request req,
        final Organization organ
    ) {
        this.entry = req;
        this.request = req.uri()
            .path("/orgs")
            .path(organ.login())
            .path("public_members")
            .back();
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
        this.request
            .uri().path(user.login()).back()
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public void publicize(
        final User user
    ) throws IOException {
        this.request
            .uri().path(user.login()).back()
            .method(Request.PUT)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<User> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtUser(
                this.organization.github(),
                this.entry,
                object.getString("login")
            )
        );
    }

    @Override
    public boolean contains(
        final User user
    ) throws IOException {
        return this.request
            .uri().path(user.login()).back()
            .method(Request.GET)
            .fetch().as(RestResponse.class)
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
            )
        )
            .status() == HttpURLConnection.HTTP_NO_CONTENT;
    }
}
