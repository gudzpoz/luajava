package party.iroiro.luajava;

import java.io.*;
import java.nio.ByteBuffer;

public class ResourceLoader {
    public int load(String path, Lua L) throws IOException {
        try (InputStream stream = ResourceLoader.class.getResourceAsStream(path)) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (stream != null) {
                byte[] bytes = new byte[512];
                while (stream.available() != 0) {
                    int len = stream.read(bytes);
                    output.write(bytes, 0, len);
                }
                ByteBuffer buffer = ByteBuffer.allocateDirect(output.size());
                buffer.put(output.toByteArray());
                L.load(buffer, path);
                return 0;
            } else {
                return 1;
            }
        }
    }
}
