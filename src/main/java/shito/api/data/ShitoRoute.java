package shito.api.data;

import cc.sfclub.transform.ChatGroup;
import cc.sfclub.transform.Receiver;
import com.google.gson.*;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * Subclasses must implement toString()
 */
public interface ShitoRoute {
    void broadcast(Consumer<? super Receiver> groupConsumer);
    void init();

    class ShitoRouteSerializer implements JsonDeserializer<ShitoRoute>, JsonSerializer<ShitoRoute> {
        private static final Gson SELF_HOLD_GSON = new Gson();
        @Override
        @SneakyThrows
        @SuppressWarnings("unchecked")
        public ShitoRoute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(!json.getAsJsonObject().has("impl")){
                throw new JsonParseException("Invalid Routing Object");
            }
            JsonObject obj = json.getAsJsonObject();
            String clazzName = obj.get("impl").getAsString();
            Class<? extends ShitoRoute> routeImpl = (Class<? extends ShitoRoute>) Class.forName(clazzName);
            var data = SELF_HOLD_GSON.fromJson(obj.get("data"),routeImpl);
            data.init();
            return data;
        }

        @Override
        public JsonElement serialize(ShitoRoute src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj  = new JsonObject();
            obj.addProperty("impl",src.getClass().getName());
            obj.add("data",SELF_HOLD_GSON.toJsonTree(src));
            return obj;
        }
    }
}
