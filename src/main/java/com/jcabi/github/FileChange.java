/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.google.common.base.Optional;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.Locale;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * File change.
 * @since 0.24
 * @see <a href="https://developer.github.com/v3/repos/commits/#compare-two-commits">Compare two commits</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface FileChange extends JsonReadable {
    enum Status implements StringEnum {
        /**
         * File was added.
         */
        ADDED("added"),
        /**
         * File's content was modified.
         */
        MODIFIED("modified"),
        /**
         * File was removed.
         */
        REMOVED("removed"),
        /**
         * File was renamed.
         */
        RENAMED("renamed");

        /**
         * File status string.
         */
        private final transient String status;

        /**
         * Ctor.
         * @param stat File status string.
         */
        Status(final String stat) {
            this.status = stat;
        }

        @Override
        public String identifier() {
            return this.status;
        }

        /**
         * Get file change status corresponding to the given status string.
         * @param name Status string
         * @return Status enum value
         */
        public static FileChange.Status forValue(final String name) {
            return Status.valueOf(name.toUpperCase(Locale.ENGLISH));
        }
    }

    /**
     * Smart file change with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "change", "jsn" })
    @SuppressWarnings("PMD.TooManyMethods")
    final class Smart implements FileChange {
        /**
         * Encapsulated file change.
         */
        private final transient FileChange change;
        /**
         * SmartJson object for convenient JSON parsing.
         */
        private final transient SmartJson jsn;

        /**
         * Public ctor.
         * @param chng File change
         */
        public Smart(final FileChange chng) {
            this.change = chng;
            this.jsn = new SmartJson(chng);
        }

        /**
         * File's commit SHA.
         * @return SHA
         * @throws IOException If there is any I/O problem
         */
        public String sha() throws IOException {
            return this.jsn.text("sha");
        }

        /**
         * File's name. Includes the path to the file from the
         * root directory of the repository. Does not start with a
         * forward slash. Example: "foo/bar/baz.txt"
         * @return Filename
         * @throws IOException If there is any I/O problem
         */
        public String filename() throws IOException {
            return this.jsn.text("filename");
        }

        /**
         * Status of the file in this change.
         * @return File status
         * @throws IOException If there is any I/O problem
         */
        public FileChange.Status status() throws IOException {
            return FileChange.Status.forValue(this.jsn.text("status"));
        }

        /**
         * Number of lines added, or 0 if the file is binary.
         * @return Number of lines added
         * @throws IOException If there is any I/O problem
         */
        public int additions() throws IOException {
            return this.jsn.number("additions");
        }

        /**
         * Number of lines deleted, or 0 if the file is binary.
         * @return Number of lines deleted
         * @throws IOException If there is any I/O problem
         */
        public int deletions() throws IOException {
            return this.jsn.number("deletions");
        }

        /**
         * Number of lines modified, which is equal to the sum of
         * {@link Smart#additions()} and {@link Smart#deletions()}.
         * @return Number of lines modified
         * @throws IOException If there is any I/O problem
         */
        public int changes() throws IOException {
            return this.jsn.number("changes");
        }

        /**
         * Diff string of the changes to the file. Only available if
         * the file is text (as opposed to binary).
         * @return Diff string
         * @throws IOException If there is any I/O problem
         */
        public Optional<String> patch() throws IOException {
            return Optional.fromNullable(this.json().getString("patch", null));
        }

        /**
         * URL for the raw contents of the file.
         * @return URL
         * @throws IOException If there is any I/O problem
         */
        public String rawUrl() throws IOException {
            return this.jsn.text("raw_url");
        }

        /**
         * URL for the file's git blob.
         * @return URL
         * @throws IOException If there is any I/O problem
         */
        public String blobUrl() throws IOException {
            return this.jsn.text("blob_url");
        }

        /**
         * Repo contents URL for the file.
         * @return URL
         * @throws IOException If there is any I/O problem
         */
        public String contentsUrl() throws IOException {
            return this.jsn.text("contents_url");
        }

        @Override
        public JsonObject json() throws IOException {
            return this.change.json();
        }
    }
}
