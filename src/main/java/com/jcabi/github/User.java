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
import java.io.IOException;
import java.net.URL;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github user.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @see <a href="http://developer.github.com/v3/users/">User API</a>
 * @since 0.1
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
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
        public Smart(final User usr) {
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
        public URL avatarUrl() throws IOException {
            return new URL(this.jsn.text("avatar_url"));
        }

        /**
         * Get his URL.
         * @return URL of the user
         * @throws IOException If it fails
         */
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }

        /**
         * Get his name.
         * @return User name
         * @throws IOException If it fails
         */
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
        public String company() throws IOException {
            return this.jsn.text("company");
        }

        /**
         * Get his location.
         * @return Location name
         * @throws IOException If it fails
         */
        public String location() throws IOException {
            return this.jsn.text("location");
        }

        /**
         * Get his email.
         * @return Email
         * @throws IOException If it fails
         */
        public String email() throws IOException {
            return this.jsn.text("email");
        }

        @Override
        public Github github() {
            return this.user.github();
        }

        @Override
        public String login() throws IOException {
            return this.user.login();
        }

        @Override
        public Organizations organizations() {
            return this.user.organizations();
        }

        @Override
        public PublicKeys keys() {
            return this.user.keys();
        }

        @Override
        public UserEmails emails() {
            return this.user.emails();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.user.json();
        }

        @Override
        public void patch(final JsonObject json) throws IOException {
            this.user.patch(json);
        }

    }

}
