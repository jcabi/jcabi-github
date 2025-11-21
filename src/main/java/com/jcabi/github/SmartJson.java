/*
 * SPDX-FileCopyrightText: Copyright (c) 2013-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Smart JSON (supplementary help class).
 * @since 0.5
 */
@Immutable
@ToString
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = "object")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class SmartJson {

    /**
     * Encapsulated JSON object.
     */
    private final transient JsonReadable object;

    /**
     * Public ctor.
     * @param obj Readable object
     */
    SmartJson(final JsonReadable obj) {
        this.object = obj;
    }

    /**
     * Get its property as string.
     * @param name Name of the property
     * @return Value
     * @throws IOException If there is any I/O problem
     */
    public String text(
        final String name
    ) throws IOException {
        return this.value(name, JsonString.class).getString();
    }

    /**
     * Get its property as number.
     * @param name Name of the property
     * @return Value
     * @throws IOException If there is any I/O problem
     */
    public int number(
        final String name
    ) throws IOException {
        return this.value(name, JsonNumber.class).intValue();
    }

    /**
     * Get JSON.
     * @return JSON
     * @throws IOException If there is any I/O problem
     * @since 0.14
     */
    public JsonObject json() throws IOException {
        return this.object.json();
    }

    /**
     * Get its property as custom type.
     * @param name Name of the property
     * @param type Type of result expected
     * @param <T> Type expected
     * @return Value
     * @throws IOException If there is any I/O problem
     */
    public <T> T value(
        final String name,
        final Class<T> type
    ) throws IOException {
        final JsonObject json = this.json();
        if (!json.containsKey(name)) {
            throw new IllegalStateException(
                String.format(
                    "'%s' is absent in JSON: %s", name, json
                )
            );
        }
        final JsonValue value = json.get(name);
        if (value == null) {
            throw new IllegalStateException(
                String.format(
                    "'%s' is NULL in %s", name, json
                )
            );
        }
        if (value.getClass().isAssignableFrom(type)) {
            throw new IllegalStateException(
                String.format(
                    "'%s' is not of type %s", name, type
                )
            );
        }
        return type.cast(value);
    }

    /**
     * Checks if a certain key is present
     *  AND its ValueType isn't ValueType.NULL.
     * @param name Name of the key which ValueType should be checked.
     * @return Returns <code>true</code> if key <code>name</code> is present
     *  and its ValueType isn't ValueType.NULL, <code>false</code> otherwise.
     * @throws IOException If there is any I/O problem
     */
    public boolean hasNotNull(
        final String name
    ) throws IOException {
        final JsonValue value = this.object.json().get(name);
        return value != null
            && !JsonValue.ValueType.NULL.equals(value.getValueType());
    }
}
