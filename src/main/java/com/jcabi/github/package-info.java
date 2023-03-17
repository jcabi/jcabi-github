/**
 * Copyright (c) 2013-2023, jcabi.com
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

/**
 * Object Oriented Github API.
 *
 * <p>The only dependency you need is (check our latest version available
 * at <a href="http://github.jcabi.com">github.jcabi.com</a>):
 *
 * <pre>&lt;depedency&gt;
 *   &lt;groupId&gt;com.jcabi&lt;/groupId&gt;
 *   &lt;artifactId&gt;jcabi-github&lt;/artifactId&gt;
 * &lt;/dependency&gt;</pre>
 *
 * <p>There are some design conventions in this library, which is important
 * to keep in mind.
 *
 * <p>Sometimes we use {@link javax.json.JsonObject}
 * as an input argument for a method
 * (for example, in {@link Repos}),
 * somewhere else we use {@link java.util.Map}
 * (see {@link Issues#iterate(Map)}),
 * in other case we use just a few plain Java types
 * (see {@link DeployKeys#create(String,String)}),
 * and sometimes we combine them
 * (see {@link Hooks}).
 * This is not a bug, it's done intentionally.
 * The logic is simple.
 * Class {@link javax.json.JsonObject} is used when Github API is
 * expecting a JSON object as HTTP request body.
 * {@link java.util.Map} is used when Github API expects HTTP query parameters,
 * and some of them are optional, and there are more than two of them.
 * In all other situations we're using plain Java types.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @see <a href="http://github.jcabi.com/">project website</a>
 */
package com.jcabi.github;
