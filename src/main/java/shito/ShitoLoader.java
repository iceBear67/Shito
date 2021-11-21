package shito;

import cc.sfclub.command.Node;
import cc.sfclub.events.Event;
import cc.sfclub.events.server.ServerStartedEvent;
import cc.sfclub.plugin.Plugin;
import cc.sfclub.service.ServiceProvider;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.greenrobot.eventbus.Subscribe;
import shito.handler.CommandHandler;
import shito.handler.MessageBroadcaster;
import shito.handler.PushHandler;
import shito.handler.SessionHandler;

public class ShitoLoader extends Plugin {
    private Shito shito;
    private PushHandler handler;
    public static Shito getShito(){
        return ShitoLoader.get(ShitoLoader.class).shito;
    }
    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
    }
    @Subscribe
    public void onServerStarted(ServerStartedEvent event){
        shito = new Shito(getDataFolder().toPath());
        handler = new PushHandler(shito.getTemplateManager());
        Vertx vertx = ServiceProvider.get(Vertx.class);
        var mainRouter = ServiceProvider.get(Router.class);
        mainRouter.route().handler(BodyHandler.create(false));
        mainRouter.post("/shito/api/v1/push/:templateId/:token").handler(handler::handlePost);
        mainRouter.route("/shito/api/v1/push/:templateId/:token/:data").handler(handler::handleOther);

        // register events
        Event.registerListeners(new MessageBroadcaster(),new SessionHandler(shito.sessions)); // inject reference

        var handler = new CommandHandler(shito);
        registerCommand(
                Node.literal("shito")
                        .executes(handler::handleHelp)
                        .then(
                                Node.literal("route")
                                        .then(
                                                Node.argument("templateId",Node.string())
                                                        .executes(handler::handleRoute)
                                        )
                        )
                        .then(
                                Node.literal("create")
                                        .then(
                                                Node.argument("templateId",Node.string())
                                                        .executes(handler::handleCreate)
                                        )
                        )
                        .then(
                                Node.literal("status")
                                        .then(
                                                Node.argument("templateId",Node.string())
                                                        .executes(handler::handleTemplateStatus)
                                        )
                                        .executes(handler::handleUserStatus)
                        )
                        .then(
                                Node.literal("edit")
                                        .then(
                                                Node.argument("templateId", Node.string())
                                                        .executes(handler::handleEdit)
                                        )
                        )
                        .then(
                                Node.literal("delroute")
                                        .then(
                                                Node.argument("templateId",Node.string())
                                                        .then(
                                                                Node.argument("id",Node.integerArg())
                                                                        .executes(handler::handleDelRoute)
                                                        )
                                        )

                        )
                        .then(
                                Node.literal("enable")
                                        .then(
                                                Node.argument("templateId", Node.string())
                                                        .executes(handler::handleEnable)
                                        )
                        )
                        .then(
                                Node.literal("disable")
                                        .then(
                                                Node.argument("templateId", Node.string())
                                                        .executes(handler::handleDisable)
                                        )
                        )
                        .then(
                                Node.literal("remove")
                                        .then(
                                                Node.argument("templateId",Node.string())
                                                        .executes(handler::handleRemove)
                                        )
                        )
                        .then(
                                Node.literal("all")
                                        .requires( user -> user.getSender().hasPermission("shito.listall"))
                                        .executes(handler::handleListTemplates)
                        )
        );

    }

    @Override
    public void onDisable() {
        shito.getTemplateManager().saveAllTemplate();
    }
}
