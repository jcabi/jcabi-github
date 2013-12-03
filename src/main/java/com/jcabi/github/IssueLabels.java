/**
 * Copyright (c) 2012-2013, JCabi.com
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
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Github labels of an issue.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://developer.github.com/v3/issues/labels/">Labels API</a>
 */
@Immutable
public interface IssueLabels {

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

}
