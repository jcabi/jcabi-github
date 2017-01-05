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

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github status.
 *
 * <p>The status exposes all available properties through its
 * {@code json()} method. However, it is recommended to use its
 * "smart" decorator, which helps you to get access to all JSON properties,
 * for example:
 *
 * <pre> URL url = new Status.Smart(status).url();</pre>
 *
 * @author Marcin Cylke (marcin.cylke+github@gmail.com)
 * @version $Id$
 * @since 0.23
 * @see <a href="https://developer.github.com/v3/repos/statuses/">Repo statuses</a>
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
     * @throws java.io.IOException If there is any I/O problem
     * @return The added status
     * @see <a href="https://developer.github.com/v3/repos/statuses/#create-a-status">Create a Status</a>
     */
    Status create(
        final StatusCreate status
    ) throws IOException;

    /**
     * List all statuses for a given ref.
     * @param ref It can be a SHA, a branch name, or a tag name.
     * @return Iterable of statuses
     * @see <a href="https://developer.github.com/v3/repos/statuses/#list-statuses-for-a-specific-ref">List Statuses for a specific Ref</a>
     */
    Iterable<Status> list(
        final String ref
    );

    /**
     * Data to use when creating a new GitHub commit status.
     *
     * @author Chris Rebert (github@rebertia.com)
     * @version $Id$
     * @since 0.24
     * @see <a href="https://developer.github.com/v3/repos/statuses/#create-a-status">Create a Status</a>
     */
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = {
            "state",
            "description",
            "context",
            "targeturl"
            })
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
                Optional.<String>absent(),
                Optional.<String>absent()
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
        public StatusCreate withState(final Status.State stat) {
            return new StatusCreate(
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
        public StatusCreate withDescription(final String desc) {
            return new StatusCreate(
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
        public StatusCreate withContext(final Optional<String> cntxt) {
            return new StatusCreate(
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
        public StatusCreate withTargetUrl(final Optional<String> target) {
            return new StatusCreate(
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
