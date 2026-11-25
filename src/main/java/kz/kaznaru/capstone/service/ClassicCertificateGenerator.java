package kz.kaznaru.capstone.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import kz.kaznaru.capstone.domain.Course;
import kz.kaznaru.capstone.domain.Student;

/**
 * Template Method: a third certificate format, a classic printable certificate (A4 landscape).
 * The steps (check, header, body, footer) come from CertificateGenerator; this class only gives the design.
 * Names and titles are escaped, so text from the database can not break the HTML.
 */
public class ClassicCertificateGenerator extends CertificateGenerator {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    @Override
    protected String header() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<title>Certificate of Completion</title><style>" + STYLE + "</style></head><body>"
                + "<div class=\"page\"><div class=\"frame\"><div class=\"inner\">"
                + "<span class=\"corner tl\"></span><span class=\"corner tr\"></span>"
                + "<span class=\"corner bl\"></span><span class=\"corner br\"></span>"
                + LOGO
                + "<div class=\"brand\">Course Marketplace</div>"
                + "<h1>Certificate</h1><div class=\"sub\">of Completion</div>"
                + "<div class=\"rule\"><span>&#10022;</span></div>";
    }

    @Override
    protected String body(Student student, Course course, LocalDate date) {
        String number = String.format(Locale.ROOT, "CM-%d-%04d-%04d", date.getYear(), course.id(),
                Math.abs(student.getName().hashCode()) % 10000);
        return "<p class=\"lead\">This is proudly presented to</p>"
                + "<div class=\"name\">" + escape(student.getName()) + "</div>"
                + "<p class=\"lead\">for successfully completing the course</p>"
                + "<div class=\"course\">" + escape(course.title()) + "</div>"
                + "<p class=\"by\">taught by " + escape(course.instructor()) + "</p>"
                + "<div class=\"bottom\">"
                + "<div class=\"sign\"><div class=\"line\">" + escape(course.instructor()) + "</div>"
                + "<small>Instructor</small></div>"
                + "<div class=\"seal\">" + SEAL + "</div>"
                + "<div class=\"sign\"><div class=\"line\">" + DATE.format(date) + "</div>"
                + "<small>Date of completion</small></div>"
                + "</div>"
                + "<div class=\"number\">Certificate No. " + number + "</div>";
    }

    @Override
    protected String footer() {
        return "</div></div></div></body></html>\n";
    }

    /** Makes text safe to put inside HTML. */
    static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private static final String STYLE = String.join("",
            "@page{size:A4 landscape;margin:0}",
            "*{box-sizing:border-box}",
            "html,body{margin:0;background:#e9eef3}",
            "body{display:flex;justify-content:center;align-items:center;min-height:100vh;padding:12px;",
            "-webkit-print-color-adjust:exact;print-color-adjust:exact}",
            ".page{container-type:inline-size;width:min(100%,1123px);aspect-ratio:297/210;",
            "background:#fffdf6;box-shadow:0 6px 30px rgba(0,0,0,.18)}",
            ".frame{height:100%;padding:2.2cqw;background:",
            "radial-gradient(circle at 50% 40%,#fffef9 0,#fbf6e7 70%,#f3ead0 100%)}",
            ".inner{position:relative;height:100%;border:.45cqw double #b8913a;outline:.15cqw solid #d9bf7c;",
            "outline-offset:-1.3cqw;display:flex;flex-direction:column;align-items:center;justify-content:flex-start;",
            "padding:2.6cqw 6cqw 2cqw;text-align:center;color:#2b2b2b;",
            "font-family:Georgia,'Times New Roman',serif}",
            ".corner{position:absolute;width:5cqw;height:5cqw;border:.35cqw solid #b8913a}",
            ".tl{top:1.8cqw;left:1.8cqw;border-right:0;border-bottom:0}",
            ".tr{top:1.8cqw;right:1.8cqw;border-left:0;border-bottom:0}",
            ".bl{bottom:1.8cqw;left:1.8cqw;border-right:0;border-top:0}",
            ".br{bottom:1.8cqw;right:1.8cqw;border-left:0;border-top:0}",
            ".logo{width:5.2cqw;height:5.2cqw}",
            ".brand{margin-top:.5cqw;font-size:1.25cqw;letter-spacing:.45cqw;text-transform:uppercase;color:#1c6fb8}",
            "h1{margin:1cqw 0 0;font-size:5.2cqw;font-weight:normal;letter-spacing:.9cqw;text-transform:uppercase;",
            "color:#8a6a22}",
            ".sub{font-size:2cqw;letter-spacing:.5cqw;text-transform:uppercase;color:#6b5a33}",
            ".rule{display:flex;align-items:center;gap:1.2cqw;width:34cqw;margin:1.3cqw 0 .6cqw;color:#b8913a;",
            "font-size:1.4cqw}",
            ".rule::before,.rule::after{content:'';flex:1;height:.12cqw;background:linear-gradient(90deg,",
            "transparent,#b8913a,transparent)}",
            ".lead{margin:.9cqw 0 .3cqw;font-size:1.6cqw;font-style:italic;color:#555}",
            ".name{font-family:'Palatino Linotype','Book Antiqua',Palatino,Georgia,serif;font-style:italic;",
            "font-size:4.6cqw;color:#16324f;padding:0 3cqw .4cqw;border-bottom:.12cqw solid #d9bf7c;min-width:40cqw}",
            ".course{margin-top:.3cqw;font-size:2.8cqw;font-weight:bold;color:#1c6fb8}",
            ".by{margin:.4cqw 0 0;font-size:1.4cqw;color:#666}",
            ".bottom{margin-top:auto;width:100%;display:flex;align-items:flex-end;justify-content:space-between}",
            ".sign{width:26cqw}",
            ".sign .line{font-family:'Palatino Linotype',Georgia,serif;font-style:italic;font-size:1.8cqw;",
            "padding-bottom:.4cqw;border-bottom:.12cqw solid #2b2b2b}",
            ".sign small{display:block;margin-top:.4cqw;font-size:1.1cqw;letter-spacing:.2cqw;",
            "text-transform:uppercase;color:#777}",
            ".seal svg{width:11cqw;height:11cqw;display:block}",
            ".number{margin-top:.8cqw;font-size:1cqw;letter-spacing:.15cqw;color:#8b8b8b}",
            "@media print{html,body{background:none;padding:0;display:block}",
            ".page{width:297mm;height:210mm;box-shadow:none}}");

    private static final String LOGO = "<svg class=\"logo\" viewBox=\"0 0 64 64\" aria-hidden=\"true\">"
            + "<defs><linearGradient id=\"sky\" x1=\"0\" y1=\"0\" x2=\"1\" y2=\"1\">"
            + "<stop offset=\"0\" stop-color=\"#5ab8f5\"/><stop offset=\"1\" stop-color=\"#1c7fd6\"/>"
            + "</linearGradient></defs><rect width=\"64\" height=\"64\" rx=\"16\" fill=\"url(#sky)\"/>"
            + "<path d=\"M32 9 L50 17 L32 25 L14 17 Z\" fill=\"#fff\"/>"
            + "<path d=\"M22 21 V26 C22 29 42 29 42 26 V21 L32 25.5 Z\" fill=\"#dff0fd\"/>"
            + "<path d=\"M10 33 C18 30 26 31 31 35 V54 C26 50 18 49 10 52 Z\" fill=\"#fff\"/>"
            + "<path d=\"M54 33 C46 30 38 31 33 35 V54 C38 50 46 49 54 52 Z\" fill=\"#eaf5fe\"/></svg>";

    /** A gold rosette seal, drawn with SVG (no image file needed). */
    private static final String SEAL = "<svg viewBox=\"0 0 120 120\" aria-hidden=\"true\">"
            + "<defs><radialGradient id=\"gold\" cx=\"40%\" cy=\"35%\" r=\"70%\">"
            + "<stop offset=\"0\" stop-color=\"#f7e3a1\"/><stop offset=\".55\" stop-color=\"#d4a940\"/>"
            + "<stop offset=\"1\" stop-color=\"#9c7424\"/></radialGradient>"
            + "<path id=\"arc\" d=\"M60 60 m-34 0 a34 34 0 1 1 68 0 a34 34 0 1 1 -68 0\"/></defs>"
            + "<path d=\"M44 92 L36 118 L50 110 L58 120 L60 94 Z\" fill=\"#1c6fb8\"/>"
            + "<path d=\"M76 92 L84 118 L70 110 L62 120 L60 94 Z\" fill=\"#2b8fe0\"/>"
            + "<polygon fill=\"url(#gold)\" points=\""
            + rosette() + "\"/>"
            + "<circle cx=\"60\" cy=\"60\" r=\"40\" fill=\"none\" stroke=\"#fff6d8\" stroke-width=\"1.5\"/>"
            + "<circle cx=\"60\" cy=\"60\" r=\"27\" fill=\"none\" stroke=\"#fff6d8\" stroke-width=\"1\"/>"
            + "<text font-family=\"Georgia,serif\" font-size=\"8.5\" letter-spacing=\"2\" fill=\"#5c4310\">"
            + "<textPath href=\"#arc\" startOffset=\"2%\">CERTIFIED &#8226; COURSE MARKETPLACE &#8226;</textPath></text>"
            + "<text x=\"60\" y=\"68\" text-anchor=\"middle\" font-family=\"Georgia,serif\" font-size=\"22\""
            + " fill=\"#5c4310\">&#9733;</text></svg>";

    /** Points of a 24-tooth rosette around the centre (60, 60). */
    private static String rosette() {
        StringBuilder points = new StringBuilder();
        int teeth = 48;
        for (int i = 0; i < teeth; i++) {
            double angle = Math.PI * 2 * i / teeth;
            double radius = i % 2 == 0 ? 50 : 45;
            points.append(String.format(Locale.ROOT, "%.1f,%.1f ",
                    60 + radius * Math.cos(angle), 60 + radius * Math.sin(angle)));
        }
        return points.toString().trim();
    }
}
