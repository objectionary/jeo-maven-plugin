/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.hone;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public class JnaPrinter {

    public String hello() {
        Pointer buffer = new Pointer(Native.malloc(100));
        int length = CLibrary.INSTANCE.sprintf(
            buffer,
            "Hello from %s! Running on %s\n", "JNA",
            Platform.isWindows() ? "Windows" : "Unix"
        );
        try {
            return buffer.getString(0);
        } finally {
            Native.free(Pointer.nativeValue(buffer));
        }
    }

    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.load(
            Platform.isWindows() ? "msvcrt" : "c",
            CLibrary.class
        );

        int sprintf(Pointer buffer, String format, Object... args);

        void printf(String format, Object... args);
    }
}
