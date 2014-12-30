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
package com.jcabi.github.mock;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.Stars;
import javax.validation.constraints.NotNull;
import lombok.ToString;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Github starring API.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.15
 * @todo #919:30min Implement MkStars.starred() operation.
 *  Don't forget about unit tests.
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
final class MkStars implements Stars {
    @Override
    public boolean starred(
        @NotNull(message = "user can't be NULL") final String user,
        @NotNull(message = "repo can't be NULL") final String repo
    ) {
        throw new NotImplementedException("MkStars.starred()");
    }

    @Override
    public void star(
        @NotNull(message = "user can't be NULL") final String user,
        @NotNull(message = "repo can't be NULL") final String repo
    ) {
        throw new NotImplementedException("MkStars.star()");
    }

    @Override
    public void unstar(
        @NotNull(message = "user can't be NULL") final String user,
        @NotNull(message = "repo can't be NULL") final String repo
    ) {
        throw new NotImplementedException("MkStars.unstar()");
    }
}
