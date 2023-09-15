package org.eolang.jeo;

import com.jcabi.log.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Converts EO to bytecode.
 * The mojo that converts EO to bytecode only.
 * It does not apply any improvements.
 *
 * @since 0.1.0
 */
@Mojo(name = "eo-to-bytecode", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class EoToBytecodeMojo extends AbstractMojo {

    /**
     * Project compiled classes.
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File classes;

    /**
     * Project default target directory.
     * @since 0.1.0
     */
    @Parameter(defaultValue = "${project.build.directory}")
    private File target;

    @Override
    public void execute() {
        new EoObjects(this.target.toPath())
            .objects()
            .forEach(this::recompile);
    }

    /**
     * Recompile the Intermediate Representation.
     * @param representation Intermediate Representation to recompile.
     */
    private void recompile(final Representation representation) {
        final String name = representation.name();
        try {
            final byte[] bytecode = representation.toBytecode();
            final String[] subpath = name.split("\\.");
            subpath[subpath.length - 1] = String.format("%s.class", subpath[subpath.length - 1]);
            final Path path = Paths.get(this.classes.toString(), subpath);
            Logger.info(
                this,
                "Recompiling '%s', bytecode instance '%s', bytes to save '%s'",
                path,
                representation.getClass(),
                bytecode.length
            );
            Files.createDirectories(path.getParent());
            Files.write(path, bytecode);
            Logger.info(
                this,
                "%s was recompiled successfully.",
                path.getFileName().toString()
            );
        } catch (final IOException exception) {
            throw new IllegalStateException(String.format("Can't recompile '%s'", name), exception);
        }
    }
}
