/**
 * Copyright (c) 2013-2024, jcabi.com
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
package com.jcabi.github.safe;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Comments;
import com.jcabi.github.Event;
import com.jcabi.github.Issue;
import com.jcabi.github.IssueLabels;
import com.jcabi.github.Reaction;
import com.jcabi.github.Repo;
import com.jcabi.github.mock.MkGithub;
import com.jcabi.log.Logger;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Safe issue.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.36
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "origin")
@SuppressWarnings("PMD.TooManyMethods")
public final class SfIssue implements Issue {

    /**
     * Original issue.
     */
    private final transient Issue origin;

    /**
     * Public ctor.
     * @param issue The original issue
     */
    public SfIssue(final Issue issue) {
        this.origin = issue;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    public JsonObject json() throws IOException {
        JsonObject json;
        try {
            json = this.origin.json();
        } catch (final AssertionError ex) {
            json = new MkGithub().randomRepo()
                .issues().create("", "").json();
            Logger.warn(this, "failed to fetch issue: %[exception]s", ex);
        }
        return json;
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        try {
            this.origin.patch(json);
        } catch (final AssertionError ex) {
            Logger.warn(this, "failed to patch issue: %[exception]s", ex);
        }
    }

    @Override
    public Repo repo() {
        return this.origin.repo();
    }

    @Override
    public int number() {
        return this.origin.number();
    }

    @Override
    public Comments comments() {
        return new SfComments(this.origin.comments());
    }

    @Override
    public IssueLabels labels() {
        return this.origin.labels();
    }

    @Override
    public Iterable<Event> events() throws IOException {
        return this.origin.events();
    }

    @Override
    public boolean exists() throws IOException {
        return this.origin.exists();
    }

    @Override
    public int compareTo(final Issue issue) {
        return this.origin.compareTo(issue);
    }

    @Override
    public void react(final Reaction reaction) throws IOException {
        this.origin.react(reaction);
    }

    @Override
    public Iterable<Reaction> reactions() {
        return this.origin.reactions();
    }

    @Override
    public void lock(final String reason) {
        this.origin.lock(reason);
    }

    @Override
    public void unlock() {
        this.origin.unlock();
    }

    @Override
    public boolean isLocked() {
        return this.origin.isLocked();
    }
}
