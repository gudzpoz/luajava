package party.iroiro.luajava.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Generates a class
 */
public abstract class SampleExtender {
    private final static byte[] CAFEBABE = {
            /* The CAFEBABE magic */
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE,
            /* Minor version */
            0x00, 0x00,
            /* Major version: Java 8 */
            0x00, 0x34,
            /* Constant pool count */
            0x00, 0x13,
            /* Constant: Utf-8 */ // Constant 1, to be filled by us with the class name
            0x01,
    };

    private final static byte[] SOME_CONTANTS = {
            /* Constant: Class */ // Constant 2
            0x07,
            /* Name index */
            0x00, 0x01,

            /* Constant: Utf-8 */ // Constant 3
            0x01,
            /* Length */
            0x00, 0x10,
            /* "java/lang/Object" */
            0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E,
            0x67, 0x2F, 0x4F, 0x62, 0x6A, 0x65, 0x63, 0x74,

            /* Constant: Class */ // Constant 4
            0x07,
            /* Name index */
            0x00, 0x03,

            /* Constant: Utf-8 */ // Constant 5, to be filled by us with the extended interface
            0x01,
    };

    private final static byte[] CONTENT = {
            /* Constant: Class */ // Constant 6
            0x07,
            /* Name index */
            0x00, 0x05,

            /* Constant: Utf-8 */ // Constant 7
            0x01,
            /* Length */
            0x00, 0x25,
            /* "java/lang/invoke/MethodHandles$Lookup" */
            0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E, 0x67, 0x2F,
            0x69, 0x6E, 0x76, 0x6F, 0x6B, 0x65, 0x2F, 0x4D, 0x65, 0x74,
            0x68, 0x6F, 0x64, 0x48, 0x61, 0x6E, 0x64, 0x6C, 0x65, 0x73,
            0x24, 0x4C, 0x6F, 0x6F, 0x6B, 0x75, 0x70,

            /* Constant: Class */ // Constant 8
            0x07,
            /* Name index */
            0x00, 0x07,

            /* Constant: Utf-8 */ // Constant 9
            0x01,
            /* Length */
            0x00, 0x1E,
            /* "java/lang/invoke/MethodHandles" */
            0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E, 0x67, 0x2F, 0x69, 0x6E, 0x76, 0x6F, 0x6B,
            0x65, 0x2F, 0x4D, 0x65, 0x74, 0x68, 0x6F, 0x64, 0x48, 0x61, 0x6E, 0x64, 0x6C, 0x65, 0x73,

            /* Constant: Class */ // Constant 0x0A
            0x07,
            /* Name index */
            0x00, 0x09,

            /* Constant: Utf-8 */ // Constant 0x0B
            0x01,
            /* Length */
            0x00, 0x06,
            /* "Lookup" */
            0x4C, 0x6F, 0x6F, 0x6B, 0x75, 0x70,

            /* Constant: Utf-8 */ // Constant 0x0C
            0x01,
            /* Length */
            0x00, 0x09,
            /* "getLookup" */
            0x67, 0x65, 0x74, 0x4C, 0x6F, 0x6F, 0x6B, 0x75, 0x70,

            /* Constant: Utf-8 */ // Constant 0x0D
            0x01,
            /* Length */
            0x00, 0x29,
            /* "()Ljava/lang/invoke/MethodHandles$Lookup;" */
            0x28, 0x29, 0x4C, 0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E, 0x67, 0x2F, 0x69,
            0x6E, 0x76, 0x6F, 0x6B, 0x65, 0x2F, 0x4D, 0x65, 0x74, 0x68, 0x6F, 0x64, 0x48, 0x61,
            0x6E, 0x64, 0x6C, 0x65, 0x73, 0x24, 0x4C, 0x6F, 0x6F, 0x6B, 0x75, 0x70, 0x3B,

            /* Constant: Utf-8 */ // Constant 0x0E
            0x01,
            /* Length */
            0x00, 0x06,
            /* "lookup" */
            0x6C, 0x6F, 0x6F, 0x6B, 0x75, 0x70,

            /* Constant: NameAndType */ // Constant 0x0F
            0x0C,
            /* Name index */
            0x00, 0x0E,
            /* Descriptor index */
            0x00, 0x0D,

            /* Constant: Methodref */ // Constant 0x10
            0x0A,
            /* Class index */
            0x00, 0x0A,
            /* NameAndType index */
            0x00, 0x0F,

            /* Constant: Utf-8 */ // Constant 0x11
            0x01,
            /* Length */
            0x00, 0x04,
            /* "Code" */
            0x43, 0x6F, 0x64, 0x65,

            /* Constant: Utf-8 */ // Constant 0x12
            0x01,
            /* Length */
            0x00, 0x0C,
            /* "InnerClasses" */
            0x49, 0x6E, 0x6E, 0x65, 0x72, 0x43, 0x6C, 0x61, 0x73, 0x73, 0x65, 0x73,

            /* Access flags */
            0x06, 0x01,
            /* `this` */
            0x00, 0x02,
            /* `super` */
            0x00, 0x04,
            /* Interfaces count */
            0x00, 0x01,
            /* Interfaces */
            0x00, 0x06,
            /* Fields count */
            0x00, 0x00,
            /* Methods count */
            0x00, 0x01,
            /* Method: Method flags */
            0x00, 0x09,
            /* Method: Name index */
            0x00, 0x0C,
            /* Method: Descriptor index */
            0x00, 0x0D,
            /* Method: Attributes count */
            0x00, 0x01,
            /* Method: Attribute: Attribute name index: Code */
            0x00, 0x11,
            /* Method: Attribute: Attribute length */
            0x00, 0x00, 0x00, 0x10,
            /* Method: Attribute: Code: Max stack */
            0x00, 0x01,
            /* Method: Attribute: Code: Max locals */
            0x00, 0x00,
            /* Method: Attribute: Code: Code length */
            0x00, 0x00, 0x00, 0x04,
            /* Method: Attribute: Code: Code */
            (byte) 0xB8, 0x00, 0x10, (byte) 0xB0, // invokestatic, areturn
            /* Method: Attribute: Code: Exception table length */
            0x00, 0x00,
            /* Method: Attribute: Code: Attribute count */
            0x00, 0x00,
            /* Attribute count */
            0x00, 0x01,
            /* Attribute: Attribute name index: InnerClasses */
            0x00, 0x12,
            /* Attribute: Attribute length */
            0x00, 0x00, 0x00, 0x0A,
            /* Attribute: InnerClasses: Number of classes */
            0x00, 0x01,
            /* Attribute: InnerClasses: Class: Inner class info index */
            0x00, 0x08,
            /* Attribute: InnerClasses: Class: Outer class info index */
            0x00, 0x0A,
            /* Attribute: InnerClasses: Class: Inner name index */
            0x00, 0x0B,
            /* Attribute: InnerClasses: Class: Access flags */
            0x00, 0x19
    };

    /**
     * Generates a sample class extending the specified interface
     *
     * <p>
     * The names should be like {@code com/example/SomeClass$InnerClass}.
     * </p>
     *
     * @param name  the generated class name
     * @param iName the interface name
     * @return a generated class
     * @throws IOException if {@link ByteArrayOutputStream} errs
     */
    public static byte[] generateClass(String name, String iName) throws IOException {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] iNameBytes = iName.getBytes(StandardCharsets.UTF_8);
        if (nameBytes.length > Short.MAX_VALUE || iNameBytes.length > Short.MAX_VALUE) {
            throw new IOException("The class name is too lengthy");
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream(
                CAFEBABE.length
                + 2 + nameBytes.length
                + 2 + iNameBytes.length
        );
        output.write(CAFEBABE);
        output.write((nameBytes.length & 0xFF00) >> 8);
        output.write(nameBytes.length & 0x00FF);
        output.write(nameBytes);
        output.write(SOME_CONTANTS);
        output.write((iNameBytes.length & 0xFF00) >> 8);
        output.write(iNameBytes.length & 0x00FF);
        output.write(iNameBytes);
        output.write(CONTENT);
        return output.toByteArray();
    }
}
