/**
 * Copyright (c) 2013-2014, jcabi.com
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
import com.jcabi.http.Response;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.response.JsonResponse;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.DatatypeConverter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.io.Charsets;

/**
 * Github user.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @see <a href="http://developer.github.com/v3/users/">User API</a>
 * @since 0.1
 */
@Immutable
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessivePublicCount" })
public interface User extends JsonReadable, JsonPatchable {

    /**
     * Github we're in.
     * @return Github
     * @since 0.4
     */
    @NotNull(message = "Github is never NULL")
    Github github();

    /**
     * Get his login.
     * @return Login name
     * @throws IOException If it fails
     */
    @NotNull(message = "login is never NULL")
    String login() throws IOException;

    /**
     * Get his organizations.
     * @return Organizations organizations
     */
    @NotNull(message = "organizations is never NULL")
    Organizations organizations();

    /**
     * Get his keys.
     * @return PublicKeys keys
     */
    @NotNull(message = "keys is never NULL")
    PublicKeys keys();

    /**
     * Get user's emails.
     * @return User's emails
     * @since 0.8
     */
    @NotNull(message = "user emails is never NULL")
    UserEmails emails();

    /**
     * Returns all notifications of a user. Wraps the call "List your
     *  notifications". See "List your notifications" at
     *  https://developer.github.com/v3/activity/notifications/
     * @see <a href="https://developer.github.com/v3/activity/notifications/#list-your-notifications">List your notifications</a>
     * @return Returns all notifications for this user.
     * @throws IOException Thrown, if an error during sending request and/or
     * receiving response occurs.
     */
    List<Notification> notifications() throws IOException;

    /**
     * Marks notifications as read.
     * @param lastread Describes the last point that notifications were
     *  checked.
     * @see <a href="https://developer.github.com/v3/activity/notifications/#mark-as-read">Mark as read</a>
     */
    void markAsRead(final Date lastread);

    /**
     * Smart user with extra features.
     * @todo #1:30min Implement methods to retrieve all values provided
     *  by Github for a single user, see:
     *  http://developer.github.com/v3/users/#get-a-single-user
     *  At the moment we implement just a few, but every data
     *  items should have its own method. Of course, every new item should
     *  be tested by a new unit test method.
     * @see <a href="http://developer.github.com/v3/users/#get-a-single-user">Get a Single User</a>
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "user", "jsn" })
    final class Smart implements User {
        /**
         * Encapsulated user.
         */
        private final transient User user;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param usr User
         */
        public Smart(
            @NotNull(message = "user can't be NULL") final User usr
        ) {
            this.user = usr;
            this.jsn = new SmartJson(usr);
        }

        /**
         * Get his ID.
         * @return Unique user ID
         * @throws IOException If it fails
         * @checkstyle MethodName (3 lines)
         */
        @SuppressWarnings("PMD.ShortMethodName")
        public int id() throws IOException {
            return this.user.json().getJsonNumber("id").intValue();
        }

        /**
         * Get his avatar URL.
         * @return URL of the avatar
         * @throws IOException If it fails
         */
        @NotNull(message = "URL is never NULL")
        public URL avatarUrl() throws IOException {
            return new URL(this.jsn.text("avatar_url"));
        }

        /**
         * Get his URL.
         * @return URL of the user
         * @throws IOException If it fails
         */
        @NotNull(message = "URL is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }

        /**
         * Get his name.
         * @return User name
         * @throws IOException If it fails
         */
        @NotNull(message = "Name is never NULL")
        public String name() throws IOException {
            final JsonObject json = this.json();
            if (!json.containsKey("name")) {
                throw new IllegalStateException(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "User %s doesn't have a name specified in his/her Github account; use #hasName() first.",
                        this.login()
                    )
                );
            }
            return json.getString("name");
        }

        /**
         * Check if user has name.
         * @return True if user has name
         * @throws IOException If it fails
         */
        public boolean hasName() throws IOException {
            return this.json().containsKey("name");
        }

        /**
         * Get his company.
         * @return Company name
         * @throws IOException If it fails
         */
        @NotNull(message = "Company is never NULL")
        public String company() throws IOException {
            return this.jsn.text("company");
        }

