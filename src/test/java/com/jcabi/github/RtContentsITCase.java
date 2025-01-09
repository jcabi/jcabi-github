/**
 * Copyright (c) 2013-2025, jcabi.com
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

import com.jcabi.aspects.Tv;
import com.jcabi.github.OAuthScope.Scope;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link RtContents}.
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@OAuthScope(Scope.REPO)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RtContentsITCase {

    /**
     * RepoRule.
     * @checkstyle VisibilityModifierCheck (3 lines)
     */
    @Rule
    public final transient RepoRule rule = new RepoRule();

    /**
     * RtContents can fetch readme file.
     * @throws Exception If some problem inside
     */
    @Test
    public void canFetchReadmeFiles() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            MatcherAssert.assertThat(
                repos.get(repo.coordinates()).contents().readme().path(),
                Matchers.equalTo("README.md")
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can get update file content.
     * @throws Exception If some problem inside
     */
    @Test
    public void canUpdateFileContent() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        final String message = "commit message";
        final String text = "new content";
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final Content content = contents.create(
                this.jsonObject(
                    path, new String(
                        Base64.encodeBase64("init content".getBytes())
                    ),
                    message
                )
            );
            contents.update(
                path,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message)
                    .add("content", Base64.encodeBase64String(text.getBytes()))
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            MatcherAssert.assertThat(
                new String(
                    Base64.decodeBase64(
                        new Content.Smart(
                            contents.get(path, "master")
                        ).content()
                    )
                ),
                Matchers.equalTo(text)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can get update file content in specific branch.
     * @throws Exception If some problem inside
     */
    @Test
    public void canUpdateFileContentInSpecificBranch() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        final String message = "Commit message";
        final String text = "Updated";
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final Content content = contents.create(
                this.jsonObject(
                    path, new String(
                        Base64.encodeBase64("Initial.".getBytes())
                    ),
                    message
                )
            );
            contents.update(
                path,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message)
                    .add("ref", "master")
                    .add("content", Base64.encodeBase64String(text.getBytes()))
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            MatcherAssert.assertThat(
                new String(
                    Base64.decodeBase64(
                        new Content.Smart(
                            contents.get(path, "master")
                        ).content()
                    )
                ),
                Matchers.equalTo(text)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can remove and throw an exception when get an absent content.
     * @throws Exception If some problem inside
     */
    @Test(expected = AssertionError.class)
    public void throwsWhenTryingToGetAnAbsentContent() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        final String message = "commit message";
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final Content content = contents.create(
                this.jsonObject(
                    path, new String(
                        Base64.encodeBase64("first content".getBytes())
                    ),
                    message
                )
            );
            contents.remove(
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message)
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            contents.get(path, "master");
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can create file content.
     * @throws Exception If some problem inside
     */
    @Test
    public void canCreateFileContent() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            MatcherAssert.assertThat(
                repos.get(repo.coordinates()).contents().create(
                    this.jsonObject(
                        path, new String(
                            Base64.encodeBase64("some content".getBytes())
                        ), "theMessage"
                    )
                ).path(),
                Matchers.equalTo(path)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can get content.
     * @throws Exception If some problem inside
     */
    @Test
    public void getContent() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final String message = String.format("testMessage");
            final String cont = new String(
                Base64.encodeBase64(
                    String.format("content%d", System.currentTimeMillis())
                        .getBytes()
                )
            );
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(this.jsonObject(path, cont, message));
            final Content content = contents.get(path, "master");
            MatcherAssert.assertThat(
                content.path(),
                Matchers.equalTo(path)
            );
            MatcherAssert.assertThat(
                new Content.Smart(content).content(),
                Matchers.equalTo(String.format("%s\n", cont))
            );
            final Content other = contents.get(path);
            MatcherAssert.assertThat(content, Matchers.equalTo(other));
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can iterate content.
     * @throws Exception If some problem inside
     * @todo #863 unignore after Contents#get is implemented for
     *  directories (#968 and #903)
     */
    @Test
    @Ignore
    public void iteratesContent() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        try {
            final String afile = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final String dir = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final String bfile = String.format(
                "%s/%s",
                dir,
                RandomStringUtils.randomAlphanumeric(Tv.TEN)
            );
            final String message = String.format("testMessage");
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(
                this.jsonObject(
                    afile,
                    new String(
                        Base64.encodeBase64(
                            String.format(
                                "content a:%d",
                                System.currentTimeMillis()
                            ).getBytes()
                        )
                    ),
                    message
                )
            );
            contents.create(
                this.jsonObject(
                    bfile,
                    new String(
                        Base64.encodeBase64(
                            String.format(
                                "content b:%d",
                                System.currentTimeMillis()
                            ).getBytes()
                        )
                    ),
                    message
                )
            );
            final Iterable<Content> iterated = contents.iterate("", "master");
            MatcherAssert.assertThat(
                iterated,
                Matchers.allOf(
                    Matchers.hasItems(contents.get(afile), contents.get(dir)),
                    Matchers.<Content>iterableWithSize(Tv.THREE)
                )
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can check whether content exists or not.
     * @throws Exception if any problem inside.
     */
    @Test
    public void checkExists() throws Exception {
        final Repos repos = new GithubIT().connect().repos();
        final Repo repo = this.rule.repo(repos);
        final String branch = "master";
        try {
            final String path = RandomStringUtils.randomAlphanumeric(Tv.TEN);
            final String cont = new String(
                Base64.encodeBase64(
                    String.format("exist%d", System.currentTimeMillis())
                        .getBytes()
                )
            );
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(this.jsonObject(path, cont, "test exist"));
            MatcherAssert.assertThat(
                contents.exists(path, branch),
                Matchers.is(true)
            );
            MatcherAssert.assertThat(
                contents.exists("content-not-exist.txt", branch),
                Matchers.is(false)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Create and return JsonObject of content.
     * @param path Content's path
     * @param cont Content's Base64 string
     * @param message Message
     * @return JsonObject
     */
    private JsonObject jsonObject(
        final String path, final String cont, final String message
    ) {
        return Json.createObjectBuilder()
            .add("path", path)
            .add("message", message)
            .add("content", cont)
            .add("ref", "master")
            .build();
    }

}
