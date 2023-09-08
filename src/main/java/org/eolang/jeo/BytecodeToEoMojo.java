package org.eolang.jeo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Converts bytecode to EO.
 * The mojo that converts bytecode to EO only.
 * It does not apply any improvements. It does not convert EO to bytecode back.
 *
 * @since 0.1.0
 */
@Mojo(name = "bytecode-to-eo", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class BytecodeToEoMojo extends AbstractMojo {
    @Override
    public void execute() {

    }
}
