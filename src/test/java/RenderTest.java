import com.jayway.jsonpath.JsonPath;
import com.mitchellbosecke.pebble.PebbleEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shito.util.DelegatingMap;
import shito.util.JsonPathMap;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class RenderTest {
    private static final PebbleEngine TEMPLATE_ENGINE = new PebbleEngine.Builder()
            .build();
    @Test
    public void onTest(){
        var t = TEMPLATE_ENGINE.getLiteralTemplate("A new repository has been added.\n" +
                "Name: {{ repository.full_name }}\n" +
                "Url: {{ repository.html_url }} \n" +
                "Language: {{ repository.language }}");
        var excepted = "A new repository has been added.\n" +
                "Name: iceBear67/ShitoUrl: https://github.com/iceBear67/Shito \n" +
                "Language: Java";
        try(var sw = new StringWriter()){
            t.evaluate(sw,new DelegatingMap(new JsonPathMap(JsonPath.parse(RenderTest.class.getClassLoader().getResourceAsStream("test.json"))),new HashMap<>()));
            Assertions.assertEquals(excepted,sw.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
