package kz.kaznaru.capstone.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class WebFileTest {
    @Test
    void rootGivesTheHomePage() {
        WebFile page = WebFile.find("/").orElseThrow();

        assertEquals("text/html; charset=utf-8", page.contentType());
        assertTrue(new String(page.bytes(), StandardCharsets.UTF_8).contains("<title>Online Course Marketplace</title>"));
    }

    @Test
    void scriptAndStyleHaveTheRightType() {
        assertEquals("text/javascript; charset=utf-8", WebFile.find("/app.js").orElseThrow().contentType());
        assertEquals("text/css; charset=utf-8", WebFile.find("/style.css").orElseThrow().contentType());
        assertEquals("image/svg+xml", WebFile.find("/logo.svg").orElseThrow().contentType());
    }

    @Test
    void apiPathsAndOtherFilesAreNotWebFiles() {
        assertTrue(WebFile.find("/courses").isEmpty());
        assertTrue(WebFile.find("/missing.html").isEmpty());
        assertTrue(WebFile.find("/../pom.xml").isEmpty());
        assertTrue(WebFile.find("/web/../../App.class").isEmpty());
    }
}