        /**
         * Get his location.
         * @return Location name
         * @throws IOException If it fails
         */
        @NotNull(message = "Location is never NULL")
        public String location() throws IOException {
            return this.jsn.text("location");
        }

        /**
         * Get his email.
         * @return Email
         * @throws IOException If it fails
         */
        @NotNull(message = "Email is never NULL")
        public String email() throws IOException {
            return this.jsn.text("email");
        }

        @Override
        @NotNull(message = "Github is never NULL")
        public Github github() {
            return this.user.github();
        }

        @Override
        @NotNull(message = "Login is never NULL")
        public String login() throws IOException {
            return this.user.login();
        }

        @Override
        @NotNull(message = "Organizations is never NULL")
        public Organizations organizations() {
            return this.user.organizations();
        }

        @Override
        @NotNull(message = "Keys is never NULL")
        public PublicKeys keys() {
            return this.user.keys();
        }

        @Override
        @NotNull(message = "Emails is never NULL")
        public UserEmails emails() {
            return this.user.emails();
        }

        /**
         * Returns this user's notifications.
         * @todo #913:30min Implement the call "List your notifications" as
         *  described at https://developer.github.com/v3/activity/notifications/
         * @return Notifications of the user
         * @throws IOException Thrown, if an error during sending request and/or
         * receiving response occurs.
         */
        @Override
        public List<Notification> notifications() throws IOException {
            final String pwd = "";
            final Response resp =
                new ApacheRequest("https://api.github.com/notifications")
                    .header(
                        HttpHeaders.AUTHORIZATION,
                        String.format(
                            "Basic %s",
                            DatatypeConverter.printBase64Binary(
                                String.format(
                                    "%s:%s",
                                    this.user,
                                    pwd
                                ).getBytes(Charsets.UTF_8)
                            )
                        )
                    ).fetch();
            final JsonResponse jsonresp = new JsonResponse(resp);
            final JsonArray jsnnotifs = jsonresp.json().readArray();
            final LinkedList<Notification> notifs =
                new LinkedList<Notification>();
            for (final JsonValue jsnnotif : jsnnotifs) {
                final JsonObject notifobj = (JsonObject) jsnnotif;
                final Notification notif = new RtNotification(
                    notifobj.getInt("id")
                );
                notifs.add(notif);
            }
            return notifs;
        }

        /**
         * Marks notifications of a user as read.
         * @todo #913:30min Implement the call "Mark as read" as described at
         *  https://developer.github.com/v3/activity/notifications/#mark-as-read
         * @param lastread Describes the last point that notifications were
         *  checked.
         */
        @Override
        public void markAsRead(final Date lastread) {
            // Will be implemented later
        }

        @Override
        @NotNull(message = "Json is never NULL")
        public JsonObject json() throws IOException {
            return this.user.json();
        }

        @Override
        public void patch(
            @NotNull(message = "Json can't be NULL") final JsonObject json
        ) throws IOException {
            this.user.patch(json);
        }

