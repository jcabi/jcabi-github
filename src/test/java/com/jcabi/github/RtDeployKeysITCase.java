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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtDeployKeys}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 */
public final class RtDeployKeysITCase {

    /**
     * RtDeployKeys can iterate deploy keys.
     * @throws Exception If some problem inside
     * @todo #224 RtDeployKeysITCase#canFetchAllDeployKeys() is ignored because
     *  at the moment, {@link RtDeployKeys#iterate()} is not fully implemented
     *  and only returns empty iterators. Once {@link RtDeployKeys#iterate()}
     *  has been implemented, remove the Ignore annotation here to enable the
     *  integration test. Revise this test method if necessary.
     */
    @Test
    public void canFetchAllDeployKeys() throws Exception {
        final DeployKeys keys = repo().keys();
        final String title = "Test Iterate Key";
        final DeployKey key = keys.create(
            title,
            new StringBuilder()
                .append("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQDGlOZAXP4XlJ0g2")
                .append("tj0rTZ8TYRLi1e9ADpDmz0FgiUEhB1VsudTOxceWPuylf5AfGePRH")
                .append("zUklHU2txFG48MkOIGaiSMFcf5nKOZd0ewqQFTA5rmGweMtcl+YSQ")
                .append("6h1Pne5gUn2BM9BZpRaq3KgMNOXFU5dJ5+etQSgf/gain54LsBQ==")
                .toString()
        );
        try {
            MatcherAssert.assertThat(
                keys.iterate(),
                Matchers.contains(key)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can create a deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    public void createsDeployKey() throws Exception {
        final DeployKeys keys = repo().keys();
        final String title = "Test Create Key";
        final DeployKey key = keys.create(
            title,
            new StringBuilder()
                .append("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQC2hZYMju2NywH/g")
                .append("t0sxtSOFTIjlxImGq8m72hOnm/HjCAQSYXTF2v0kWyh9PZC1frPMf")
                .append("U+clfy0MpetWJ76tKz4qVS3aA35WK5vLmQYjA5lyhVwq/1TkZikIy")
                .append("21Bvc+KmlguI+bd4HWaN6D3uylQetoCTcxvzf4F2IBZFKmLjTrQ==")
                .toString()
        );
        try {
            MatcherAssert.assertThat(
                new DeployKey.Smart(key).title(),
                Matchers.is(title)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can get a single deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    public void getsDeployKey() throws Exception {
        final DeployKeys keys = repo().keys();
        final String title = "Test Get Key";
        final DeployKey key = keys.create(
            title,
            new StringBuilder()
                .append("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCyMMT1KP0TvZltl")
                .append("IZmGG4oNf2fLbzqKUU24BV4ln25yCL0yqQACdKXRheXVGE6/4gX0i")
                .append("FtpuwePlccGSVJXWgU0uOkQUmMGLQoU+XjBzSa1GaW/r/Igabd1CX")
                .append("cZpeRSsVZ8GQX/XlxPBYeg+ES3ZjqasUBSgn9sZ7ym/G3jsJAlQ==")
                .toString()
        );
        try {
            MatcherAssert.assertThat(
                keys.get(key.number()),
                Matchers.is(key)
            );
        } finally {
            key.remove();
        }
    }

    /**
     * RtDeployKeys can remove a deploy key.
     * @throws Exception If something goes wrong
     */
    @Test
    public void removesDeployKey() throws Exception {
        final DeployKeys keys = repo().keys();
        final String title = "Test Remove Key";
        final DeployKey key = keys.create(
            title,
            new StringBuilder()
                .append("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAAAgQCWMIIddQkxDUr2/")
                .append("opU50nkyAQYb95p0JvLgbIGkU6VUOYjj4XZEQ8DFUHOf8acG1AGv1")
                .append("KbvpKuJ2StUxXVsWjhrniYKZ0UrQ4pKRHVKQBf2RmKr8fw70Z57oi")
                .append("mRpipnpYTmT2cK2FFPTUZ7bYahZ/KctelIwGXAf5PHIqZFxkwyw==")
                .toString()
        );
        try {
            MatcherAssert.assertThat(
                keys.get(key.number()),
                Matchers.notNullValue()
            );
        } finally {
            key.remove();
        }
        MatcherAssert.assertThat(
            keys.iterate(),
            Matchers.not(Matchers.contains(key))
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     * @throws Exception If some problem inside
     */
    private static Repo repo() throws Exception {
        final String key = System.getProperty("failsafe.github.key");
        Assume.assumeThat(key, Matchers.notNullValue());
        return new RtGithub(key).repos().get(
            new Coordinates.Simple(System.getProperty("failsafe.github.repo"))
        );
    }

}
