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

import com.jcabi.log.Logger;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import lombok.EqualsAndHashCode;

/**
 * Smart items.
 *
 * <p>This class should be used as a decorator for an iterable of objects, for
 * example:
 *
 * <pre> Iterable&lt;Issue.Smart&gt; issues = new Smarts&lt;Issue.Smart&gt;(
 *   repo.issues().iterate(
 *     new HashMap&lt;String, String&gt;()
 *   )
 * );
 * for (Issue.Smart issue : issues) {
 *     System.out.println("state is: " + issue.state());
 * }</pre>
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 * @param <T> Type of iterable objects
 */
@EqualsAndHashCode(of = "origin")
public final class Smarts<T> implements Iterable<T> {

    /**
     * Original iterable.
     */
    private final transient Iterable<?> origin;

    /**
     * Public ctor.
     * @param items Items original
     */
    public Smarts(
        final Iterable<?> items
    ) {
        this.origin = items;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<?> iterator = this.origin.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override
            public T next() {
                return Smarts.<T>wrap(iterator.next());
            }
            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    /**
     * Wrap an object, and make a "smart" decorator.
     * @param object Object to wrap
     * @return Decorator
     * @param <X> Type of result
     */
    @SuppressWarnings("unchecked")
    private static <X> X wrap(final Object object) {
        try {
            return (X) Smarts.type(object).newInstance(object);
        } catch (final InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        } catch (final InstantiationException ex) {
            throw new IllegalStateException(ex);
        } catch (final IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Find "smart" class for the object.
     * @param object Object to wrap
     * @return Class to use
     */
    private static Constructor<?> type(final Object object) {
        for (final Class<?> iface : object.getClass().getInterfaces()) {
            try {
                return Class.forName(
                    String.format("%s$Smart", iface.getName())
                ).getDeclaredConstructor(iface);
            } catch (final ClassNotFoundException ex) {
                Logger.debug(Smarts.class, "%s: %s", iface.getName(), ex);
            } catch (final NoSuchMethodException ex) {
                throw new IllegalStateException(ex);
            }
        }
        throw new IllegalStateException(
            String.format(
                "can't find Smart decorator for %s",
                object.getClass().getName()
            )
        );
    }

}
