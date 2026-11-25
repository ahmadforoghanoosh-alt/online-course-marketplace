package kz.kaznaru.capstone.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonTest {
    @Test
    void writesMapsListsAndEscapesText() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", "Say \"hi\"\n");
        m.put("price", 15000.5);
        m.put("ids", List.of(1L, 2L));
        m.put("ok", true);
        m.put("none", null);

        assertEquals("{\"name\":\"Say \\\"hi\\\"\\n\",\"price\":15000.50,\"ids\":[1,2],\"ok\":true,\"none\":null}",
                Json.write(m));
    }
}
