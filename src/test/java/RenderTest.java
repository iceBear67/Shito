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

        var t = TEMPLATE_ENGINE.getLiteralTemplate("[{{ repository.full_name }}]\n" +
                "{% for commit in commits %}\n" +
                "{{ commit.committer.username }} HKT {{ commit.timestamp | date(existingFormat=\"yyyy-MM-dd'T'HH:mm:ssX\", format = \"HH:mm:ss\") }} \n" +
                "#{{ commit.id | slice(0,7) }}({{ ref | split(\"\\/\") | last }}):\n" +
                "{{commit.message | replace( {\"\n" +
                "\": \"\n" +
                "- \"} ) }}\n" +
                "{% endfor %}")

             /*   "{% for commit in commits %}\n" +
                "{{ commit.committer.username }} HKT {{ commit.timestamp | date(existingFormat=\"yyyy-MM-dd'T'HH:mm:ssX\", format = \"HH:mm:ss\") }}\n" +
                "#{{ commit.id | slice(0,7) }}({{ ref | spilt(\"/\") | last }}):\n" +
                "{{commit.message | spilt(\"\\n\") | join(\"\\n - )}}\n" +
                "{% endfor %}");

              */;
        var excepted = "[iceBear67/Shito]\n" +
                "iceBear67 HKT 10:09:34 \n" +
                "#2ca42ab(master):\n" +
                "Add listAll\n" +
                "- test";
        try(var sw = new StringWriter()){
            t.evaluate(sw,new DelegatingMap(new JsonPathMap(JsonPath.parse(RenderTest.class.getClassLoader().getResourceAsStream("test.json"))),new HashMap<>()));
            //System.out.println(sw);
            Assertions.assertEquals(excepted,sw.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
