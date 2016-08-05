/**
 * Copyright (c) 2013-2015, jcabi.com
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
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Release;
import com.jcabi.github.Releases;
import com.jcabi.github.Repo;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github releases.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.8
 */
@Immutable
@Loggable(Loggable.DEBUG)
@ToString
@EqualsAndHashCode(of = { "storage", "self", "coords" })
final class MkReleases implements Releases {

    /**
     * Storage.
     */
    private final transient MkStorage storage;

    /**
     * Login of the user logged in.
     */
    private final transient String self;

    /**
     * Repo name.
     */
    private final transient Coordinates coords;

    /**
     * Public ctor.
     * @param stg Storage
     * @param login User to login
     * @param rep Repo
     * @throws IOException If there is any I/O problem
     */
    public MkReleases(
        final MkStorage stg,
        final String login,
        final Coordinates rep
    ) throws IOException {
        this.storage = stg;
        this.self = login;
        this.coords = rep;
        this.storage.apply(
            new Directives().xpath(
                String.format(
                    "/github/repos/repo[@coords='%s']",
                    this.coords
                )
            ).addIf("releases")
        );
    }

    @Override
    public Repo repo() {
        return new MkRepo(this.storage, this.self, this.coords);
    }

    @Override
    public Iterable<Release> iterate() {
        return new MkIterable<Release>(
            this.storage,
            String.format("%s/release", this.xpath()),
            new MkIterable.Mapping<Release>() {
                @Override
                public Release map(final XML xml) {
                    return MkReleases.this.get(
                        Integer.parseInt(xml.xpath("id/text()").get(0))
                    );
                }
            }
        );
    }

    @Override
    public Release get(final int number) {
        return new MkRelease(this.storage, this.self, this.coords, number);
    }

    @Override
    public Release latest() throws IOException {
        Release latest = null;
        for (final Release release: this.iterate()) {
            if (latest == null) {
                latest = release;
            } else if (this.isNewer(latest, release)) {
                latest = release;
            }
        }
        return latest;
    }

    @Override
    public Release tagged(final String name) throws IOException {
        Release tagged = null;
        for (final Release release: this.iterate()) {
            if (name.equals(this.smart(release).tag())) {
                tagged = release;
                break;
            }
        }
        return tagged;
    }

    @Override
    public Release create(
        final String tag
    ) throws IOException {
        this.storage.lock();
        final int number;
        try {
            number = 1 + this.storage.xml().xpath(
                String.format("%s/release/id/text()", this.xpath())
            ).size();
            this.storage.apply(
                new Directives().xpath(this.xpath()).add("release")
                    .add("id").set(Integer.toString(number)).up()
                    .add("tag_name").set(tag).up()
                    .add("target_commitish").set("master").up()
                    .add("name").set("v1.0.0").up()
                    .add("body").set("Description of the release").up()
                    .add("draft").set("true").up()
                    .add("prerelease").set("false").up()
                    .add("created_at").set(new Github.Time().toString()).up()
                    .add("published_at").set(new Github.Time().toString()).up()
                    .add("url").set("http://localhost/1").up()
                    .add("html_url").set("http://localhost/2").up()
                    .add("assets_url").set("http://localhost/3").up()
                    .add("upload_url").set("http://localhost/4").up()
            );
        } finally {
            this.storage.unlock();
        }
        return this.get(number);
    }

    @Override
    public void remove(final int number) throws IOException {
        this.storage.lock();
        try {
            this.storage.apply(
                new Directives().xpath(
                    String.format("%s/release[id='%d']", this.xpath(), number)
                ).remove()
            );
        } finally {
            this.storage.unlock();
        }
    }

    /**
     * XPath of this element in XML tree.
     * @return XPath
     */
    private String xpath() {
        return String.format(
            "/github/repos/repo[@coords='%s']/releases",
            this.coords
        );
    }

    /**
     * Check the first release is greater than last release.
     * @param first First release.
     * @param last Last release
     * @return True if it this
     */
    private boolean isNewer(
            final Release first,
            final Release last
    ) {
        return this.createdAt(first)
                .compareTo(this.createdAt(last)) > 0;
    }

    /**
     * Verify the date time release was created.
     * @param release Release date
     * @return Zero or DateTime
     */
    private Long createdAt(final Release release) {
        Long result = 0L;
        try {
            final Date created = this.smart(release).createdAt();
            if (created != null) {
                result = created.getTime();
            }
        } catch (final IOException iox) {
            throw new IllegalArgumentException(iox);
        }
        return result;
    }

    /**
     * Create a smart release.
     * @param release Release value
     * @return Smart release.
     */
    private Release.Smart smart(final Release release) {
        return new Release.Smart(release);
    }
}
