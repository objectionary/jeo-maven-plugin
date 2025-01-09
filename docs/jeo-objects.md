# Full List of Jeo Objects

During decompilation, the `jeo-maven-plugin` creates a set of objects
representing
bytecode primitives.
These objects provide a way to handle various aspects of Java bytecode.
Below is the full list of these objects, grouped by category:

---

## Bytecode Instructions

- **`jeo.opcode.*`**  
  Represents a single bytecode instruction like `aload_0`, `iconst_0`, etc.

## Classes, Methods, and Fields

- **`jeo.class`**  
  Represents a Java class.
- **`jeo.method`**  
  Represents a Java method.
- **`jeo.field`**  
  Represents a Java field.
- **`jeo.params`**  
  Represents method parameters.
- **`jeo.param`**  
  Represents a single method parameter.
- **`jeo.maxs`**  
  Represents the maximum stack and local variable sizes.

## Primitive Values

- **`jeo.int`** - Represents an integer value.
- **`jeo.bool`** - Represents a boolean value.
- **`jeo.string`** - Represents a string value.
- **`jeo.float`** - Represents a float value.
- **`jeo.double`** - Represents a double value.
- **`jeo.long`** - Represents a long value.
- **`jeo.char`** - Represents a char value.
- **`jeo.short`** - Represents a short value.
- **`jeo.byte`** - Represents a byte value.
- **`jeo.bytes`** - Represents a byte array.

## Collections and Complex Types

- **`jeo.nullable`**  
  Represents an object that can be `null`.
- **`jeo.array`**  
  Represents an array of objects.
- **`jeo.type`**  
  Represents a Java type.
- **`jeo.seq.*`**  
  Represents a sequence of objects with a specific size,
  like `jeo.seq.of0`, `jeo.seq.of1`, etc.

## Annotations and Metadata

- **`jeo.annotation`**  
  Represents a Java annotation.
- **`jeo.annotation-property`**  
  Represents a single annotation element.
- **`jeo.annotation-default-value`**  
  Represents a default value of a Java interface method or annotation property.
- **`jeo.inner-class`**  
  Represents a Java inner class annotation property.

## Local Variables and Control Flow

- **`jeo.local-variable`**  
  Represents a local variable entry.
- **`jeo.try-catch`**  
  Represents a try-catch block.

## Labels, Handles, Frames

- **`jeo.label`**  
  Represents a Java label.
- **`jeo.handle`**  
  Represents a Java method handle.
- **`jeo.frame`**  
  Represents a stack frame.

---