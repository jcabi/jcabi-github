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
package com.jcabi.github;
/**
 *
 * The code is based on the RtDatatypeConverter from OpenJDK 7u40-b43.
 * See https://github.com/jcabi/jcabi-github/issues/932
 * @author Haris Peco (snpe@gmail60.com)
 * @version $Id$
 *
 */
public final class RtDatatypeConverter {

    /**
     * Buffer.
     */
    private static final int BUFFER = 64;
    /**
     * Constant.
     */
    private static final int C_63 = 63;
    /**
     * Constant.
     */
    private static final int C_52 = 52;
    /**
     * Constant.
     */
    private static final int C_62 = 62;
    /**
     * Constant.
     */
    private static final int C_26 = 26;
    /**
     * Constant.
     */
    private static final int C_0X3F = 0x3F;
    /**
     * Constant.
     */
    private static final int C_0XF = 0xF;
    /**
     * Constant.
     */
    private static final int C_0X3 = 0x3;
    /**
     * Constant.
     */
    private static final int SIX = 6;
    /**
     * Constant.
     */
    private static final int FOUR = 4;
    /**
     * Constant.
     */
    private static final int THREE = 3;
    /**
     * Encoded map.
     */
    private static final char[] ENCODE_MAP = initEncodeMap();

    /**
     * Constructor for utility class.
     */
    private RtDatatypeConverter() {
    }

    /**
     * Returns base64 string.
     * @param data Array of bytes
     * @return String Base64 string
     */
    public static String printBinary(final byte[] data) {
        String value;
        if (isJRE("javax.xml.bind.DatatypeConverter")) {
            value = javax.xml.bind.DatatypeConverter.printBase64Binary(data);
        } else {
            value = printInternal(data);
        }
        return value;
    }

    /**
     * Check is the class exists.return.
     * @param fqn Fully qualified name
     * @return Boolean true if exists
     */
    private static boolean isJRE(final String fqn) {
        boolean ret;
        try {
            Class.forName(fqn);
            ret = true;
        } catch (final ClassNotFoundException ex) {
            try {
                Thread.currentThread().getContextClassLoader().loadClass(fqn);
                ret = true;
            } catch (final ClassNotFoundException ex1) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Returns Base64 string.
     * @param input Array of bytes
     * @return String Base64 string
     */
    private static String printInternal(final byte[] input) {
        return printBinary(input, 0, input.length);
    }

    /**
     * Returns Base64 string.
     * @param input Array of bytes
     * @param offset Int
     * @param len Int
     * @return String Base64 string
     */
    private static String printBinary(final byte[] input,
            final int offset, final int len) {
        final char[] buf = new char[((len + 2) / THREE) * FOUR];
        final int ptr = printBinary(input, offset, len, buf, 0);
        assert ptr == buf.length;
        return new String(buf);
    }

    /**
     * Returns Base64 string.
     * @param input Array of bytes
     * @param offset Int
     * @param len Int
     * @param buf Array of char
     * @param start Int
     * @return String Base64 string
     * @checkstyle ParameterNumber (5 lines)
     * @checkstyle ExecutableStatementCountCheck (30 lines)
     */
    private static int printBinary(final byte[] input, final int offset,
            final int len, final char[] buf, final int start) {
        int remaining = len;
        int index;
        int ptr = start;
        for (index = offset;
                remaining >= THREE;
                remaining -= THREE, index += THREE) {
            buf[ptr] = encode(input[index] >> 2);
            ptr = ptr + 1;
            buf[ptr] = encode(
                    ((input[index] & C_0X3) << FOUR)
                    | ((input[index + 1] >> FOUR) & C_0XF)
            );
            ptr = ptr + 1;
            buf[ptr] = encode(
                    ((input[index + 1] & C_0XF) << 2)
                    | ((input[index + 2] >> SIX) & C_0X3)
            );
            ptr = ptr + 1;
            buf[ptr] = encode(input[index + 2] & C_0X3F);
            ptr = ptr + 1;
        }
        if (remaining == 1) {
            buf[ptr] = encode(input[index] >> 2);
            ptr = ptr + 1;
            buf[ptr] = encode(((input[index]) & C_0X3) << FOUR);
            ptr = ptr + 1;
            buf[ptr] = '=';
            ptr = ptr + 1;
            buf[ptr] = '=';
            ptr = ptr + 1;
        }
        if (remaining == 2) {
            buf[ptr] = encode(input[index] >> 2);
            ptr = ptr + 1;
            buf[ptr] = encode(((input[index] & C_0X3) << FOUR)
                    | ((input[index + 1] >> FOUR) & C_0XF)
            );
            ptr = ptr + 1;
            buf[ptr] = encode((input[index + 1] & C_0XF) << 2);
            ptr = ptr + 1;
            buf[ptr] = '=';
            ptr = ptr + 1;
        }
        return ptr;
    }

    /**
     * Encodes int.
     * @param input Int
     * @return Char Encoded int
     */
    private static char encode(final int input) {
        return ENCODE_MAP[input & C_0X3F];
    }
    /**
     * Initializes map.
     * @return Char Array of chars
     */
    private static char[] initEncodeMap() {
        final char[] map = new char[BUFFER];
        int index;
        // @checkstyle IllegalTokenCheck (1 line)
        for (index = 0; index < C_26; index++) {
            map[index] = (char) ('A' + index);
        }
        // @checkstyle IllegalTokenCheck (1 line)
        for (index = C_26; index < C_52; index++) {
            map[index] = (char) ('a' + (index - C_26));
        }
        // @checkstyle IllegalTokenCheck (1 line)
        for (index = C_52; index < C_62; index++) {
            map[index] = (char) ('0' + (index - C_52));
        }
        map[C_62] = '+';
        map[C_63] = '/';
        return map;
    }
}
