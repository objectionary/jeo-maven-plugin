/*
 * MIT License
 *
 * Copyright (c) 2022-2023 Volodya
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
//Check logs first.
String log = new File(basedir, 'build.log').text;
assert log.contains("BUILD SUCCESS")
assert log.contains("Result: 31")
//Check that we have generated all the required XMIR object files.
assert new File(basedir, 'target/jeo/xmir/org/eolang/jeo/Main.xmir').exists()
assert new File(basedir, 'target/jeo/xmir/org/eolang/jeo/A.xmir').exists()
assert new File(basedir, 'target/jeo/xmir/org/eolang/jeo/B.xmir').exists()
/**
 * @todo #150:90min Implement generation of AB class.
 *  Currently, AB class is not generated, because optimization is not
 *  implemented yet. We also have to check that we generate AB class
 *  after optimization application. When optimization is implemented,
 *  we should uncomment the following line.
 */
//assert new File(basedir, 'target/jeo/xmir/org/eolang/jeo/AB.xmir').exists()
//Check that class file was changed
assert log.contains("Main.class was recompiled successfully.")
true