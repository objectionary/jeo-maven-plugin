/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * A self-checking class with complex generics.
 *
 * <T> is a Number that is Comparable to itself.
 * <U> is any List whose element type is a supertype of T.
 */
public final class GenericSelfCheck<T extends Number & Comparable<T>, U extends List<? super T>> {

    // --- Fields with complex generic shapes (for reflection checks) ---

    // Map<String, List<? extends Number>>
    private final Map<String, List<? extends Number>> ratings = new HashMap<>();

    // List<Map<String, Set<List<Integer>>>>
    private final List<Map<String, Set<List<Integer>>>> deep = new ArrayList<>();

    // List<? super Integer>
    private final List<? super Integer> sink = new ArrayList<>();

    // Owner type example: Map.Entry<String, Integer>
    private final Entry<String, Integer> pair = new AbstractMap.SimpleEntry<>("x", 42);

    // Generic array type: List<String>[]
    @SuppressWarnings("unused")
    private final List<String>[] buckets = null;

    // Wildcard with upper bound & nested parameterization
    private final Optional<List<? extends T>> maybe = Optional.empty();

    // --- Constructors (one is generic to test constructor type parameters) ---

    public GenericSelfCheck() {
    }

    // Generic constructor: <Z extends CharSequence>()
    public <Z extends CharSequence> GenericSelfCheck(String seed, Z note) {
        // No-op; just here to give us generic constructor type parameters to inspect.
        ratings.put(seed, List.of());
    }

    // --- Generic methods to be inspected ---

    // Method with multi-bounded type variable and generic return/params.
    public static <K extends Number & Comparable<K>, V> Map<K, List<V>> group(Map<K, V> in) {
        Map<K, List<V>> out = new HashMap<>();
        for (Map.Entry<K, V> e : in.entrySet()) {
            out.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(e.getValue());
        }
        return out;
    }

    // Method using class type variable as a bound on method type variable.
    public <X extends T> List<X> echo(List<X> xs) {
        return xs;
    }

    // Wildcards in parameters and return types.
    public List<? super T> widen(List<? super T> xs) {
        return xs;
    }

    // --- The required self-check method ---

