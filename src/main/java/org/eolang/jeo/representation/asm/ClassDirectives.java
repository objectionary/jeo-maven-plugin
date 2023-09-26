package org.eolang.jeo.representation.asm;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Class printer.
 * ASM class visitor which scans the class and builds Xembly directives.
 * You can read more about Xembly right here:
 * - https://github.com/yegor256/xembly
 * - https://www.xembly.org
 * Firther all this directives will be used to build XML representation of the class.
 * @since 0.1
 * @todo #84:30min Handle constructors in classes.
 *  Right now we just skip constructors. We should handle them in order to
 *  build correct XML representation of the class. When the method is ready
 *  remove that puzzle.
 */
public class ClassDirectives extends ClassVisitor implements Iterable<Directive> {

    /**
     * Bytecode listing.
     */
    private final String listing;

    /**
     * Xembly directives.
     */
    private final Directives directives;

    /**
     * Constructor.
     */
    public ClassDirectives(final String listing) {
        this(Opcodes.ASM9, new Directives(), listing);
    }

    /**
     * Constructor.
     * @param api ASM API version.
     * @param directives Xembly directives.
     */
    private ClassDirectives(
        final int api,
        final Directives directives,
        final String listing
    ) {
        super(api);
        this.directives = directives;
        this.listing = listing;
    }

    @Override
    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String supername,
        final String[] interfaces
    ) {
        final String now = ZonedDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT);
        this.directives.add("program")
            .attr("name", name.replace('/', '.'))
            .attr("version", "0.0.0")
            .attr("revision", "0.0.0")
            .attr("dob", now)
            .attr("time", now)
            .add("listing")
            .set(this.listing)
            .up()
            .add("errors").up()
            .add("sheets").up()
            .add("license").up()
            .add("metas").up()
            .attr("ms", System.currentTimeMillis())
            .add("objects");
        this.directives.add("o")
            .attr("abstract", "")
            .attr("name", String.format("class__%s", name));
        super.visit(version, access, name, signature, supername, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String descriptor,
        final String signature,
        final String[] exceptions
    ) {
        final MethodVisitor result;
        if (name.equals("<init>")) {
            result = super.visitMethod(access, name, descriptor, signature, exceptions);
        } else {
            this.directives.add("o")
                .attr("abstract", "")
                .attr("name", name);
            if (Type.getMethodType(descriptor).getArgumentTypes().length > 0) {
                this.directives.add("o")
                    .attr("name", "args")
                    .up();
            }
            this.directives.add("o")
                .attr("base", "seq")
                .attr("name", "@");
            result = new MethodDirectives(
                this.directives,
                super.visitMethod(access, name, descriptor, signature, exceptions)
            );
        }
        return result;
    }

    @Override
    public void visitEnd() {
        this.directives.up();
        super.visitEnd();
    }

    @Override
    public Iterator<Directive> iterator() {
        return this.directives.iterator();
    }
}
