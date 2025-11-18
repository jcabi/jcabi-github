/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XML;
import jakarta.json.Json;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link JsonPatch}.
 * @since 0.5
 */
public final class JsonPatchTest {

    /**
     * JsonPatch can patch an XML.
     */
    @Test
    public void patchesXml() throws IOException {
        final MkStorage storage = new MkStorage.InFile();
        new JsonPatch(storage).patch(
            "/github",
            Json.createObjectBuilder()
                .add("name", "hi you!")
                .add("number", 1)
                .build()
        );
        final XML xml = storage.xml();
        MatcherAssert.assertThat(
            "String does not end with expected value",
            xml.xpath("/github/name/text()").get(0),
            Matchers.describedAs(xml.toString(), Matchers.endsWith("you!"))
        );
    }

}
