package kz.kaznaru.capstone.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A file of the website (HTML, CSS, JavaScript or the SVG logo) from src/main/resources/web (ADR-0011).
 * Only simple file names are allowed, so a path like "/../pom.xml" can never read other files.
 */
public record WebFile(String contentType, byte[] bytes) {
    private static final Pattern SAFE_NAME = Pattern.compile("[a-z0-9-]+\\.(html|css|js|svg)");
    private static final Map<String, String> TYPES = Map.of(
            "html", "text/html; charset=utf-8",
            "css", "text/css; charset=utf-8",
            "js", "text/javascript; charset=utf-8",
            "svg", "image/svg+xml");

    /** "/" gives index.html; "/app.js" gives app.js; anything else (for example "/courses") is not a web file. */
    public static Optional<WebFile> find(String path) {
        String name = path == null || path.equals("/") ? "index.html" : path.substring(1);
        if (!SAFE_NAME.matcher(name).matches()) {
            return Optional.empty();
        }
        try (InputStream in = WebFile.class.getResourceAsStream("/web/" + name)) {
            if (in == null) {
                return Optional.empty();
            }
            String extension = name.substring(name.lastIndexOf('.') + 1);
            return Optional.of(new WebFile(TYPES.get(extension), in.readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
