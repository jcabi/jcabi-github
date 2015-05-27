/**
 * Copyright (c) 2013-2015, jcabi.com
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

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Repo API.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 * @see <a href="http://developer.github.com/v3/repos/">Repos API</a>
 */
@SuppressWarnings("PMD.TooManyMethods")
@Immutable
public interface Repos {

    /**
     * Get its owner.
     * @return Github
     */
    @NotNull(message = "github is never NULL")
    Github github();
    //byte[]

    /**
     * Create repository.
     * @param settings Settings to use for creating the new repository
     * @return Repository
     * @throws IOException If there is any I/O problem
     * @since 0.5
     * @see <a href="http://developer.github.com/v3/repos/#create">Create Repository</a>
     */
    @NotNull(message = "repository is never NULL")
    Repo create(@NotNull(message = "new repo settings can't be NULL")
                RepoCreate settings)
        throws IOException;

    /**
     * Get repository by name.
     * @param coords Repository name in "user/repo" format
     * @return Repository
     * @see <a href="http://developer.github.com/v3/repos/#get">Get Repository</a>
     */
    @NotNull(message = "repository is never NULL")
    Repo get(@NotNull(message = "coordinates can't be NULL")
        Coordinates coords);

    /**
     * Remove repository by name.
     *
     * <p>Note: Deleting a repository requires admin access.
     * If OAuth is used, the delete_repo scope is required.
     *
     * @param coords Repository name in "user/repo" format
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/#delete-a-repository">Delete a Repository</a>
     */
    void remove(@NotNull(message = "coordinates can't be NULL")
        Coordinates coords) throws IOException;

    /**
     * Iterate all public repos, starting with the one you've seen already.
     * @param identifier The integer ID of the last Repo that you’ve seen.
     * @return Iterator of repo
     * @see <a href="https://developer.github.com/v3/repos/#list-all-public-repositories">List all public repositories</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Repo> iterate(
        @NotNull(message = "identifier can't be NULL") String identifier
    );

    /**
     * Settings to use when creating a new GitHub repository.
     *
     * @author Chris Rebert (github@rebertia.com)
     * @version $Id$
     * @since 0.24
     * @see <a href="https://developer.github.com/v3/repos/#create">Create Repo API</a>
     * @todo #1095:30m Add the ability to set the other parameters of
     *  the repo creation API (has_issues, has_wiki, has_downloads,
     *  team_id, gitignore_template, license_template).
     */
    @SuppressWarnings("PMD.TooManyMethods")
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "nam", "priv", "descr", "home", "init" })
    final class RepoCreate implements JsonReadable {
        /**
         * Name of the new repo.
         */
        private final transient String nam;
        /**
         * Privateness of the new repo.
         */
        private final transient boolean priv;
        /**
         * Description of the new repo.
         */
        private final transient String descr;
        /**
         * Homepage of the new repo.
         */
        private final transient String home;
        /**
         * Auto-init the new repo?
         */
        private final transient Optional<Boolean> init;

        /**
         * Public ctor.
         * @param nme Name of the new repository. Cannot be empty.
         * @param prvt Will the new repo be private?
         *  If not, then it will be public.
         */
        public RepoCreate(final String nme, final boolean prvt) {
            this(nme, prvt, "", "", Optional.<Boolean>absent());
        }

        /**
         * Private ctor.
         * @param nme Name of the new repo. Cannot be empty.
         * @param prvt Will the new repo be private?
         *  If not, then it will be public.
         * @param desc Description of the new repo
         * @param page Homepage of the new repo
         * @param auto Auto-init the new repo?
         * @checkstyle ParameterNumberCheck (7 lines)
         */
        private RepoCreate(
            @NotNull(message = "name can't be NULL") final String nme,
            final boolean prvt,
            @NotNull(message = "description can't be NULL") final String desc,
            @NotNull(message = "homepage can't be NULL") final String page,
            @NotNull(message = "optional itself can't be NULL")
            final Optional<Boolean> auto) {
            if (nme.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty!");
            }
            this.nam = nme;
            this.priv = prvt;
            this.descr = desc;
            this.home = page;
            this.init = auto;
        }

        /**
         * Name of the new repo.
         * @return Name
         */
        @NotNull(message = "name is never NULL")
        public String name() {
            return this.nam;
        }

        /**
         * Will the new repo be private? If not, then it will be public.
         * @return Is this repo private?
         */
        public boolean isPrivate() {
            return this.priv;
        }

        /**
         * Description of the new repo.
         * If it has no description, this is an empty string.
         * @return Description
         */
        @NotNull(message = "description is never NULL")
        public String description() {
            return this.descr;
        }

        /**
         * Homepage of the new repo.
         * If it has no homepage, this is an empty string.
         * @return Homepage
         */
        @NotNull(message = "homepage is never NULL")
        public String homepage() {
            return this.home;
        }

        /**
         * Auto-init the new repo?
         * If absent, the GitHub default will be used.
         * @return Optional boolean
         */
        @NotNull(message = "optional itself is never NULL")
        public Optional<Boolean> autoInit() {
            return this.init;
        }

        /**
         * Returns a RepoCreate with the given name.
         * The name cannot be empty.
         * @param nme Name of the new repo
         * @return RepoCreate
         */
        @NotNull(message = "renamed settings is never NULL")
        public RepoCreate withName(
            @NotNull(message = "name can't be NULL") final String nme
        ) {
            return new RepoCreate(
                nme,
                this.priv,
                this.descr,
                this.home,
                this.init
            );
        }

        /**
         * Returns a RepoCreate with the given privacy.
         * @param privacy Privateness of the new repo
         * @return RepoCreate
         */
        @NotNull(message = "new repo settings is never NULL")
        public RepoCreate withPrivacy(final boolean privacy) {
            return new RepoCreate(
                this.nam,
                privacy,
                this.descr,
                this.home,
                this.init
            );
        }

        /**
         * Returns a RepoCreate with the given description.
         * @param desc Description
         * @return RepoCreate
         */
        @NotNull(message = "adjusted settings is never NULL")
        public RepoCreate withDescription(
            @NotNull(message = "description can't be NULL") final String desc
        ) {
            return new RepoCreate(
                this.nam,
                this.priv,
                desc,
                this.home,
                this.init
            );
        }

        /**
         * Returns a RepoCreate with the given homepage.
         * @param page Homepage URL
         * @return RepoCreate
         */
        @NotNull(message = "changed settings is never NULL")
        public RepoCreate withHomepage(
            @NotNull(message = "homepage can't be NULL") final String page
        ) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                page,
                this.init
            );
        }

        /**
         * Returns a RepoCreate with the given auto-init enabledness.
         * @param auto Auto-init the new repo?
         * @return RepoCreate
         */
        @NotNull(message = "modified settings is never NULL")
        public RepoCreate withAutoInit(final Optional<Boolean> auto) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                this.home,
                auto
            );
        }

        /**
         * Returns a RepoCreate with the given auto-init enabledness.
         * @param auto Auto-init the new repo?
         * @return RepoCreate
         */
        @NotNull(message = "new settings is never NULL")
        public RepoCreate withAutoInit(final boolean auto) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                this.home,
                Optional.of(auto)
            );
        }

        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() {
            JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("name", this.nam)
                .add("description", this.descr)
                .add("homepage", this.home)
                .add("private", this.priv);
            if (this.init.isPresent()) {
                builder = builder.add("auto_init", this.init.get());
            }
            return builder.build();
        }
    }
}
