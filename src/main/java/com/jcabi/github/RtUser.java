/**
 * Copyright (c) 2013-2017, jcabi.com
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
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import lombok.EqualsAndHashCode;

/**
 * Github user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "request" })
@SuppressWarnings("PMD.TooManyMethods")
final class RtUser implements User {

    /**
     * Github.
     */
    private final transient Github ghub;

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
     * @param github Github
     * @param req Request
     */
    RtUser(
        final Github github,
        final Request req
    ) {
        this(github, req, "");
    }

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param login User identity/identity
     */
    RtUser(
        final Github github,
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
    public Github github() {
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
    public List<Notification> notifications() throws IOException {
        final List<Notification> list =
            new LinkedList<Notification>();
        final JsonResponse resp = this.github().entry().uri()
            .path("notifications")
            .back()
            .fetch()
            .as(JsonResponse.class);
        final JsonArray array = resp.json().readArray();
        for (final JsonValue value : array) {
            final JsonObject notif = (JsonObject) value;
            list.add(this.createNotification(notif));
        }
        return list;
    }

    @Override
    public void markAsRead(final Date lastread) throws IOException {
        this.github().entry().uri()
            .path("notifications")
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

    /**
     * Creates RtNotification object with the id from notifobj.
     * @param notifobj JSON object with notification data.
     * @return RtNotification object with the id from notifobj.
     */
    private Notification createNotification(final JsonObject notifobj) {
        return new RtNotification(
            Long.parseLong(notifobj.getString("id"))
        );
    }
}
