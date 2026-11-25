package kz.kaznaru.capstone.ui;

/** What the API sends back: an HTTP status code and a JSON body. */
public record ApiResponse(int status, String body) {
}