        /**
         * Returns the value of gravatar_id property of User's JSON.
         * @return The 'gravatar_id' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Gravatar is never NULL")
        public String gravatar() throws IOException {
            return this.jsn.text("gravatar_id");
        }

        /**
         * Returns the value of html_url property of User's JSON.
         * @return The 'html_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Html URL is never NULL")
        public String htmlUrl() throws IOException {
            return this.jsn.text("html_url");
        }

        /**
         * Returns the value of followers_url property of User's JSON.
         * @return The 'followers_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Followers URL is never NULL")
        public String follwersUrl() throws IOException {
            return this.jsn.text("followers_url");
        }

        /**
         * Returns the value of following_url property of User's JSON.
         * @return The 'following_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Following URL is never NULL")
        public String followingUrl() throws IOException {
            return this.jsn.text("following_url");
        }

        /**
         * Returns the value of gists_url property of User's JSON.
         * @return The 'gists_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Gists URL is never NULL")
        public String gistsUrl() throws IOException {
            return this.jsn.text("gists_url");
        }

        /**
         * Returns the value of starred_url property of User's JSON.
         * @return The 'starred_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Starred URL is never NULL")
        public String starredUrl() throws IOException {
            return this.jsn.text("starred_url");
        }

        /**
         * Returns the value of subscriptions_url property of User's JSON.
         * @return The 'subscriptions_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Subscriptions URL is never NULL")
        public String subscriptionsUrl() throws IOException {
            return this.jsn.text("subscriptions_url");
        }

        /**
         * Returns the value of organizations_url property of User's JSON.
         * @return The 'organizations_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Organizations URL is never NULL")
        public String organizationsUrl() throws IOException {
            return this.jsn.text("organizations_url");
        }

        /**
         * Returns the value of repos_url property of User's JSON.
         * @return The 'repos_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Repos URL is never NULL")
        public String reposUrl() throws IOException {
            return this.jsn.text("repos_url");
        }

        /**
         * Returns the value of events_url property of User's JSON.
         * @return The 'events_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Events URL is never NULL")
        public String eventsUrl() throws IOException {
            return this.jsn.text("events_url");
        }

        /**
         * Returns the value of received_events_url property of User's JSON.
         * @return The 'received_events_url' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Received enets URL is never NULL")
        public String receivedEventsUrl() throws IOException {
            return this.jsn.text("received_events_url");
        }

        /**
         * Returns the value of type property of User's JSON.
         * @return The 'type' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Type is never NULL")
        public String type() throws IOException {
            return this.jsn.text("type");
        }

        /**
         * Returns the value of site_admin property of User's JSON.
         * @return The 'site_admin' property value.
         * @throws IOException If any I/O error occurs.
         */
        public boolean siteAdmin() throws IOException {
            return "true".equals(this.jsn.text("site_admin"));
        }

        /**
         * Returns the value of blog property of User's JSON.
         * @return The 'blog' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Blog is never NULL")
        public String blog() throws IOException {
            return this.jsn.text("blog");
        }

        /**
         * Returns the value of hireable property of User's JSON.
         * @return The 'hireable' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Hireable is never NULL")
        public boolean hireable() throws IOException {
            return "true".equals(this.jsn.text("hireable"));
        }

        /**
         * Returns the value of bio property of User's JSON.
         * @return The 'bio' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Bio is never NULL")
        public String bio() throws IOException {
            return this.jsn.text("bio");
        }

        /**
         * Returns the value of public_repos property of User's JSON.
         * @return The 'public_repos' property value.
         * @throws IOException If any I/O error occurs.
         */
        public int publicRepos() throws IOException {
            return Integer.parseInt(this.jsn.text("public_repos"));
        }

        /**
         * Returns the value of public_gists property of User's JSON.
         * @return The 'public_gists' property value.
         * @throws IOException If any I/O error occurs.
         */
        public int publicGists() throws IOException {
            return Integer.parseInt(this.jsn.text("public_gists"));
        }

        /**
         * Returns the value of followers property of User's JSON.
         * @return The 'followers' property value.
         * @throws IOException If any I/O error occurs.
         */
        public int followersCount() throws IOException {
            return Integer.parseInt(this.jsn.text("followers"));
        }

        /**
         * Returns the value of following property of User's JSON.
         * @return The 'following' property value.
         * @throws IOException If any I/O error occurs.
         */
        public int followingCount() throws IOException {
            return Integer.parseInt(this.jsn.text("following"));
        }

        /**
         * Returns the value of created_at property of User's JSON.
         * @return The 'created_at' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Created time is never NULL")
        public Github.Time created() throws IOException {
            try {
                return new Github.Time(this.jsn.text("created_at"));
            } catch (final ParseException ex) {
                throw new IllegalArgumentException(
                    "Cannot parse value of 'created_at' property",
                    ex
                );
            }
        }

        /**
         * Returns the value of updated_at property of User's JSON.
         * @return The 'updated_at' property value.
         * @throws IOException If any I/O error occurs.
         */
        @NotNull(message = "Updated time is never NULL")
        public Github.Time updated() throws IOException {
            try {
                return new Github.Time(this.jsn.text("updated_at"));
            } catch (final ParseException ex) {
                throw new IllegalArgumentException(
                    "Cannot parse value of 'updated_at' property",
                    ex
                );
            }
        }
    }
}
