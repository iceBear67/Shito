package shito.handler;

import cc.sfclub.events.Event;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shito.api.ITemplateManager;
import shito.api.data.ShitoMessage;
import shito.util.JsonPathMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class PushHandler{
    private final ITemplateManager manager;
    // /shito/:id
    // body: message
    public void handlePost(RoutingContext event) {
        var id = event.pathParam("templateId");
        var body = event.getBody().toString(StandardCharsets.UTF_8);
        var token = event.pathParam("token");
        if(id == null || body == null || token == null){
            event.response().setStatusCode(400);
            event.response().end();
            return;
        }
        var c = event.request().getHeader("Content-Type");
        event.response().setStatusCode(handle(id,body,token,c != null && c.contains("application/json")));
        event.response().end();
    }
    // /shito/:id/:base64_message
    public void handleOther(RoutingContext event) {
        var id = event.pathParam("templateId");
        var data = event.pathParam("data");
        var token = event.pathParam("token");
        if(id == null || data == null || token == null){
            event.response().setStatusCode(400);
            event.response().end();
            return;
        }
        var s= new String(Base64.getUrlDecoder().decode(data));
        var c = event.request().getHeader("Content-Type");
        event.response().setStatusCode(handle(id, s,token, c != null && c.contains("application/json")));
        event.response().end();
    }

    private int handle(String templateId, String data,String token,boolean json){
        //System.out.println(data);
        var template = manager.getTemplate(templateId);
        if(template == null){
            return 404;
        }
        // validation
        if(!template.validate(token)){
            return 403;
        }
        // try parse.
        Map<String,Object> context;
        if(json){
          context = new JsonPathMap(JsonPath.parse(data));
        }else{
            context = new HashMap<>();
            context.put("data",data);
        }
        Event.postEvent(ShitoMessage.of(template,context));
        return 200;
    }
}
