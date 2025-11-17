/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
                return Smarts.wrap(iterator.next());
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
