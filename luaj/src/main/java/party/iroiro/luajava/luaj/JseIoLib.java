package party.iroiro.luajava.luaj;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.lib.IoLib;

import java.io.*;
import java.nio.file.*;
import java.util.Objects;

/**
 * Copied from LuaJ libraries.
 */
public class JseIoLib extends IoLib {
    @Override
    protected File wrapStdin() {
        return new FileImpl(globals.STDIN, true);
    }

    @Override
    protected File wrapStdout() {
        return new FileImpl(globals.STDOUT, true);
    }

    @Override
    protected File wrapStderr() {
        return new FileImpl(globals.STDERR, true);
    }

    @Override
    protected File openFile(String filename, boolean readMode, boolean appendMode,
                            boolean updateMode, boolean binaryMode) throws IOException {
        Path path = Paths.get(filename);
        if (readMode) {
            return new FileImpl(Files.newInputStream(path), false);
        }
        OutputStream outputStream = Files.newOutputStream(path,
                StandardOpenOption.WRITE,
                appendMode ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        return new FileImpl(outputStream, false);
    }

    @Override
    protected File tmpFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected File openProgram(String prog, String mode) {
        throw new UnsupportedOperationException();
    }

    protected final class FileImpl extends File {
        private final InputStream in;
        private final OutputStream out;
        private final boolean std;
        private boolean closed = false;
        private int peeked = -1;

        public FileImpl(InputStream in, boolean std) {
            this.in = in;
            out = null;
            this.std = std;
        }

        public FileImpl(OutputStream out, boolean std) {
            in = null;
            this.out = out;
            this.std = std;
        }

        @Override
        public void write(LuaString string) throws IOException {
            Objects.requireNonNull(out).write(string.m_bytes);
        }

        @Override
        public void flush() throws IOException {
            Objects.requireNonNull(out).flush();
        }

        @Override
        public boolean isstdfile() {
            return std;
        }

        @Override
        public void close() throws IOException {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            closed = true;
        }

        @Override
        public boolean isclosed() {
            return closed;
        }

        @Override
        public int seek(String option, int bytecount) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setvbuf(String mode, int size) {
            // empty
        }

        @Override
        public int remaining() {
            return -1;
        }

        @Override
        public int peek() throws IOException {
            if (peeked < 0) {
                peeked = Objects.requireNonNull(in).read();
            }
            return peeked;
        }

        @Override
        public int read() throws IOException {
            if (peeked >= 0) {
                int next = peeked;
                peeked = -1;
                return next;
            }
            return Objects.requireNonNull(in).read();
        }

        @Override
        public int read(byte[] bytes, int offset, int length) {
            throw new UnsupportedOperationException();
        }
    }
}
