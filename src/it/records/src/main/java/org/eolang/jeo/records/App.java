/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.records;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({RECORD_COMPONENT, FIELD, METHOD, PARAMETER})
@interface Label {
    String value();
}

@Documented
@Retention(RUNTIME)
@Target(RECORD_COMPONENT)
@interface ComponentTag {
    String value();
}

// Package-private record; single public top-level type per file.
record User(
    @Label("username") @ComponentTag("primary-id") String name,
    @Label("age-years") int age
) {}

public class App {

    public static void main(String[] args) throws Exception {
        // --- Record components ---
        var components = User.class.getRecordComponents();
        check(components.length == 2, "Two record components expected");

        var rcA = components[0];
        var rcB = components[1];
        var rcName = "name".equals(rcA.getName()) ? rcA : rcB;
        var rcAge  = rcName == rcA ? rcB : rcA;

        checkEquals("name", rcName.getName(), "Name component name");
        check(rcName.getType() == String.class, "Name component type is String");
        var nameLabel = rcName.getAnnotation(Label.class);
        check(nameLabel != null && "username".equals(nameLabel.value()),
            "@Label on name component");
        var nameTag = rcName.getAnnotation(ComponentTag.class);
        check(nameTag != null && "primary-id".equals(nameTag.value()),
            "@ComponentTag on name component");

        checkEquals("age", rcAge.getName(), "Age component name");
        check(rcAge.getType() == int.class, "Age component type is int");
        var ageLabel = rcAge.getAnnotation(Label.class);
        check(ageLabel != null && "age-years".equals(ageLabel.value()),
            "@Label on age component");
        check(rcAge.getAnnotation(ComponentTag.class) == null,
            "No @ComponentTag on age component");

        // --- Backing fields (propagation when @Target includes FIELD) ---
        Field fName = User.class.getDeclaredField("name");
        Field fAge  = User.class.getDeclaredField("age");
        var fNameLabel = fName.getAnnotation(Label.class);
        var fAgeLabel  = fAge.getAnnotation(Label.class);
        check(fNameLabel != null && "username".equals(fNameLabel.value()),
            "@Label propagated to field 'name'");
        check(fAgeLabel != null && "age-years".equals(fAgeLabel.value()),
            "@Label propagated to field 'age'");
        check(fName.getAnnotation(ComponentTag.class) == null,
            "@ComponentTag should NOT be on fields");
        check(fAge.getAnnotation(ComponentTag.class) == null,
            "@ComponentTag should NOT be on fields");

        // --- Accessor methods (propagation when @Target includes METHOD) ---
        Method mName = User.class.getDeclaredMethod("name");
        Method mAge  = User.class.getDeclaredMethod("age");
        var mNameLabel = mName.getAnnotation(Label.class);
        var mAgeLabel  = mAge.getAnnotation(Label.class);
        check(mNameLabel != null && "username".equals(mNameLabel.value()),
            "@Label propagated to accessor name()");
        check(mAgeLabel != null && "age-years".equals(mAgeLabel.value()),
            "@Label propagated to accessor age()");
        check(mName.getAnnotation(ComponentTag.class) == null,
            "@ComponentTag should NOT be on accessors");
        check(mAge.getAnnotation(ComponentTag.class) == null,
            "@ComponentTag should NOT be on accessors");

        // --- Canonical constructor parameters (propagation when @Target includes PARAMETER) ---
        Constructor<User> ctor = User.class.getDeclaredConstructor(String.class, int.class);
        Parameter[] params = ctor.getParameters();
        check(params.length == 2, "Two constructor parameters expected");
        var p0Label = params[0].getAnnotation(Label.class);
        var p1Label = params[1].getAnnotation(Label.class);
        check(p0Label != null && "username".equals(p0Label.value()),
            "@Label propagated to ctor param 0");
        check(p1Label != null && "age-years".equals(p1Label.value()),
            "@Label propagated to ctor param 1");

        // --- Basic record behavior sanity checks ---
        var u = new User("alice", 30);
        checkEquals("alice", u.name(), "Accessor name()");
        check(u.age() == 30, "Accessor age() == 30");
        check(u.toString().contains("alice") && u.toString().contains("30"),
            "toString contains component values");

        // If we got here, everything matched.
        System.out.println("All checks passed.");
    }

    // --- Minimal plain-Java "assertions" ---
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static void checkEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }
}
