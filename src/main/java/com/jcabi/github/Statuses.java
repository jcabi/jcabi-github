/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub status.
 * <p>The status exposes all available properties through its
 * {@code json()} method. However, it is recommended to use its
 * "smart" decorator, which helps you to get access to all JSON properties,
 * for example:
 * <pre> URL url = new Status.Smart(status).url();</pre>
 * @see <a href="https://developer.github.com/v3/repos/statuses/">Repo statuses</a>
 * @since 0.23
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Statuses extends JsonReadable {

    /**
     * Associated commit.
     * @return Commit
     */
    Commit commit();

    /**
     * Create new status.
     * @param status Add this status
     * @return The added status
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/repos/statuses/#create-a-status">Create a Status</a>
     */
    Status create(
        Statuses.StatusCreate status
    ) throws IOException;

    /**
     * List all statuses for a given ref.
     * @param ref It can be a SHA, a branch name, or a tag name.
     * @return Iterable of statuses
     * @see <a href="https://developer.github.com/v3/repos/statuses/#list-statuses-for-a-specific-ref">List Statuses for a specific Ref</a>
     */
    Iterable<Status> list(
        String ref
    );

    /**
     * Data to use when creating a new GitHub commit status.
     * @see <a href="https://developer.github.com/v3/repos/statuses/#create-a-status">Create a Status</a>
     * @since 0.24
     */
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(
        of = {
            "state",
            "description",
            "context",
            "targeturl"
        }
    )
    final class StatusCreate implements JsonReadable {
        /**
         * State.
         */
        private final transient Status.State state;

        /**
         * Description.
         */
        private final transient String description;

        /**
         * Context string.
         */
        private final transient Optional<String> context;

        /**
         * Target URL.
         */
        private final transient Optional<String> targeturl;

        /**
         * Public ctor.
         * @param stat State
         */
        public StatusCreate(
            final Status.State stat
        ) {
            this(
                stat,
                "",
                Optional.absent(),
                Optional.absent()
            );
        }

        /**
         * Private ctor.
         * @param stat State
         * @param desc Description
         * @param cntxt Context
         * @param target Target URL
         * @checkstyle ParameterNumberCheck (10 lines)
         */
        private StatusCreate(
            final Status.State stat,
            final String desc,
            final Optional<String> cntxt,
            final Optional<String> target
        ) {
            this.state = stat;
            this.description = desc;
            this.context = cntxt;
            this.targeturl = target;
        }

        /**
         * Returns a StatusCreate with the given state.
         * @param stat State
         * @return StatusCreate
         */
        public Statuses.StatusCreate withState(final Status.State stat) {
            return new Statuses.StatusCreate(
                stat,
                this.description,
                this.context,
                this.targeturl
            );
        }

        /**
         * Returns a StatusCreate with the given description.
         * @param desc Description
         * @return StatusCreate
         */
        public Statuses.StatusCreate withDescription(final String desc) {
            return new Statuses.StatusCreate(
                this.state,
                desc,
                this.context,
                this.targeturl
            );
        }

        /**
         * Returns a StatusCreate with the given context.
         * @param cntxt Context
         * @return StatusCreate
         */
        public Statuses.StatusCreate withContext(final Optional<String> cntxt) {
            return new Statuses.StatusCreate(
                this.state,
                this.description,
                cntxt,
                this.targeturl
            );
        }

        /**
         * Returns a StatusCreate with the given target URL.
         * @param target Target URL
         * @return StatusCreate
         */
        public Statuses.StatusCreate withTargetUrl(final Optional<String> target) {
            return new Statuses.StatusCreate(
                this.state,
                this.description,
                this.context,
                target
            );
        }

        @Override
        public JsonObject json() {
            final JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("state", this.state.identifier())
                .add("description", this.description);
            if (this.context.isPresent()) {
                builder.add("context", this.context.get());
            }
            if (this.targeturl.isPresent()) {
                builder.add("target_url", this.targeturl.get());
            }
            return builder.build();
        }
    }
}
