package shito;

import lombok.Getter;
import shito.api.ITemplateManager;
import shito.manager.FileTemplateManager;
import shito.session.ChatSession;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Shito {
    private final Path dataDir;
    @Getter
    private ITemplateManager templateManager;
    List<ChatSession> sessions = new ArrayList<>();

    public Shito(Path dataDir) {
        this.dataDir = dataDir;
        templateManager=new FileTemplateManager(dataDir);
    }

    public void addSession(ChatSession session){
        sessions.add(session);
    }
}
