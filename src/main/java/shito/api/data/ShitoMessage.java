package shito.api.data;

import cc.sfclub.events.Event;
import com.jayway.jsonpath.JsonPath;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import shito.util.DelegatingMap;
import shito.util.JsonPathMap;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ShitoMessage extends Event {
    private final ShitoTemplate template;
    private final Map<String,Object> context;

    public static final ShitoMessage of(ShitoTemplate template,Map<String,Object> context){
        return new ShitoMessage(template,context);
    }

    public String render(Map<String,Object> addition){ // todo should we cache.
        var delegated = new DelegatingMap(context,addition);
        //var delegated = context;
       // System.out.println(context.get("repository.full_name"));
        return template.render(delegated);
    }
}
