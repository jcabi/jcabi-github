/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import jakarta.json.JsonObject;
import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Safe issue.
 *
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
