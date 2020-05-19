/**
 * Copyright (c) 2013-2020, jcabi.com
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
import java.text.ParseException;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github organization.
 *
 * <p>Use a supplementary "smart" decorator to get other properties
 * from an organization, for example:
 *
 * <pre> Organization.Smart org = new Organization.Smart(origin);
 * if (org.name() == null) {
 *   name = "new_name";
 * }</pre>
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (500 lines)
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Organization extends Comparable<Organization>,
    JsonReadable, JsonPatchable {

    /**
     * Github we're in.
     * @return Github
     */
    Github github();

    /**
     * Get this organization's login.
     * @return Login name
     */
    String login();

    /**
     * Get this organization's public members.
     * @return Public members
     */
    PublicMembers publicMembers();

    /**
     * Smart Organization with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "org", "jsn" })
    final class Smart implements Organization {

        /**
         * Encapsulated org.
         */
        private final transient Organization org;

        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param orgn Organization
         */
        public Smart(
            final Organization orgn
        ) {
            this.org = orgn;
            this.jsn = new SmartJson(orgn);
        }

        /**
         * Get this organization's ID.
         * @return Unique organization ID
         * @throws IOException If it fails
         */
        public int number() throws IOException {
            return this.org.json().getJsonNumber("id").intValue();
        }

        /**
         * Get its company.
         * @return Company of organization
         * @throws IOException If there is any I/O problem
         */
        public String company() throws IOException {
            return this.jsn.text("company");
        }

        /**
         * Change its company.
         * @param company Company of organization
         * @throws IOException If there is any I/O problem
         */
        public void company(
            final String company
        ) throws IOException {
            this.org.patch(
                Json.createObjectBuilder().add("company", company).build()
            );
        }

        /**
         * Get its location.
         * @return Location of organization
         * @throws IOException If there is any I/O problem
         */
        public String location() throws IOException {
            return this.jsn.text("location");
        }

        /**
         * Change its location.
         * @param location Location of organization
         * @throws IOException If there is any I/O problem
         */
        public void location(
            final String location
        ) throws IOException {
            this.org.patch(
                Json.createObjectBuilder().add("location", location).build()
            );
        }

        /**
         * Get its name.
         * @return Name of organization
         * @throws IOException If there is any I/O problem
         */
        public String name() throws IOException {
            return this.jsn.text("name");
        }

        /**
         * Change its name.
         * @param name Company of organization
         * @throws IOException If there is any I/O problem
         */
        public void name(
            final String name
        ) throws IOException {
            this.org.patch(
                Json.createObjectBuilder().add("name", name).build()
            );
        }

        /**
         * Get its email.
         * @return Email of organization
         * @throws IOException If there is any I/O problem
         */
        public String email() throws IOException {
            return this.jsn.text("email");
        }

        /**
         * Change its email.
         * @param email Email of organization
         * @throws IOException If there is any I/O problem
         */
        public void email(
            final String email
        ) throws IOException {
            this.org.patch(
                Json.createObjectBuilder().add("email", email).build()
            );
        }

        /**
         * Get its billingEmail.
         * @return BillingEmail of organization
         * @throws IOException If there is any I/O problem
         */
        public String billingEmail() throws IOException {
            return this.jsn.text("billing_email");
        }

        /**
         * Change its billingEmail.
         * @param billingemail BillingEmail of organization
         * @throws IOException If there is any I/O problem
         */
        public void billingEmail(
            final String billingemail
        )
            throws IOException {
            this.org.patch(
                Json.createObjectBuilder()
                    .add("billing_email", billingemail).build()
            );
        }

        /**
         * Get its blog.
         * @return Blog of organization
         * @throws IOException If there is any I/O problem
         */
        public String blog() throws IOException {
            return this.jsn.text("blog");
        }

        /**
         * Get its URL.
         * @return URL of organization
         * @throws IOException If there is any I/O problem
         */
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }

        /**
         * Get its HTML URL.
         * @return HTML URL of organization
         * @throws IOException If there is any I/O problem
         */
        public URL htmlUrl() throws IOException {
            return new URL(this.jsn.text("html_url"));
        }

        /**
         * Get its avatar URL.
         * @return Avatar URL of organization
         * @throws IOException If there is any I/O problem
         */
        public URL avatarUrl() throws IOException {
            return new URL(this.jsn.text("avatar_url"));
        }

        /**
         * When this organisation was created.
         * @return Date of creation
         * @throws IOException If there is any I/O problem
         */
        public Date createdAt() throws IOException {
            try {
                return new Github.Time(
                    this.jsn.text("created_at")
                ).date();
            } catch (final ParseException ex) {
                throw new IllegalStateException(ex);
            }
        }

        /**
         * Get its public repos count.
         * @return Count of public repos of organization
         * @throws IOException If there is any I/O problem
         */
        public int publicRepos() throws IOException {
            return this.jsn.number("public_repos");
        }

        /**
         * Get its public gists count.
         * @return Count of public gists of organization
         * @throws IOException If there is any I/O problem
         */
        public int publicGists() throws IOException {
            return this.jsn.number("public_gists");
        }

        /**
         * Get its followers count.
         * @return Count of followers of organization
         * @throws IOException If there is any I/O problem
         */
        public int followers() throws IOException {
            return this.jsn.number("followers");
        }

        /**
         * Get its following count.
         * @return Count of following of organization
         * @throws IOException If there is any I/O problem
         */
        public int following() throws IOException {
            return this.jsn.number("following");
        }

        /**
         * Get its type.
         * @return Type of organization
         * @throws IOException If there is any I/O problem
         */
        public String type() throws IOException {
            return this.jsn.text("type");
        }

        @Override
        public String login() {
            return this.org.login();
        }

        @Override
        public Github github() {
            return this.org.github();
        }

        @Override
        public PublicMembers publicMembers() {
            return this.org.publicMembers();
        }

        @Override
        public JsonObject json() throws IOException {
            return this.org.json();
        }

        @Override
        public void patch(
            final JsonObject json
        ) throws IOException {
            this.org.patch(json);
        }

        @Override
        public int compareTo(
            final Organization obj
        ) {
            return this.org.compareTo(obj);
        }
    }

}
