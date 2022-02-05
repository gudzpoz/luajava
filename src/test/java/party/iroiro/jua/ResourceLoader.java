package party.iroiro.jua;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceLoader implements ExternalLoader {
    @Override
    public int load(String path, Lua L) {
        InputStreamReader r = new InputStreamReader(Objects.requireNonNull(
                ResourceLoader.class.getResourceAsStream(path)));
        BufferedReader b = new BufferedReader(r);
        String collect = b.lines().collect(Collectors.joining("\n"));
        L.load(collect);
        return 0;
    }
}
