package shito;

import lombok.Getter;
import shito.api.IPresetManager;
import shito.api.ITemplateManager;
import shito.manager.FilePresetManager;
import shito.manager.FileTemplateManager;
import shito.session.ChatSession;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Shito {
    private final Path dataDir;
    @Getter
    private ITemplateManager templateManager;
    @Getter
    private IPresetManager presetManager;
    List<ChatSession> sessions = new ArrayList<>();

    public Shito(Path dataDir) {
        this.dataDir = dataDir;
        templateManager = new FileTemplateManager(dataDir);
        presetManager = new FilePresetManager(dataDir);
    }

    public void addSession(ChatSession session) {
        sessions.add(session);
    }
}
