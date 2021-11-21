
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import shito.Shito;
import shito.api.data.ShitoMessage;
import shito.api.data.ShitoTemplate;
import shito.handler.MessageBroadcaster;
import shito.manager.FileTemplateManager;
import shito.util.JsonPathMap;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.UUID;

public class MessageFlowTest {
    @Test
    public void onTest(){
        //FileSystem jfs = Jimfs.newFileSystem();
        Shito shito = new Shito(Path.of(System.getProperty("java.io.tmpdir")));
        ShitoTemplate template = new ShitoTemplate("test",UUID.randomUUID(), "A new repository has been added.\n" +
                "Name: {{ repository.full_name }}\n" +
                "Url: {{ repository.html_url }}\n" +
                "Language: {{ repository.language }}", true, "114514");

        shito.getTemplateManager().saveTemplate(template);
        JsonPathMap jpm = new JsonPathMap(JsonPath.parse(RenderTest.class.getClassLoader().getResourceAsStream("test.json")));
        Assertions.assertEquals("A new repository has been added.\n" +
                "Name: iceBear67/ShitoUrl: https://github.com/iceBear67/ShitoLanguage: Java",ShitoMessage.of(template, jpm).render(null));
    }
}
