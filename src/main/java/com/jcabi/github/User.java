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
import java.net.MalformedURLException;
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
 */
@Immutable
public interface User {

    /**
     * Get his login.
     * @return Login name
     */
    @NotNull(message = "login is never NULL")
    String login();

    /**
     * Get his JSON description.
     * @return JSON object
     * @see <a href="http://developer.github.com/v3/users/#get-a-single-user">Get a Single User</a>
     */
    @NotNull(message = "JSON is never NULL")
    JsonObject json();

    /**
     * Smart Gist that can manipulate with JSON data.
     * @see <a href="http://developer.github.com/v3/users/#get-a-single-user">Get a Single User</a>
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "user")
    final class Tool {
        /**
         * Encapsulated user.
         */
        private final transient User user;
        /**
         * Public ctor.
         * @param usr User
         */
        public Tool(final User usr) {
            this.user = usr;
        }
        /**
         * Get his ID.
         * @return Unique user ID
         * @checkstyle MethodName (3 lines)
         */
        @SuppressWarnings("PMD.ShortMethodName")
        public int id() {
            return this.user.json().getJsonNumber("id").intValue();
        }
        /**
         * Get his avatar URL.
         * @return URL of the avatar
         */
        public URL avatarUrl() {
            try {
                return new URL(this.user.json().getString("avatar_url"));
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * Get his URL.
         * @return URL of the user
         */
        public URL url() {
            try {
                return new URL(this.user.json().getString("url"));
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }
        /**
         * Get his name.
         * @return User name
         */
        public String name() {
            return this.user.json().getString("name");
        }
        /**
         * Get his company.
         * @return Company name
         */
        public String company() {
            return this.user.json().getString("company");
        }
        /**
         * Get his location.
         * @return Location name
         */
        public String location() {
            return this.user.json().getString("location");
        }
        /**
         * Get his email.
         * @return Email
         */
        public String email() {
            return this.user.json().getString("email");
        }
    }

}
