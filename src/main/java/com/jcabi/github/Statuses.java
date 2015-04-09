package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.List;

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
 * @since 1.16
 * @see <a href="https://developer.github.com/v3/repos/statuses/">Repo statuses</a>
 */
@Immutable
public interface Statuses extends Comparable<Statuses>, JsonReadable {

    /**
     * Associated commit
     * @return Commit
     */
    @NotNull(message = "commit is never NULL")
    Commit commit();

    /**
     * Create new status
     * @param status
     */
    Status create(@NotNull(message = "status can't be NULL") Status status) throws IOException;

    /**
     *
     * @param ref It can be a SHA, a branch name, or a tag name.
     * @return list of statuses
     */
    @NotNull(message = "list of statuses is never NULL")
    List<Statuses> list(@NotNull(message = "ref can't be NULL") String ref);

    /**
     * Smart commit.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "statuses", "jsn" })
    final class Smart implements Statuses {
        /**
         * Encapsulated status.
         */
        private final transient Statuses statuses;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;
        /**
         * Public ctor.
         * @param statuses Status
         */
        public Smart(
                @NotNull(message = "cmt can't be NULL") final Statuses statuses
        ) {
            this.statuses = statuses;
            this.jsn = new SmartJson(statuses);
        }
        /**
         * Get its message.
         * @return Message of commit
         * @throws java.io.IOException If there is any I/O problem
         */
        @NotNull(message = "message is never NULL")
        public String message() throws IOException {
            return this.jsn.text("message");
        }
        /**
         * Get its URL.
         * @return URL of comment
         * @throws IOException If there is any I/O problem
         */
        @NotNull(message = "URL is never NULL")
        public URL url() throws IOException {
            return new URL(this.jsn.text("url"));
        }
        @Override
        @NotNull(message = "commit is never NULL")
        public Commit commit() {
            return this.statuses.commit();
        }

        @Override
        public Status create(@NotNull(message = "status can't be NULL") Status status) throws IOException {
            return this.statuses.create(status);
        }

        @Override
        public List<Statuses> list(@NotNull(message = "ref can't be NULL") String ref) {
            return this.statuses.list(ref);
        }

        @Override
        @NotNull(message = "JSON is never NULL")
        public JsonObject json() throws IOException {
            return this.statuses.json();
        }
        @Override
        public int compareTo(
                @NotNull(message = "obj can't be NULL") final Statuses obj
        ) {
            return this.statuses.compareTo(obj);
        }
    }
}
