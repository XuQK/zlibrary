package xuqk.github.zlibrary.basekit.zlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * ClassName: LogFormat <br/>
 * PackageName: xuqk.github.zlibrary.basekit.zlog <br/>
 * Create On: 12/24/17 4:26 PM <br/>
 * Site:https://github.XuQK <br/>
 * @author 徐乾琨 <br/>
 */

class LogFormat {

    private static final int JSON_INDENT = 4;
    private static final int XML_INDENT = 4;

    private static final char VERTICAL_BORDER_CHAR = '║';

    private static final String TOP_HORIZONTAL_BORDER =
            "╔═════════════════════════════════════════════════" +
                    "══════════════════════════════════════════════════";

    private static final String DIVIDER_HORIZONTAL_BORDER =
            "╟─────────────────────────────────────────────────" +
                    "──────────────────────────────────────────────────";

    private static final String BOTTOM_HORIZONTAL_BORDER =
            "╚═════════════════════════════════════════════════" +
                    "══════════════════════════════════════════════════";

    static String formatJson(String json) {
        String formatted = null;
        if (json == null || json.length() == 0) {
            return null;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jo = new JSONObject(json);
                formatted = jo.toString(JSON_INDENT);
            } else if (json.startsWith("[")) {
                JSONArray ja = new JSONArray(json);
                formatted = ja.toString(JSON_INDENT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return formatted;
    }

    static String formatXml(String xml) {
        String formatted = null;
        if (xml == null || xml.trim().length() == 0) {
            return null;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
                    String.valueOf(XML_INDENT));
            transformer.transform(xmlInput, xmlOutput);
            formatted = xmlOutput.getWriter().toString().replaceFirst(">", ">"
                    + XPrinter.lineSeparator);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return formatted;
    }

    static String formatThrowable(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


    static String formatArgs(String format, Object... args) {
        if (format != null) {
            return String.format(format, args);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, N = args.length; i < N; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }

    static String formatBorder(String[] segments) {
        if (segments == null || segments.length == 0) {
            return "";
        }
        String[] nonNullSegments = new String[segments.length];
        int nonNullCount = 0;
        for (String segment : segments) {
            if (segment != null) {
                nonNullSegments[nonNullCount++] = segment;
            }
        }
        if (nonNullCount == 0) {
            return "";
        }

        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(TOP_HORIZONTAL_BORDER).append(XPrinter.lineSeparator);
        for (int i = 0; i < nonNullCount; i++) {
            msgBuilder.append(appendVerticalBorder(nonNullSegments[i]));
            if (i != nonNullCount - 1) {
                msgBuilder.append(XPrinter.lineSeparator).append(DIVIDER_HORIZONTAL_BORDER)
                        .append(XPrinter.lineSeparator);
            } else {
                msgBuilder.append(XPrinter.lineSeparator).append(BOTTOM_HORIZONTAL_BORDER);
            }
        }
        return msgBuilder.toString();
    }

    private static String appendVerticalBorder(String msg) {
        StringBuilder borderedMsgBuilder = new StringBuilder(msg.length() + 10);
        String[] lines = msg.split(XPrinter.lineSeparator);
        for (int i = 0, n = lines.length; i < n; i++) {
            if (i != 0) {
                borderedMsgBuilder.append(XPrinter.lineSeparator);
            }
            String line = lines[i];
            borderedMsgBuilder.append(VERTICAL_BORDER_CHAR).append(line);
        }
        return borderedMsgBuilder.toString();
    }
}
