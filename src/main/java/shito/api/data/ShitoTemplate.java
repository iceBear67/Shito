package shito.api.data;

import cc.sfclub.user.User;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ShitoTemplate {
    private static final PebbleEngine TEMPLATE_ENGINE = new PebbleEngine.Builder().build();

    private final String id;
    private final UUID user;
    @Setter
    private String context;
    @Setter
    private boolean enabled;
    private final String token;
    private final List<ShitoRoute> messageRouting = new ArrayList<>();

    // for stream collecting
    public String asUUIDString(){
        return user.toString();
    }

    public boolean validate(String sign){
        return enabled && token.equals(sign); // todo v2
    }
    public boolean isAuthorized(User user){
        return user.getUniqueID().equals(this.user.toString());
    }
    @SneakyThrows
    public String render(Map<String,Object> data){
        PebbleTemplate compiledTemplate = TEMPLATE_ENGINE.getLiteralTemplate(context);
        try (StringWriter wr = new StringWriter()) {
            compiledTemplate.evaluate(wr, data);
            return wr.toString();
        }
    }
}
