package shito.manager;

import cc.sfclub.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.core.file.OpenOptions;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import shito.api.ITemplateManager;
import shito.api.data.ShitoRoute;
import shito.api.data.ShitoTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@Slf4j
public class FileTemplateManager implements ITemplateManager {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ShitoRoute.class, new ShitoRoute.ShitoRouteSerializer())
            .create();

    private final Path dataDir;
    private Map<String, Set<String>> index;
    private Map<String, ShitoTemplate> cachedTemplates = new ConcurrentHashMap<>();

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public FileTemplateManager(Path dataDir) {
        this.dataDir = dataDir;

        // generate index.
        //log.info("Preloading");
        try ( var s = Files.walk(dataDir,2)){
            index = s
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(e -> e.startsWith("template.") && e.endsWith(".json"))
                    .map(e -> getTemplate(e.replaceFirst("template\\.", "").replaceFirst("\\.json", "")))
                    .collect(
                            Collectors.groupingBy(ShitoTemplate::asUUIDString, mapping(ShitoTemplate::getId, toSet()))
                    );
        }
    }

    @Override
    @SneakyThrows
    public ShitoTemplate getTemplate(String id) {
        if (cachedTemplates.containsKey(id)) {
            return cachedTemplates.get(id);
        }
        var data = dataDir.resolve("template." + id + ".json").toFile();
        if (!data.exists()) {
            return null;
        }
        var res = GSON.fromJson(Files.readString(data.toPath()), ShitoTemplate.class);
        cachedTemplates.put(id, res);
        return res;
    }

    @Override
    public List<ShitoTemplate> templatesFromUser(User user) {
        return index.getOrDefault(user.getUniqueID(), Collections.emptySet()).stream().map(this::getTemplate).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public boolean hasTemplate(String id) {
        return cachedTemplates.containsKey(id);
    }

    @Override
    public void removeTemplate(@NonNull ShitoTemplate template) {
        index.get(template.getUser().toString()).remove(template.getId());
        dataDir.resolve("template." + template.getId() + ".json").toFile().delete();
        cachedTemplates.remove(template.getId());
    }

    @Override
    @SneakyThrows
    public void saveTemplate(@NonNull ShitoTemplate template) {
        cachedTemplates.put(template.getId(), template);
        Files.writeString(dataDir.resolve("template." + template.getId() + ".json"), GSON.toJson(template), StandardCharsets.UTF_8);
    }

    @Override
    @SneakyThrows
    public void saveAllTemplate() {
        cachedTemplates.values().forEach(this::saveTemplate);
    }

    @Override
    public Collection<? extends ShitoTemplate> getTemplates() {
        return cachedTemplates.values();
    }
}
