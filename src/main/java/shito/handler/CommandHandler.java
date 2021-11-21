package shito.handler;

import cc.sfclub.command.Source;
import cc.sfclub.events.message.direct.PrivateMessage;
import cc.sfclub.events.message.group.GroupMessage;
import cc.sfclub.transform.Contact;
import com.mojang.brigadier.context.CommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shito.Shito;
import shito.ShitoLoader;
import shito.api.data.ShitoRoute;
import shito.api.data.ShitoTemplate;
import shito.routing.GroupShitoRouting;
import shito.routing.PrivateShitoRouting;
import shito.session.SessionCreate;
import shito.session.SessionEdit;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class CommandHandler {
    private final Shito shito;

    public int handleRoute(CommandContext<Source> source) {
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }
        // create router
        if (source.getSource().getMessageEvent() instanceof GroupMessage) {
            // group router
            GroupMessage gm = (GroupMessage) source.getSource().getMessageEvent();
            GroupShitoRouting routing = new GroupShitoRouting(gm.getGroupId(), gm.getTransform());
            template.getMessageRouting().add(routing);
        } else if (source.getSource().getMessageEvent() instanceof PrivateMessage) {
            PrivateMessage pm = (PrivateMessage) source.getSource().getMessageEvent();
            PrivateShitoRouting routing = new PrivateShitoRouting(pm.getTransform(), pm.getContact().getID());
            template.getMessageRouting().add(routing);
        } else {
            src.reply("Unknown message source. Report it to admin with detailed steps");
            return 0;
        }
        src.reply("Added!");
        return 0;
    }

    public int handleCreate(CommandContext<Source> source) {
        Source src = source.getSource();
        String tid = source.getArgument("templateId", String.class);
        if (shito.getTemplateManager().hasTemplate(tid)) {
            src.reply("This name is already claimed.");
            return 0;
        }
        String token = UUID.randomUUID().toString();
        Contact contact = src.getSender().asContact();
        contact.sendMessage("Now, type your template context.");
        shito.addSession(new SessionCreate(src.getSender(), templateContext -> {
            if (shito.getTemplateManager().hasTemplate(tid)) { // already claimed, drop
                // contact.sendMessage("Previous failed session was detected and skipped.");
                log.warn("Previous failed session was detected and skipped for contact " + contact.getUsername());
                return;
            }
            ShitoTemplate template = new ShitoTemplate(tid, UUID.fromString(src.getSender().uniqueID), templateContext, true, token);
            contact.sendMessage("Saved! Your token is [" + token + "], it won't show again!");
            contact.sendMessage("POST: https://bot.sfclub.cc/shito/api/v1/push/" + tid + "/" + token);
            contact.sendMessage("GET/OTHERS: https://bot.sfclub.cc/shito/api/v1/push/" + tid + "/" + token + "/:data(urlsafe_base64 encoded)");
            shito.getTemplateManager().saveTemplate(template);
        }));
        src.reply("Check your private message. Remember adding polar as your contact before if you haven't");
        return 0;
    }

    public int handleTemplateStatus(CommandContext<Source> source) {
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }
        src.reply("Context dump: \n\n" + template.getContext());
        StringBuilder status = new StringBuilder();
        status.append("State: " + (template.isEnabled() ? "ENABLED" : "DISABLED")).append('\n');
        status.append("Routers: \n");
        for (ShitoRoute shitoRoute : template.getMessageRouting()) {
            status.append(" - ").append(shitoRoute).append("\n");
        }
        src.reply(status.toString());
        return 0;
    }

    /*
        User status
     */
    public int handleUserStatus(CommandContext<Source> source) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your Templates: \n");
        for (ShitoTemplate template : shito.getTemplateManager().templatesFromUser(source.getSource().getSender())) {
            sb.append(" - ").append(template.getId()).append(" ( ").append(template.isEnabled() ? "ENABLED" : "DISABLED").append(" )\n");
        }
        source.getSource().reply(sb.toString());
        return 0;
    }

    public int handleEdit(CommandContext<Source> source) {
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }

        // edit
        Contact contact = src.getSender().asContact();
        contact.sendMessage("Now, type your template context.");
        shito.addSession(new SessionEdit(src.getSender(), templateContext -> {
            if (!shito.getTemplateManager().hasTemplate(tid)) { // already claimed, drop
                // contact.sendMessage("Previous failed session was detected and skipped.");
                log.warn("EDITING: Previous failed session was detected and skipped for contact " + contact.getUsername());
                return;
            }
            template.setContext(templateContext);
            shito.getTemplateManager().saveTemplate(template);
            contact.sendMessage("Saved.");
        }));
        src.reply("Check your private message. Remember adding polar as your contact before if you haven't");
        return 0;
    }

    public int handleDelRoute(CommandContext<Source> source) {
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }

        int ordinary = source.getArgument("id", int.class);
        if (ordinary > template.getMessageRouting().size()) {
            src.reply(tid + ": Illegal ordinary out of size.");
            return 0;
        }
        template.getMessageRouting().remove(ordinary);
        src.reply("Removed.");
        return 0;
    }

    public int handleEnable(CommandContext<Source> source) {
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }
        template.setEnabled(true);
        src.reply(tid + " now enabled.");
        return 0;
    }

    public int handleDisable(CommandContext<Source> source) {
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }
        template.setEnabled(false);
        src.reply(tid + " now disabled.");
        return 0;
    }
    public int handleRemove(CommandContext<Source> source){
        String tid = source.getArgument("templateId", String.class);
        if (!shito.getTemplateManager().hasTemplate(tid)) {
            source.getSource().reply("Template not found.");
            return 0;
        }
        Source src = source.getSource();
        ShitoTemplate template = shito.getTemplateManager().getTemplate(tid);
        // check permissions
        if (!template.isAuthorized(src.getSender())) {
            src.reply(tid + ": Not authorized.");
            return 0;
        }
        shito.getTemplateManager().removeTemplate(template);
        src.reply("Removed.");
        return 0;
    }
    private static final String[] HELP_MESSAGE = new String[]{
            "Usage:",
            "You have to add me as a contact first.",
            "Commands:",
            "!p shito -- full help",
            "!p shito route <templateId> -- add a new push destination for here",
            "!p shito create <templateId> -- create a new template",
            "!p shito status [templateId] -- list of",
            "!p shito edit <templateId> -- edit template",
            "!p shito delroute <templateId> <routeId> -- remove route",
            "!p shito enable <templateId> -- enable",
            "!p shito disable <templateId> -- disable",
            "!p shito remove <templateId> -- delete"
    };
    private static final String HELP_MSG = Stream.of(HELP_MESSAGE).collect(Collectors.joining("\n"));

    public int handleHelp(CommandContext<Source> context) {
        context.getSource().reply(HELP_MSG);
        return 0;
    }
}
