/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github Repo API.
 *
 * @since 0.5
 * @see <a href="https://developer.github.com/v3/repos/">Repos API</a>
 */
@SuppressWarnings("PMD.TooManyMethods")
@Immutable
public interface Repos {

    /**
     * Get its owner.
     * @return Github
     */
    Github github();
    //byte[]

    /**
     * Create repository.
     * @param settings Settings to use for creating the new repository
     * @return Repository
     * @throws IOException If there is any I/O problem
     * @since 0.5
     * @see <a href="https://developer.github.com/v3/repos/#create">Create Repository</a>
     */
    Repo create(RepoCreate settings)
        throws IOException;

    /**
     * Get repository by name.
     * @param coords Repository name in "user/repo" format
     * @return Repository
     * @see <a href="https://developer.github.com/v3/repos/#get">Get Repository</a>
     */
    Repo get(Coordinates coords);

    /**
     * Remove repository by name.
     *
     * <p>Note: Deleting a repository requires admin access.
     * If OAuth is used, the delete_repo scope is required.
     *
     * @param coords Repository name in "user/repo" format
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/#delete-a-repository">Delete a Repository</a>
     */
    void remove(Coordinates coords) throws IOException;

    /**
     * Iterate all public repos, starting with the one you've seen already.
     * @param identifier The integer ID of the last Repo that you’ve seen.
     * @return Iterator of repo
     * @see <a href="https://developer.github.com/v3/repos/#list-all-public-repositories">List all public repositories</a>
     */
    Iterable<Repo> iterate(
        String identifier
    );

    /**
     * Check if a repository exists on Github.
     * @param coords Coordinates of the repo.
     * @return True if it exists, false otherwise.
     * @throws IOException If something goes wrong.
     */
    boolean exists(final Coordinates coords) throws IOException;

    /**
     * Settings to use when creating a new GitHub repository.
     *
             * @since 0.24
     * @see <a href="https://developer.github.com/v3/repos/#create">Create Repo API</a>
     * @todo #1095:30m Add the ability to set the other parameters of
     *  the repo creation API (has_issues, has_wiki, has_downloads,
     *  team_id, gitignore_template, license_template).
     */
    @SuppressWarnings("PMD.TooManyMethods")
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {"nam", "priv", "descr", "home", "init"})
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
         * Organization where the created repo belongs.
         */
        private final transient String organization;

        /**
         * Other parameters which repo might have.
         */
        private final transient Map<String, JsonValue> other;

        /**
         * Public ctor.
         * @param nme Name of the new repository. Cannot be empty.
         * @param prvt Will the new repo be private?
         *  If not, then it will be public.
         */
        public RepoCreate(final String nme, final boolean prvt) {
            this(nme, prvt, "", "", Optional.<Boolean>absent(), "");
        }

        /**
         * Private ctor.
         * @param nme Name of the new repo. Cannot be empty.
         * @param prvt Will the new repo be private?
         *  If not, then it will be public.
         * @param desc Description of the new repo
         * @param page Homepage of the new repo
         * @param auto Auto-init the new repo?
         * @param org Organization to which this repo belongs.
         *  When empty or null, the repo is created under the
         *  authenticated user.
         * @checkstyle ParameterNumberCheck (7 lines)
         */
        private RepoCreate(
            final String nme,
            final boolean prvt,
            final String desc,
            final String page,
            final Optional<Boolean> auto,
            final String org
        ) {
            if (nme.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty!");
            }
            this.nam = nme;
            this.priv = prvt;
            this.descr = desc;
            this.home = page;
            this.init = auto;
            this.organization = org;
            this.other = new HashMap<>(0);
        }

        /**
         * Name of the new repo.
         * @return Name
         */
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
        public String description() {
            return this.descr;
        }

        /**
         * Homepage of the new repo.
         * If it has no homepage, this is an empty string.
         * @return Homepage
         */
        public String homepage() {
            return this.home;
        }

        /**
         * Auto-init the new repo?
         * If absent, the GitHub default will be used.
         * @return Optional boolean
         */
        public Optional<Boolean> autoInit() {
            return this.init;
        }

        /**
         * Name of the organization to which this repo belongs.
         * @return String org name
         */
        public String organization() {
            return this.organization;
        }

        /**
         * Returns a RepoCreate with the given name.
         * The name cannot be empty.
         * @param nme Name of the new repo
         * @return RepoCreate
         */
        public RepoCreate withName(
            final String nme
        ) {
            return new RepoCreate(
                nme,
                this.priv,
                this.descr,
                this.home,
                this.init,
                this.organization
            );
        }

        /**
         * Returns a RepoCreate with the given privacy.
         * @param privacy Privateness of the new repo
         * @return RepoCreate
         */
        public RepoCreate withPrivacy(final boolean privacy) {
            return new RepoCreate(
                this.nam,
                privacy,
                this.descr,
                this.home,
                this.init,
                this.organization
            );
        }

        /**
         * Returns a RepoCreate with the given description.
         * @param desc Description
         * @return RepoCreate
         */
        public RepoCreate withDescription(
            final String desc
        ) {
            return new RepoCreate(
                this.nam,
                this.priv,
                desc,
                this.home,
                this.init,
                this.organization
            );
        }

        /**
         * Returns a RepoCreate with the given homepage.
         * @param page Homepage URL
         * @return RepoCreate
         */
        public RepoCreate withHomepage(
            final String page
        ) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                page,
                this.init,
                this.organization
            );
        }

        /**
         * Returns a RepoCreate with the given auto-init enabledness.
         * @param auto Auto-init the new repo?
         * @return RepoCreate
         */
        public RepoCreate withAutoInit(final Optional<Boolean> auto) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                this.home,
                auto,
                this.organization
            );
        }

        /**
         * Returns a RepoCreate with the given auto-init enabledness.
         * @param auto Auto-init the new repo?
         * @return RepoCreate
         */
        public RepoCreate withAutoInit(final boolean auto) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                this.home,
                Optional.of(auto),
                this.organization
            );
        }

        /**
         * Returns a RepoCreate with the given organization.
         * @param org Organization to which this repo belongs.
         * @return RepoCreate
         */
        public RepoCreate withOrganization(final String org) {
            return new RepoCreate(
                this.nam,
                this.priv,
                this.descr,
                this.home,
                this.init,
                org
            );
        }

        /**
         * Returns a RepoCreate with the given json fields.
         * @param key Json key
         * @param value Json value
         * @return The same RepoCreate.
         * @todo #1660:30min Make 'with' method immutable.
         *  Currently, the 'with' method mutates the 'other' field.
         *  This is not ideal, as it makes the class mutable.
         *  Make the 'with' method immutable and return a new
         *  RepoCreate object with the new field.
         */
        public RepoCreate with(final String key, final JsonValue value) {
            this.other.put(key, value);
            return this;
        }

        @Override
        public JsonObject json() {
            JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("name", this.nam)
                .add("description", this.descr)
                .add("homepage", this.home)
                .add("private", this.priv);
            if (this.init.isPresent()) {
                builder = builder.add("auto_init", this.init.get());
            }
            for (final Map.Entry<String, JsonValue> entry
                : this.other.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    }
}
