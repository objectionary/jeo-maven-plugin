package org.eolang.jeo;

import java.nio.file.Path;

public final class Assembler {

    private final Path input;
    private final Path output;

    private final boolean verify;


    public Assembler(final Path input, final Path output, final boolean verify) {
        this.input = input;
        this.output = output;
        this.verify = verify;
    }

    void assemble() {
        new LoggedTranslator(
            "Assembling",
            "assembled",
            this.input,
            this.output,
            new RepresentationsTranslator(
                new TranslationLog(
                    "Assembling",
                    "assembled",
                    new Assemble(this.output)
                )
            )
        ).apply(
            new XmirRepresentations(this.input, this.verify).all()
        );
    }
}
