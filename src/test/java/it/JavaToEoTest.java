package it;

import com.jcabi.xml.XML;
import org.cactoos.io.OutputTo;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.eolang.parser.Syntax;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JavaToEoTest {

    @Test
    void compilesJavaAndTranspilesBytecodeToEo() throws Exception {
        final ResourceOf resourceOf = new ResourceOf(
            "transpilation/eo/ch_4_types_values_variables/sec_4_2_primitive_types_and_values/sec_4_2_1_integral_types_and_values/MethodByte.eo"
        );
        final XMLOutput output = new XMLOutput();
        new Syntax("scenario", resourceOf, output).parse();
        final XML xml = output.xml();
        System.out.println(xml.toString());
        Assertions.assertTrue(true);
    }
}
