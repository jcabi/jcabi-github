/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import jakarta.json.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import lombok.EqualsAndHashCode;

/**
 * Bulk items, with pre-saved JSON.
 *
 * <p>This class should be used as a decorator for object obtained
 * from GitHub, when you want to keep their JSON values in memory. For
 * example:
 *
 * <pre> Iterable&lt;Issue&gt; issues = repo.issues().iterate(
 *   new HashMap&lt;String, String&gt;()
 * );
 * for (Issue issue : issues) {
 *   System.out.println(new Issue.Smart(issue).title());
 * }</pre>
 *
 * <p>Let's say, there are 50 issues in GitHub's repo. This code will
 * make 52 HTTP requests to GitHub. The first one will fetch the first
 * 30 issues in JSON array. Then, for every one of them, in order
 * to retrieve issue title a separate HTTP request will be made. Then,
 * one more page will be fetched, with 20 issues. And again, 20 new
 * HTTP requests to get their titles.
 *
 * <p>Class {@code Bulk} helps us to reduce the amount of this extra work:
 *
 * <pre> Iterable&lt;Issue&gt; issues = new Bulk&lt;Issue&gt;(
 *   repo.issues().iterate(
 *     new HashMap&lt;String, String&gt;()
 *   )
 * );</pre>
 *
 * <p>Now, there will be just two HTTP requests.
 * @param <T> Type of iterable objects
 * @see <a href="https://developer.github.com/v3/#pagination">Pagination</a>
 * @since 0.4
 */
@EqualsAndHashCode(of = "origin")
public final class Bulk<T extends JsonReadable> implements Iterable<T> {

    /**
     * Original iterable.
     */
    private final transient Iterable<T> origin;

    /**
     * Public ctor.
     * @param items Items original
     * @checkstyle AnonInnerLength (50 lines)
     */
    @SuppressWarnings({"unchecked", "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"})
    public Bulk(final Iterable<T> items) {
        if (items instanceof RtPagination) {
            final RtPagination<T> page = RtPagination.class.cast(items);
            final RtValuePagination.Mapping<T, JsonObject> mapping =
                page.mapping();
            this.origin = new RtPagination<>(
                page.request(),
                object -> {
                    final T item = mapping.map(object);
                    return (T) Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        item.getClass().getInterfaces(),
                        (proxy, method, args) -> {
                            final Object result;
                            if ("json".equals(method.getName())) {
                                result = object;
                            } else {
                                try {
                                    result = method.invoke(item, args);
                                } catch (
                                    final IllegalAccessException
                                        | InvocationTargetException ex
                                ) {
                                    throw new IllegalStateException(ex);
                                }
                            }
                            return result;
                        }
                    );
                }
            );
        } else {
            this.origin = items;
        }
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return this.origin.iterator();
    }

}
