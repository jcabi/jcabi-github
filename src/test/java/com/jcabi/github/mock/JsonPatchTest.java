/**
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github.mock;

import com.jcabi.xml.XML;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link JsonPatch}.
 */
public final class JsonPatchTest {

    /**
     * JsonPatch can patch an XML.
     * @throws Exception If some problem inside
     */
    @Test
    public void patchesXml() throws Exception {
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
            xml.xpath("/github/name/text()").get(0),
            Matchers.describedAs(xml.toString(), Matchers.endsWith("you!"))
        );
    }

}
