/**
 * Copyright (c) 2013-2022, jcabi.com
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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Commit;
import com.jcabi.github.Status;
import com.jcabi.github.Statuses;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Mock of GitHub commit statuses.
 * @author Chris Rebert (github@rebertia.com)
 * @version $Id$
 * @since 0.24
 * @todo #1129:30min Finish implementing this class (MkStatuses), a mock of
 *  GitHub's commits statuses (the "Statuses" interface).
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "cmmt" })
final class MkStatuses implements Statuses {
    /**
     * Commit whose statuses this represents.
     */
    private final transient Commit cmmt;

    /**
     * Ctor.
     * @param cmt Commit whose statuses this represents
     */
    MkStatuses(
        final Commit cmt
    ) {
        this.cmmt = cmt;
    }

    @Override
    public Commit commit() {
        return this.cmmt;
    }

    @Override
    public Status create(
        final StatusCreate status
    ) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Iterable<Status> list(
        final String ref
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public JsonObject json() {
        throw new UnsupportedOperationException("Yet to be implemented");
    }
}
