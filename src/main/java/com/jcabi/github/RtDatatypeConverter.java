/**
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.jcabi.github;

/**
 *
 * The mostly code in this class is copied from OpenJDK 7u40-b43.
 * See https://github.com/jcabi/jcabi-github/issues/932
 * 
 * @author openjdk
 * @version 1.0
 *
 */
final public class RtDatatypeConverter {

    private static final char[] ENCODE_MAP = initEncodeMap();

    private RtDatatypeConverter() {
    }
    
    public static String printBase64Binary(final byte[] data) {

        String value;
        if (isJRE("javax.xml.bind.DatatypeConverter")) {
            value = javax.xml.bind.DatatypeConverter.printBase64Binary(data);
        } else {
            // there isn't javax.xml.bind.DatatypeConverter; 
            // it is probably Android
            value = printBase64BinaryInternal(data);
        }
        return value;
    }

    /**
     * Check is the class exists.return
     * FIXME create more robust implementation
     * @param fqn
     * @return
     */
    private static boolean isJRE(final String fqn) {
        boolean ret = false;
        try {
            Class.forName(fqn);
            ret = true;
        } catch (ClassNotFoundException e) {
            // ignore ; ret - false
        }
        return ret;
    }

    public static String printBase64BinaryInternal(final byte[] input) {
        return printBase64Binary(input, 0, input.length);
    }

    public static String printBase64Binary(final byte[] input, final int offset, final int len) {
        final char[] buf = new char[((len + 2) / 3) * 4];
        final int ptr = printBase64Binary(input, offset, len, buf, 0);
        assert ptr == buf.length;
        return new String(buf);
    }

    /**
     * Encodes a byte array into a char array by doing base64 encoding.
     *
     * The caller must supply a big enough buffer.
     *
     * @return
     *      the value of {@code ptr+((len+2)/3)*4}, which is the new offset
     *      in the output buffer where the further bytes should be placed.
     */
    public static int printBase64Binary(final byte[] input, final int offset, final int len, final char[] buf, final int p) {
        // encode elements until only 1 or 2 elements are left to encode
        int remaining = len;
        int i;
        int ptr = p;
        for (i = offset;remaining >= 3; remaining -= 3, i += 3) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode(
                    ((input[i] & 0x3) << 4)
                    | ((input[i + 1] >> 4) & 0xF));
            buf[ptr++] = encode(
                    ((input[i + 1] & 0xF) << 2)
                    | ((input[i + 2] >> 6) & 0x3));
            buf[ptr++] = encode(input[i + 2] & 0x3F);
        }
        // encode when exactly 1 element (left) to encode
        if (remaining == 1) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode(((input[i]) & 0x3) << 4);
            buf[ptr++] = '=';
            buf[ptr++] = '=';
        }
        // encode when exactly 2 elements (left) to encode
        if (remaining == 2) {
            buf[ptr++] = encode(input[i] >> 2);
            buf[ptr++] = encode(((input[i] & 0x3) << 4)
                    | ((input[i + 1] >> 4) & 0xF));
            buf[ptr++] = encode((input[i + 1] & 0xF) << 2);
            buf[ptr++] = '=';
        }
        return ptr;
    }

    public static char encode(final int i) {
        return ENCODE_MAP[i & 0x3F];
    }

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        int i;
        for (i = 0; i < 26; i++) {
            map[i] = (char) ('A' + i);
        }
        for (i = 26; i < 52; i++) {
            map[i] = (char) ('a' + (i - 26));
        }
        for (i = 52; i < 62; i++) {
            map[i] = (char) ('0' + (i - 52));
        }
        map[62] = '+';
        map[63] = '/';

        return map;
    }
}
