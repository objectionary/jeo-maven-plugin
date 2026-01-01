/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.jeo.modules;

import java.lang.module.ModuleDescriptor;

public class App {
    public static void main(String[] args) {
        Module module = App.class.getModule();
        System.out.println("Module: " + module);

        // Ensure module has a name
        if (module.getName() == null) {
            throw new IllegalStateException("Module name is missing!");
        }

        // Ensure module has a descriptor
        ModuleDescriptor descriptor = module.getDescriptor();
        if (descriptor == null) {
            throw new IllegalStateException("Module descriptor is missing!");
        }

        // Ensure this package is part of the module
        String pkg = App.class.getPackageName();
        if (!descriptor.packages().contains(pkg)) {
            throw new IllegalStateException("Package " + pkg + " not in module descriptor!");
        }

        System.out.println("Module name: " + module.getName());
        System.out.println("Packages: " + descriptor.packages());
        System.out.println("Everything looks fine!");
    }
}
