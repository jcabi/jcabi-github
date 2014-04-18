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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

/**
 * Bulk items, with pre-saved JSON.
 *
 * <p>This class should be used as a decorator for object obtained
 * from Github, when you want to keep their JSON values in memory. For
 * example:
 *
 * <pre> Iterable&lt;Issue&gt; issues = repo.issues().iterate(
 *   new HashMap&lt;String, String&gt;()
 * );
 * for (Issue issue : issues) {
 *   System.out.println(new Issue.Smart(issue).title());
 * }</pre>
 *
 * <p>Let's say, there are 50 issues in Github's repo. This code will
 * make 52 HTTP requests to Github. The first one will fetch the first
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
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.4
 * @param <T> Type of iterable objects
 * @see <a href="http://developer.github.com/v3/#pagination">Pagination</a>
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
    @SuppressWarnings("unchecked")
    public Bulk(
        @NotNull(message = "items can't be NULL") final Iterable<T> items
    ) {
        if (items instanceof RtPagination) {
            final RtPagination<T> page = RtPagination.class.cast(items);
            final RtPagination.Mapping<T, JsonObject> mapping = page.mapping();
            this.origin = new RtPagination<T>(
                page.request(),
                new RtPagination.Mapping<T, JsonObject>() {
                    @Override
                    public T map(final JsonObject object) {
                        final T item = mapping.map(object);
                        return (T) Proxy.newProxyInstance(
                            Thread.currentThread().getContextClassLoader(),
                            item.getClass().getInterfaces(),
                            new InvocationHandler() {
                                @Override
                                public Object invoke(final Object proxy,
                                    final Method method, final Object[] args) {
                                    final Object result;
                                    if ("json".equals(method.getName())) {
                                        result = object;
                                    } else {
                                        try {
                                            result = method.invoke(item, args);
                                        } catch (IllegalAccessException ex) {
                                            throw new IllegalStateException(ex);
                                        } catch (InvocationTargetException ex) {
                                            throw new IllegalStateException(ex);
                                        }
                                    }
                                    return result;
                                }
                            }
                        );
                    }
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
    @NotNull(message = "iterator is never NULL")
    public Iterator<T> iterator() {
        return this.origin.iterator();
    }

}
