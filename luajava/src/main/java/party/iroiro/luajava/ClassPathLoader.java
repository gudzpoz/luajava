package party.iroiro.luajava;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;

public class ClassPathLoader implements ExternalLoader {
    @Override
    public @Nullable Buffer load(String module, Lua ignored) {
        try (InputStream resource =
                     Objects.requireNonNull(getClass().getResourceAsStream(getPath(module)))) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] bytes = new byte[4096];
            int i;
            do {
                i = resource.read(bytes);
                if (i != -1) {
                    output.write(bytes, 0, i);
                }
            } while (i != -1);
            ByteBuffer buffer = ByteBuffer.allocateDirect(output.size());
            output.writeTo(new BufferOutputStream(buffer));
            buffer.flip();
            return buffer;
        } catch (Exception e) {
            return null;
        }
    }

    protected String getPath(String module) {
        return "/" + module.replace('.', '/') + ".lua";
    }

    public static class BufferOutputStream extends OutputStream {
        private final ByteBuffer buffer;

        public BufferOutputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int i) {
            buffer.put((byte) i);
        }

        @Override
        public void write(byte @NotNull [] bytes, int off, int len) {
            buffer.put(bytes, off, len);
        }
    }
}
