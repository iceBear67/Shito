package shito.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import shito.api.IPresetManager;
import shito.api.data.ShitoPreset;
import shito.api.data.ShitoRoute;
import shito.api.data.ShitoTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class FilePresetManager implements IPresetManager {
    private static final Gson GSON = new Gson();
    private final Path dataDir;
    private Map<String, ShitoPreset> cachedPresets = new ConcurrentHashMap<>();
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public FilePresetManager(Path dataDir) {
        this.dataDir = dataDir;

        // generate index.
        //log.info("Preloading");
        try ( var s = Files.walk(dataDir,2)){
                    s.map(Path::getFileName)
                    .map(Path::toString)
                    .filter(e -> e.startsWith("preset.") && e.endsWith(".json"))
                    .map(e -> getPresetById(e.replaceFirst("preset\\.", "").replaceFirst("\\.json", "")));
        }
    }
    @Override
    public Collection<? extends ShitoPreset> listPresets() {
        return cachedPresets.values();
    }

    @Override
    @SneakyThrows
    public void savePreset(ShitoPreset preset) {
        cachedPresets.put(preset.getId(), preset);
        Files.writeString(dataDir.resolve("preset." + preset.getId() + ".json"), GSON.toJson(preset), StandardCharsets.UTF_8);
    }

    @Override
    public void removePreset(ShitoPreset preset) {
        cachedPresets.remove(preset.getId());
        var data = dataDir.resolve("preset." + preset.getId() + ".json").toFile();
        data.delete();
    }

    @SneakyThrows
    @Override
    public ShitoPreset getPresetById(String id) {
        if (cachedPresets.containsKey(id)) {
            return cachedPresets.get(id);
        }
        var data = dataDir.resolve("preset." + id + ".json").toFile();
        if (!data.exists()) {
            return null;
        }
        var res = GSON.fromJson(Files.readString(data.toPath()), ShitoPreset.class);
        if(!res.getId().equals(id)){
            throw new IllegalStateException("Id in file not equal to param");
        }
        cachedPresets.put(id, res);
        return res;
    }

    @Override
    public boolean hasPreset(String id) {
        return cachedPresets.containsKey(id);
    }

    @Override
    public void savePresets() {
        cachedPresets.values().forEach(this::savePreset);
    }
}
