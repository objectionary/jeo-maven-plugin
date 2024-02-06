/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.bytecode;

import org.eolang.jeo.PluginStartup;
import org.objectweb.asm.ClassWriter;

/**
 * Custom class writer.
 * This class works in couple with {@link PluginStartup#init()} ()} method that sets
 * the maven classloader as the current thread classloader.
 * Originally we faced with the problem that {@link ClassWriter} uses classes from ClassLoader
 * to perform {@link org.objectweb.asm.MethodVisitor#visitMaxs(int, int)} method and if it can't
 * find the class it throws {@link ClassNotFoundException}. To prevent this we override
 * {@link ClassWriter#getClassLoader()} method and return the current thread classloader that
 * knows about all classes that were compiled on the previous maven phases.
 * You can read more about this problem here:
 * - https://gitlab.ow2.org/asm/asm/-/issues/317918
 * - https://stackoverflow.com/questions/11292701/error-while-instrumenting-class-files-asm-classwriter-getcommonsuperclass
 *
 * @since 0.1
 */
public class CustomClassWriter extends ClassWriter {

    /**
     * Constructor.
     */
    CustomClassWriter() {
        super(ClassWriter.COMPUTE_FRAMES);
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
