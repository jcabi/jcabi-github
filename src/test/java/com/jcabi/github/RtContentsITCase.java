/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtContents}.
 * @since 0.8
 */
@OAuthScope(OAuthScope.Scope.REPO)
final class RtContentsITCase {

    @Test
    void canFetchReadmeFiles() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            MatcherAssert.assertThat(
                "Values are not equal",
                repos.get(repo.coordinates()).contents().readme().path(),
                Matchers.equalTo("README.md")
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void canUpdateFileContent() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final String message = "commit message";
            final Content content = contents.create(
                RtContentsITCase.jsonObject(
                    path, new String(
                        Base64.encodeBase64("init content".getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8
                    ),
                    message
                )
            );
            final String text = "new content";
            contents.update(
                path,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message).add(
                        "content",
                        Base64.encodeBase64String(text.getBytes(StandardCharsets.UTF_8))
                    )
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new String(
                    Base64.decodeBase64(
                        new Content.Smart(
                            contents.get(path, "master")
                        ).content()
                    ),
                    StandardCharsets.UTF_8
                ),
                Matchers.equalTo(text)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void canUpdateFileContentInSpecificBranch() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final String message = "Commit message";
            final Content content = contents.create(
                RtContentsITCase.jsonObject(
                    path, new String(
                        Base64.encodeBase64("Initial.".getBytes(StandardCharsets.UTF_8)),
                        StandardCharsets.UTF_8
                    ),
                    message
                )
            );
            final String text = "Updated";
            contents.update(
                path,
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message)
                    .add("ref", "master").add(
                        "content",
                        Base64.encodeBase64String(text.getBytes(StandardCharsets.UTF_8))
                    )
                    .add("sha", new Content.Smart(content).sha()).build()
            );
            MatcherAssert.assertThat(
                "Values are not equal",
                new String(
                    Base64.decodeBase64(
                        new Content.Smart(
                            contents.get(path, "master")
                        ).content()
                    ),
                    StandardCharsets.UTF_8
                ),
                Matchers.equalTo(text)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void throwsWhenTryingToGetAnAbsentContent() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        final Contents contents = repos.get(repo.coordinates()).contents();
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final String message = "commit message";
            contents.remove(
                Json.createObjectBuilder()
                    .add("path", path)
                    .add("message", message).add(
                        "sha",
                        new Content.Smart(
                            contents.create(
                                RtContentsITCase.jsonObject(
                                    path,
                                    new String(
                                        Base64.encodeBase64(
                                            "first content".getBytes(
                                                StandardCharsets.UTF_8
                                            )
                                        ),
                                        StandardCharsets.UTF_8
                                    ),
                                    message
                                )
                            )
                        ).sha()
                    ).build()
            );
            Assertions.assertThrows(
                AssertionError.class,
                () -> contents.get(path, "master"),
                "Absent content is not reported as an error"
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void canCreateFileContent() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            MatcherAssert.assertThat(
                "Values are not equal",
                repos.get(repo.coordinates()).contents().create(
                    RtContentsITCase.jsonObject(
                        path, new String(
                            Base64.encodeBase64("some content".getBytes(StandardCharsets.UTF_8)),
                            StandardCharsets.UTF_8
                        ), "theMessage"
                    )
                ).path(),
                Matchers.equalTo(path)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void fetchesContentPath() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(
                RtContentsITCase.jsonObject(
                    path,
                    RtContentsITCase.encoded("content"),
                    "testMessage"
                )
            );
            MatcherAssert.assertThat(
                "Fetched content has a wrong path",
                contents.get(path, "master").path(),
                Matchers.equalTo(path)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void fetchesContentBody() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final String cont = RtContentsITCase.encoded("content");
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(
                RtContentsITCase.jsonObject(path, cont, "testMessage")
            );
            MatcherAssert.assertThat(
                "Fetched content has a wrong body",
                new Content.Smart(contents.get(path, "master")).content(),
                Matchers.equalTo(String.format("%s%n", cont))
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void fetchesContentOfDefaultBranch() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(
                RtContentsITCase.jsonObject(
                    path,
                    RtContentsITCase.encoded("content"),
                    "testMessage"
                )
            );
            MatcherAssert.assertThat(
                "Content of the default branch is different",
                contents.get(path, "master"),
                Matchers.equalTo(contents.get(path))
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * RtContents can iterate content.
     * @todo #863 unignore after Contents#get is implemented for
     *  directories (#968 and #903)
     */
    @Test
    @Disabled
    void iteratesContent() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            final String afile = RandomStringUtils.secure().nextAlphanumeric(10);
            final String dir = RandomStringUtils.secure().nextAlphanumeric(10);
            final String bfile = String.format(
                "%s/%s",
                dir,
                RandomStringUtils.secure().nextAlphanumeric(10)
            );
            final String message = String.format("testMessage");
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(
                RtContentsITCase.jsonObject(
                    afile,
                    new String(
                        Base64.encodeBase64(
                            String.format(
                                "content a:%d",
                                System.currentTimeMillis()
                            ).getBytes(StandardCharsets.UTF_8)
                        ),
                        StandardCharsets.UTF_8
                    ),
                    message
                )
            );
            contents.create(
                RtContentsITCase.jsonObject(
                    bfile,
                    new String(
                        Base64.encodeBase64(
                            String.format(
                                "content b:%d",
                                System.currentTimeMillis()
                            ).getBytes(StandardCharsets.UTF_8)
                        ),
                        StandardCharsets.UTF_8
                    ),
                    message
                )
            );
            MatcherAssert.assertThat(
                "Collection size is incorrect",
                contents.iterate("", "master"),
                Matchers.allOf(
                    Matchers.hasItems(contents.get(afile), contents.get(dir)),
                    Matchers.iterableWithSize(3)
                )
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void checksExistingContent() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            final String path = RandomStringUtils.secure().nextAlphanumeric(10);
            final Contents contents = repos.get(repo.coordinates()).contents();
            contents.create(
                RtContentsITCase.jsonObject(
                    path,
                    RtContentsITCase.encoded("exist"),
                    "test exist"
                )
            );
            MatcherAssert.assertThat(
                "Existing content is reported as absent",
                contents.exists(path, "master"),
                Matchers.is(true)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    @Test
    void checksAbsentContent() throws IOException {
        final Repos repos = GitHubIT.connect().repos();
        final Repo repo = new RepoRule().repo(repos);
        try {
            MatcherAssert.assertThat(
                "Absent content is reported as existing",
                repos.get(repo.coordinates()).contents()
                    .exists("content-not-exist.txt", "master"),
                Matchers.is(false)
            );
        } finally {
            repos.remove(repo.coordinates());
        }
    }

    /**
     * Base64 encoded unique content.
     * @param prefix Prefix of the content
     * @return Encoded content
     */
    private static String encoded(final String prefix) {
        return new String(
            Base64.encodeBase64(
                String.format(
                    "%s%d", prefix, System.currentTimeMillis()
                ).getBytes(StandardCharsets.UTF_8)
            ),
            StandardCharsets.UTF_8
        );
    }

    /**
     * Create and return JsonObject of content.
     * @param path Content's path
     * @param cont Content's Base64 string
     * @param message Message
     * @return JsonObject
     */
    private static JsonObject jsonObject(
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
