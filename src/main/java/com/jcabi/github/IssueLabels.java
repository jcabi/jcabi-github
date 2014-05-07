/**
 * Copyright (c) 2013-2014, jcabi.com
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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github labels of an issue.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface IssueLabels {

    /**
     * The issue we're in.
     * @return Issue
     */
    @NotNull(message = "issue is never NULL")
    Issue issue();

    /**
     * Add new labels.
     * @param labels The labels to add
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/labels/#add-labels-to-an-issue">Add labels to an issue</a>
     */
    void add(@NotNull(message = "iterable of label names can't be NULL")
        Iterable<String> labels) throws IOException;

    /**
     * Replace all labels.
     * @param labels The labels to save
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/labels/#replace-all-labels-for-an-issue">Replace all labels for an issue</a>
     */
    void replace(@NotNull(message = "iterable of label names can't be NULL")
        Iterable<String> labels) throws IOException;

    /**
     * Iterate them all.
     * @return Iterator of labels
     * @see <a href="http://developer.github.com/v3/issues/labels/#list-labels-on-an-issue">List Labels on an Issue</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Label> iterate();

    /**
     * Remove label by name.
     * @param name Name of the label to remove
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/labels/#remove-a-label-from-an-issue">Remove a Label from an Issue</a>
     */
    void remove(@NotNull(message = "label name can't be NULL") String name)
        throws IOException;

    /**
     * Remove all labels.
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/labels/#remove-all-labels-from-an-issue">Remove all labels from an issue</a>
     */
    void clear() throws IOException;

    /**
     * Smart IssueLabels with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "labels")
    final class Smart implements IssueLabels {
        /**
         * Encapsulated labels.
         */
        private final transient IssueLabels labels;
        /**
         * Public ctor.
         * @param lbl Labels
         */
        public Smart(
            @NotNull(message = "lbl can't be NULL") final IssueLabels lbl
        ) {
            this.labels = lbl;
        }
        /**
         * Label exists?
         * @param name Name of the label
         * @return TRUE if it exists
         */
        public boolean contains(
            @NotNull(message = "name can't be NULL") final String name
        ) {
            boolean contains = false;
            for (final Label label : this.labels.iterate()) {
                if (label.name().equals(name)) {
                    contains = true;
                    break;
                }
            }
            return contains;
        }
        /**
         * Get label by name (runtime exception if absent).
         * @param name Name of the label
         * @return Label found (exception if not found)
         * @since 0.7
         */
        @NotNull(message = "label is never NULL")
        public Label get(
            @NotNull(message = "name cant be NULL") final String name
        ) {
            Label label = null;
            int count = 0;
            for (final Label opt : this.labels.iterate()) {
                if (opt.name().equals(name)) {
                    label = opt;
                    break;
                }
                ++count;
            }
            if (label == null) {
                throw new IllegalArgumentException(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "label '%s' not found among %d others, use #contains() first",
                        name, count
                    )
                );
            }
            return label;
        }
        /**
         * Add label if it is absent, don't touch its color if exists.
         * @param name Name of the label
         * @return TRUE if it was added
         * @throws IOException If there is any I/O problem
         */
        public boolean addIfAbsent(
            @NotNull(message = "name should not be NULL") final String name
        ) throws IOException {
            final boolean added;
            if (this.contains(name)) {
                added = false;
            } else {
                new Labels.Smart(this.labels.issue().repo().labels())
                    .createOrGet(name);
                this.labels.add(Collections.singletonList(name));
                added = true;
            }
            return added;
        }
        /**
         * Add label if it is absent, and set its color in any case.
         * @param name Name of the label
         * @param color Color to set
         * @return TRUE if it was added
         * @throws IOException If there is any I/O problem
         * @since 0.7
         */
        public boolean addIfAbsent(
            @NotNull(message = "name is never NULL") final String name,
            @NotNull(message = "color can't be NULL") final String color
        )
            throws IOException {
            Label label = null;
            for (final Label opt : new Bulk<Label>(this.labels.iterate())) {
                if (opt.name().equals(name)) {
                    label = opt;
                    break;
                }
            }
            boolean added = false;
            if (label == null) {
                added = true;
                label = new Labels.Smart(this.labels.issue().repo().labels())
                    .createOrGet(name, color);
                this.labels.add(Collections.singletonList(name));
            }
            final Label.Smart smart = new Label.Smart(label);
            if (!smart.color().equals(color)) {
                smart.color(color);
            }
            return added;
        }
        /**
         * Select all labels with the given color.
         * @param color Color
         * @return Collection of labels with the provided color
         * @throws IOException If there is any I/O problem
         * @since 0.7
         */
        @NotNull(message = "collection is never NULL")
        public Collection<Label> findByColor(
            @NotNull(message = "color can't be NULL") final String color
        )
            throws IOException {
            final Collection<Label> found = new LinkedList<Label>();
            for (final Label label : this.labels.iterate()) {
                if (new Label.Smart(label).color().equals(color)) {
                    found.add(label);
                }
            }
            return found;
        }
        /**
         * Remove label if it exists (do nothing otherwise).
         * @param name Label to remove
         * @return TRUE if it was removed, FALSE otherwise
         * @throws IOException If there is any I/O problem
         * @since 0.7
         */
        public boolean removeIfExists(
            @NotNull(message = "name cannot be NULL") final String name
        )
            throws IOException {
            boolean removed = false;
            for (final Label label : this.labels.iterate()) {
                if (label.name().equals(name)) {
                    this.remove(name);
                    removed = true;
                    break;
                }
            }
            return removed;
        }
        @Override
        @NotNull(message = "issue is never NULL")
        public Issue issue() {
            return this.labels.issue();
        }
        @Override
        public void add(
            @NotNull(message = "names can't be NULL")
            final Iterable<String> names
        ) throws IOException {
            this.labels.add(names);
        }
        @Override
        public void replace(
            @NotNull(message = "names can't be NULL")
            final Iterable<String> names
        ) throws IOException {
            this.labels.replace(names);
        }
        @Override
        @NotNull(message = "Iterable of labels is never NULL")
        public Iterable<Label> iterate() {
            return this.labels.iterate();
        }
        @Override
        public void remove(
            @NotNull(message = "name can't be NULL") final String name
        ) throws IOException {
            this.labels.remove(name);
        }
        @Override
        public void clear() throws IOException {
            this.labels.clear();
        }
    }
}
