/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * GitHub labels of an issue.
 * @since 0.1
 * @see <a href="https://developer.github.com/v3/issues/labels/">Labels API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface IssueLabels {

    /**
     * The issue we're in.
     * @return Issue
     */
    Issue issue();

    /**
     * Add new labels.
     * @param labels The labels to add
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/labels/#add-labels-to-an-issue">Add labels to an issue</a>
     */
    void add(Iterable<String> labels) throws IOException;

    /**
     * Replace all labels.
     * @param labels The labels to save
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/labels/#replace-all-labels-for-an-issue">Replace all labels for an issue</a>
     */
    void replace(Iterable<String> labels) throws IOException;

    /**
     * Iterate them all.
     * @return Iterator of labels
     * @see <a href="https://developer.github.com/v3/issues/labels/#list-labels-on-an-issue">List Labels on an Issue</a>
     */
    Iterable<Label> iterate();

    /**
     * Remove label by name.
     * @param name Name of the label to remove
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/labels/#remove-a-label-from-an-issue">Remove a Label from an Issue</a>
     */
    void remove(String name) throws IOException;

    /**
     * Remove all labels.
     * @throws IOException If there is any I/O problem
     * @see <a href="https://developer.github.com/v3/issues/labels/#remove-all-labels-from-an-issue">Remove all labels from an issue</a>
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
        public Smart(final IssueLabels lbl) {
            this.labels = lbl;
        }

        /**
         * Label exists?
         * @param name Name of the label
         * @return TRUE if it exists
         */
        public boolean contains(final String name) {
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
        public Label get(final String name) {
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
        public boolean addIfAbsent(final String name) throws IOException {
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
            final String name, final String color
        ) throws IOException {
            Label label = null;
            for (final Label opt : new Bulk<>(this.labels.iterate())) {
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
        public Collection<Label> findByColor(final String color)
            throws IOException {
            final Collection<Label> found = new LinkedList<>();
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
        public boolean removeIfExists(final String name)
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
        public Issue issue() {
            return this.labels.issue();
        }

        @Override
        public void add(final Iterable<String> names) throws IOException {
            this.labels.add(names);
        }

        @Override
        public void replace(final Iterable<String> names) throws IOException {
            this.labels.replace(names);
        }

        @Override
        public Iterable<Label> iterate() {
            return this.labels.iterate();
        }

        @Override
        public void remove(final String name) throws IOException {
            this.labels.remove(name);
        }

        @Override
        public void clear() throws IOException {
            this.labels.clear();
        }
    }
}
