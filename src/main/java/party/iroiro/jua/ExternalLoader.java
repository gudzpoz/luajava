package party.iroiro.jua;

import java.io.IOException;

public interface ExternalLoader {
    int load(String path, Jua L) throws IOException;
}
