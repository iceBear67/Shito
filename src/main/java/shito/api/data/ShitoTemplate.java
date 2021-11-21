package shito.api.data;

import cc.sfclub.user.User;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
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
    public String render(Map<String,Object> data){
        try (StringWriter wr = new StringWriter()) {
            PebbleTemplate compiledTemplate = TEMPLATE_ENGINE.getLiteralTemplate(context); //todo cache
            compiledTemplate.evaluate(wr, data);
            return wr.toString();
        }catch(ParserException excepted){
            return excepted.getLocalizedMessage();
        } catch(Throwable exception){
            exception.printStackTrace();
            return exception.getLocalizedMessage();
        }
    }
}
