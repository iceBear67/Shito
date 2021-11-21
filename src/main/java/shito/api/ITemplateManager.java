package shito.api;

import cc.sfclub.user.User;
import shito.api.data.ShitoTemplate;

import java.util.Collection;
import java.util.List;

public interface ITemplateManager {
    ShitoTemplate getTemplate(String id);
    List<ShitoTemplate> templatesFromUser(User user);
    void removeTemplate(ShitoTemplate template);
    default boolean hasTemplate(String id){
        return getTemplate(id)!=null;
    }
    void saveTemplate(ShitoTemplate template);
    void saveAllTemplate();
    Collection<? extends ShitoTemplate> getTemplates();
}