    /**
     * Scans this class with reflection and validates its generic signatures.
     * Throws IllegalStateException if any expectation does not hold.
     */
    public void check() {
        Class<?> cls = GenericSelfCheck.class;

        // 1) Class type parameters: <T extends Number & Comparable<T>, U extends List<? super T>>
        TypeVariable<?>[] tp = cls.getTypeParameters();
        require(tp.length == 2, "Expected 2 class type parameters");
        // T
        require(tp[0].getName().equals("T"), "First type param should be T");
        Type[] tBounds = tp[0].getBounds();
        require(tBounds.length == 2, "T should have two bounds: Number & Comparable<T>");
        require(isClass(tBounds[0], Number.class), "First bound of T must be Number");
        require(isComparableOfSelf(tBounds[1], tp[0]), "Second bound of T must be Comparable<T>");
        // U
        require(tp[1].getName().equals("U"), "Second type param should be U");
        Type[] uBounds = tp[1].getBounds();
        require(uBounds.length == 1, "U should have a single bound");
        // U bound is List<? super T>
        require(isListOfWildcardSuper(uBounds[0], tp[0]), "U must extend List<? super T>");

        // 2) Generic constructor type parameters: <Z extends CharSequence>(String, Z)
        try {
            Constructor<?> ctor = cls.getDeclaredConstructor(String.class, CharSequence.class);
            TypeVariable<?>[] ctp = ctor.getTypeParameters();
            require(ctp.length == 1, "Generic ctor should have one type param");
            require(ctp[0].getName().equals("Z"), "Ctor type param should be Z");
            Type[] zBounds = ctp[0].getBounds();
            require(
                zBounds.length == 1 && isClass(zBounds[0], CharSequence.class),
                "Z must extend CharSequence"
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Expected generic constructor not found", e);
        }

        // 3) Fields

        // Map<String, List<? extends Number>> ratings
        checkMapStringToListExtendsNumber(getFieldGenericType("ratings"));

        // List<Map<String, Set<List<Integer>>>> deep
        checkDeepNested(getFieldGenericType("deep"));

        // List<? super Integer> sink
        checkListSuperInteger(getFieldGenericType("sink"));

        // Map.Entry<String, Integer> pair (check owner type and args)
        checkMapEntryOfStringInteger(getFieldGenericType("pair"));

        // List<String>[] buckets (generic array type)
        checkGenericArrayListOfString(getFieldGenericType("buckets"));

        // Optional<List<? extends T>> maybe
        checkOptionalListExtendsT(getFieldGenericType("maybe"), tp[0]);

        // 4) Methods

        // static <K extends Number & Comparable<K>, V> Map<K, List<V>> group(Map<K, V>)
        try {
            Method m = cls.getDeclaredMethod("group", Map.class);
            TypeVariable<Method>[] mtp = m.getTypeParameters();
            require(mtp.length == 2, "group() should declare <K, V>");
            // K bounds
            TypeVariable<?> K = mtp[0];
            require(K.getName().equals("K"), "First method type parameter should be K");
            Type[] kBounds = K.getBounds();
            require(kBounds.length == 2, "K should have two bounds Number & Comparable<K>");
            require(isClass(kBounds[0], Number.class), "K's first bound must be Number");
            require(isComparableOfSelf(kBounds[1], K), "K's second bound must be Comparable<K>");

            // Return type Map<K, List<V>>
            Type rt = m.getGenericReturnType();
            require(isMapOfKeyToListOf(rt, K, mtp[1]), "group() return should be Map<K, List<V>>");

            // Param type Map<K, V>
            Type[] params = m.getGenericParameterTypes();
            require(
                params.length == 1 && isMapOf(params[0], K, mtp[1]),
                "group() param should be Map<K, V>"
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("group() not found", e);
        }

        // <X extends T> List<X> echo(List<X>)
        try {
            Method m = cls.getDeclaredMethod("echo", List.class);
            TypeVariable<Method>[] mtp = m.getTypeParameters();
            require(mtp.length == 1 && mtp[0].getName().equals("X"), "echo() should declare <X>");
            // X extends T
            require(
                mtp[0].getBounds().length == 1 && mtp[0].getBounds()[0] == tp[0],
                "X should extend T"
            );
            // Return List<X>
            require(isListOf(m.getGenericReturnType(), mtp[0]), "echo() should return List<X>");
            // Param List<X>
            Type[] params = m.getGenericParameterTypes();
            require(
                params.length == 1 && isListOf(params[0], mtp[0]),
                "echo() param should be List<X>"
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("echo() not found", e);
        }

        // List<? super T> widen(List<? super T>)
        try {
            Method m = cls.getDeclaredMethod("widen", List.class);
            require(
                isListOfWildcardSuper(m.getGenericReturnType(), tp[0]),
                "widen() return should be List<? super T>"
            );
            Type[] params = m.getGenericParameterTypes();
            require(
                params.length == 1 && isListOfWildcardSuper(params[0], tp[0]),
                "widen() param should be List<? super T>"
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("widen() not found", e);
        }
        System.out.println("All generic reflection checks passed.");
    }

    // --- Helpers -------------------------------------------------------------

    private static void require(boolean cond, String msg) {
        if (!cond) throw new IllegalStateException(msg);
    }

    private static Type getFieldGenericType(String name) {
        try {
            return GenericSelfCheck.class.getDeclaredField(name).getGenericType();
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Missing field: " + name, e);
        }
    }

    private static boolean isClass(Type t, Class<?> c) {
        return t instanceof Class<?> && t == c;
    }

    private static boolean isParameterized(Type t, Class<?> raw, int arity) {
        if (!(t instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType p = (ParameterizedType) t;
        if (!(p.getRawType() instanceof Class<?>)) {
            return false;
        }
        Class<?> rc = (Class<?>) p.getRawType();
        if (rc != raw) {
            return false;
        }

        return p.getActualTypeArguments().length == arity;
    }

    private static ParameterizedType asParam(Type t) {
        return (ParameterizedType) t;
    }

    private static boolean isWildcardExtends(Type t, Type upper) {
        if (!(t instanceof WildcardType)) {
            return false;
        }
        WildcardType w = (WildcardType) t;
        Type[] uppers = w.getUpperBounds();
        Type[] lowers = w.getLowerBounds();
        return lowers.length == 0 && uppers.length == 1 && uppers[0] == upper;
    }

    private static boolean isWildcardSuper(Type t, Type lower) {
        if (!(t instanceof WildcardType)) return false;
        WildcardType w = (WildcardType) t;
        Type[] uppers = w.getUpperBounds();
        Type[] lowers = w.getLowerBounds();
        return uppers.length == 1 && uppers[0] == Object.class && lowers.length == 1 && lowers[0] == lower;
    }

    private static boolean isListOf(Type t, Type arg) {
        if (!isParameterized(t, List.class, 1)) return false;
        return asParam(t).getActualTypeArguments()[0] == arg;
    }

    private static boolean isListOfWildcardSuper(Type t, Type lower) {
        if (!isParameterized(t, List.class, 1)) return false;
        return isWildcardSuper(asParam(t).getActualTypeArguments()[0], lower);
    }

    private static boolean isComparableOfSelf(Type t, Type selfTV) {
        if (!isParameterized(t, Comparable.class, 1)) return false;
        return asParam(t).getActualTypeArguments()[0] == selfTV;
    }

    private static boolean isMapOf(Type t, Type k, Type v) {
        if (!isParameterized(t, Map.class, 2)) return false;
        Type[] args = asParam(t).getActualTypeArguments();
        return args[0] == k && args[1] == v;
    }

    private static boolean isMapOfKeyToListOf(Type t, Type k, Type v) {
        if (!isParameterized(t, Map.class, 2)) return false;
        Type[] args = asParam(t).getActualTypeArguments();
        return args[0] == k && isListOf(args[1], v);
    }

    private static void checkMapStringToListExtendsNumber(Type t) {
        require(isParameterized(t, Map.class, 2), "ratings must be a Map<K,V>");
        Type[] args = asParam(t).getActualTypeArguments();
        require(args[0] == String.class, "ratings key must be String");
        require(isParameterized(args[1], List.class, 1), "ratings value must be List<?>");
        Type valArg = asParam(args[1]).getActualTypeArguments()[0];
        require(isWildcardExtends(valArg, Number.class), "ratings List must be <? extends Number>");
    }

    private static void checkDeepNested(Type t) {
        require(isParameterized(t, List.class, 1), "deep must be List<...>");
        Type inner = asParam(t).getActualTypeArguments()[0];
        require(isParameterized(inner, Map.class, 2), "deep inner must be Map<K,V>");
        Type[] mapArgs = asParam(inner).getActualTypeArguments();
        require(mapArgs[0] == String.class, "deep Map key must be String");
        Type v = mapArgs[1];
        require(isParameterized(v, Set.class, 1), "deep Map value must be Set<...>");
        Type setArg = asParam(v).getActualTypeArguments()[0];
        require(isParameterized(setArg, List.class, 1), "deep Set element must be List<...>");
        Type listArg = asParam(setArg).getActualTypeArguments()[0];
        require(listArg == Integer.class, "deep List element must be Integer");
    }

    private static void checkListSuperInteger(Type t) {
        require(isParameterized(t, List.class, 1), "sink must be List<...>");
        Type arg = asParam(t).getActualTypeArguments()[0];
        require(isWildcardSuper(arg, Integer.class), "sink must be List<? super Integer>");
    }

    private static void checkMapEntryOfStringInteger(Type t) {
        require(isParameterized(t, Entry.class, 2), "pair must be Map.Entry<K,V>");
        ParameterizedType p = asParam(t);
        // Check owner type is Map
        require(p.getOwnerType() == Map.class, "pair owner type must be Map");
        Type[] args = p.getActualTypeArguments();
        require(
            args[0] == String.class && args[1] == Integer.class,
            "pair must be Map.Entry<String, Integer>"
        );
    }

    private static void checkGenericArrayListOfString(Type t) {
        require(t instanceof GenericArrayType, "buckets must be a generic array type");
        GenericArrayType ga = (GenericArrayType) t;
        Type comp = ga.getGenericComponentType(); // List<String>
        require(isParameterized(comp, List.class, 1), "buckets component must be List<String>");
        require(
            asParam(comp).getActualTypeArguments()[0] == String.class,
            "buckets component arg must be String"
        );
    }

    private static void checkOptionalListExtendsT(Type t, Type tVarT) {
        require(isParameterized(t, Optional.class, 1), "maybe must be Optional<...>");
        Type inner = asParam(t).getActualTypeArguments()[0];
        require(isParameterized(inner, List.class, 1), "maybe inner must be List<...>");
        Type arg = asParam(inner).getActualTypeArguments()[0];
        require(isWildcardExtends(arg, tVarT), "maybe inner must be List<? extends T>");
    }
}
