/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.http.Request;
import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

/**
 * Test case for {@link RtTrees}.
 * @since 0.8
 */
@ExtendWith(RandomPort.class)
final class RtTreesTest {

    @Test
    void createsTree() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtTreesTest.answer())
                .start(RandomPort.port())
        ) {
            MatcherAssert.assertThat(
                "Created tree is of a wrong type",
                RtTreesTest.create(RtTreesTest.trees(container)),
                Matchers.instanceOf(Tree.class)
            );
        }
    }

    @Test
    void findsCreatedTree() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtTreesTest.answer())
                .start(RandomPort.port())
        ) {
            final Trees trees = RtTreesTest.trees(container);
            final Tree tree = RtTreesTest.create(trees);
            MatcherAssert.assertThat(
                "Created tree is not found",
                trees.get(tree.sha()),
                Matchers.equalTo(tree)
            );
        }
    }

    @Test
    void createsTreeWithPost() throws IOException {
        try (
            MkContainer container = new MkGrizzlyContainer()
                .next(RtTreesTest.answer())
                .start(RandomPort.port())
        ) {
            RtTreesTest.create(RtTreesTest.trees(container));
            MatcherAssert.assertThat(
                "Tree is not created with POST",
                container.take().method(),
                Matchers.equalTo(Request.POST)
            );
        }
    }

    @Test
    void fetchesTree() {
        final String sha = "0abcd89jcabitest";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtTrees(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("sha", sha)
                        .build()
                        .toString()
                ),
                RtTreesTest.repo()
            ).get(sha).sha(), Matchers.equalTo(sha)
        );
    }

    @Test
    void fetchesTreeRecursively() {
        final String sha = "0abcd89jcabitest";
        MatcherAssert.assertThat(
            "Values are not equal",
            new RtTrees(
                new FakeRequest().withBody(
                    Json.createObjectBuilder()
                        .add("sha", sha)
                        .build()
                        .toString()
                ),
                RtTreesTest.repo()
            ).getRec(sha).sha(), Matchers.equalTo(sha)
        );
    }

    /**
     * Trees served by the given container.
     * @param container Container to serve the trees
     * @return Trees
     * @throws IOException If there is any I/O problem
     */
    private static Trees trees(final MkContainer container) throws IOException {
        return new RtTrees(
            new ApacheRequest(container.home()),
            RtTreesTest.repo()
        );
    }

    /**
     * Create a tree in the given trees.
     * @param trees Trees to create the tree in
     * @return Created tree
     * @throws IOException If there is any I/O problem
     */
    private static Tree create(final Trees trees) throws IOException {
        return trees.create(
            Json.createObjectBuilder().add(
                "tree",
                Json.createObjectBuilder()
                    .add("path", "/path").add("mode", "100644 ")
                    .add("type", "blob").add("sha", "sha1")
                    .add("content", "content1")
            ).add("base_tree", "SHA1").build()
        );
    }

    /**
     * Answer with a created tree.
     * @return Answer
     */
    private static MkAnswer answer() {
        return new MkAnswer.Simple(
            HttpURLConnection.HTTP_CREATED,
            "{\"sha\":\"0abcd89jcabitest\", \"url\":\"http://localhost/1\"}"
        );
    }

    /**
     * Create and return repo to test.
     * @return Repo
     */
    private static Repo repo() {
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.doReturn(new Coordinates.Simple("mark", "test"))
            .when(repo).coordinates();
        return repo;
    }
}
