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
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/users/">User API</a>
 * @todo #1:1hr Fetch list of emails of a user. Let's implement
 *  a new method emails() that returns an instance of class UserEmails with
 *  a few methods: 1) iterate() returning a list of strings, 2) add(String),
 *  and 3) remove(String). Let's use the
 *  new response format suggested by Github:
 *  http://developer.github.com/v3/users/emails/#list-email-addresses-for-a-user
 *  This new UserEmails interface should be implemented by GhUserEmails,
 *  tested in a unit and integration tests. Besides that, we should
 *  implement MkUserEmails class.
 * @todo #1:1hr Public keys of a user. Let's implement a new method
 *  keys(), which should return an instance of interface PublicKeys. This
 *  interface should have at least methods 1) iterate() to list all public
 *  keys of a user, 2) get(String) to get a single public key, 3) remove(String)
 *  to remove a key. Every key should be an instance of interface PublicKey,
 *  extending JsonReadable and JsonPatchable. All of the new classes should
 *  be implemented with GhPublicKeys and GhPublicKey classes. We should
 *  create integration and unit tests, and implement MkPublicKeys
 *  and MkPublicKey classes.
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
     * Smart user with extra features.
     * @see <a href="http://developer.github.com/v3/users/#get-a-single-user">Get a Single User</a>
     * @todo #1:30min Implement methods to retrieve all values provided
     *  by Github for a single user, see:
     *  http://developer.github.com/v3/users/#get-a-single-user
     *  At the moment we implement just a few, but every data
     *  items should have its own method. Of course, every new item should
     *  be tested by a new unit test method.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "user")
    final class Smart implements User {
        /**
         * Encapsulated user.
         */
        private final transient User user;
        /**
         * Public ctor.
         * @param usr User
         */
        public Smart(final User usr) {
            this.user = usr;
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
            return new URL(new SmartJson(this).text("avatar_url"));
        }
        /**
         * Get his URL.
         * @return URL of the user
         * @throws IOException If it fails
         */
        public URL url() throws IOException {
            return new URL(new SmartJson(this).text("url"));
        }
        /**
         * Get his name.
         * @return User name
         * @throws IOException If it fails
         */
        public String name() throws IOException {
            return new SmartJson(this).text("name");
        }
        /**
         * Get his company.
         * @return Company name
         * @throws IOException If it fails
         */
        public String company() throws IOException {
            return new SmartJson(this).text("company");
        }
        /**
         * Get his location.
         * @return Location name
         * @throws IOException If it fails
         */
        public String location() throws IOException {
            return new SmartJson(this).text("location");
        }
        /**
         * Get his email.
         * @return Email
         * @throws IOException If it fails
         */
        public String email() throws IOException {
            return new SmartJson(this).text("email");
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
        public JsonObject json() throws IOException {
            return this.user.json();
        }
        @Override
        public void patch(final JsonObject json) throws IOException {
            this.user.patch(json);
        }
    }

}
