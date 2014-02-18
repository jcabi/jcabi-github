/**
 * Copyright (c) 2013-2014, JCabi.com
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
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Github labels.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
 */
@Immutable
@SuppressWarnings("PMD.TooManyMethods")
public interface Labels {

    /**
     * The repo we're in.
     * @return Repo
     */
    @NotNull(message = "repo is never NULL")
    Repo repo();

    /**
     * Create new label.
     * @param name The name of it
     * @param color Color of it
     * @return The label created
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/labels/#create-a-label">Create a Label</a>
     */
    Label create(
        @NotNull(message = "label name can't be NULL") String name,
        @NotNull(message = "label color can't be NULL") String color)
        throws IOException;

    /**
     * Get a label by name.
     * @param name The name of it
     * @return The label
     * @see <a href="http://developer.github.com/v3/issues/labels/#get-a-single-label">Get a single label</a>
     */
    Label get(@NotNull(message = "label name can't be NULL") String name);

    /**
     * Iterate them all.
     * @return Iterator of labels
     * @see <a href="http://developer.github.com/v3/issues/labels/#list-labels-on-an-issue">List Labels on an Issue</a>
     */
    @NotNull(message = "iterable is never NULL")
    Iterable<Label> iterate();

    /**
     * Delete label by name.
     * @param name Name of the label to remove
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/issues/labels/#delete-a-label">Delete a Label</a>
     */
    void delete(@NotNull(message = "label name can't be NULL") String name)
        throws IOException;

    /**
     * Smart Labels with extra features.
     */
    @Immutable
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = "labels")
    final class Smart implements Labels {
        /**
         * Encapsulated labels.
         */
        private final transient Labels labels;
        /**
         * Public ctor.
         * @param lbl Labels
         */
        public Smart(final Labels lbl) {
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
         * Create or get label.
         * @param name Name of the label
         * @return Label found or created
         * @throws IOException If there is any I/O problem
         */
        public Label createOrGet(final String name) throws IOException {
            return this.createOrGet(name, "c0c0c0");
        }
        /**
         * Create or get label (with this explicit color).
         * @param name Name of the label
         * @param color Color to set (or modify)
         * @return Label found or created
         * @throws IOException If there is any I/O problem
         * @since 0.7
         */
        public Label createOrGet(final String name,
            final String color) throws IOException {
            final Label.Smart label;
            if (this.contains(name)) {
                label = new Label.Smart(this.labels.get(name));
                if (!label.color().equals(color)) {
                    label.color(color);
                }
            } else {
                label = new Label.Smart(this.labels.create(name, color));
            }
            return label;
        }
        @Override
        public Repo repo() {
            return this.labels.repo();
        }
        @Override
        public Label create(final String name, final String color)
            throws IOException {
            return this.labels.create(name, color);
        }
        @Override
        public Label get(final String name) {
            return this.labels.get(name);
        }
        @Override
        public Iterable<Label> iterate() {
            return this.labels.iterate();
        }
        @Override
        public void delete(final String name) throws IOException {
            this.labels.delete(name);
        }
    }

}
