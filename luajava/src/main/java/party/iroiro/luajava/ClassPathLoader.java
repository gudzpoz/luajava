/*
 * Copyright (C) 2022 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
        try (InputStream resource = Objects.requireNonNull(
                // We use the class loader to load resources support loading from other Java modules.
                getClass().getClassLoader().getResourceAsStream(getPath(module))
        )) {
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
        return module.replace('.', '/') + ".lua";
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
