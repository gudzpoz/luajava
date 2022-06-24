package party.iroiro.jua;

import java.io.IOException;

public interface ExternalLoader {
    int load(String path, Lua L) throws IOException;
}
