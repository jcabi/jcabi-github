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

import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.json.Json;

/**
 * Mocker of {@link Issues}.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
public final class IssuesMocker implements Issues {

    /**
     * Repo.
     */
    private final transient Repo owner;

    /**
     * All issues.
     */
    private final transient ConcurrentMap<Integer, Issue> map =
        new ConcurrentSkipListMap<Integer, Issue>();

    /**
     * Public ctor.
     * @param repo Owner of it
     */
    public IssuesMocker(final Repo repo) {
        this.owner = repo;
    }

    @Override
    public Repo repo() {
        return this.owner;
    }

    @Override
    public Issue get(final int number) {
        return this.map.get(number);
    }

    @Override
    public Issue create(final String title, final String body)
        throws IOException {
        final int number;
        final Issue issue;
        synchronized (this.map) {
            number = this.map.size() + 1;
            issue = new IssueMocker(this.owner, number);
            this.map.put(number, issue);
        }
        issue.patch(
            Json.createObjectBuilder()
                .add("title", title)
                .add("body", body)
                .build()
        );
        Logger.info(
            this, "Github issue #%d created: %s",
            number, title
        );
        return issue;
    }

    @Override
    public Iterable<Issue> iterate(final Map<String, String> params) {
        return this.map.values();
    }

}
