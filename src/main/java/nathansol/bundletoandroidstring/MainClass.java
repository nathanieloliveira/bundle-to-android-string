package nathansol.bundletoandroidstring;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainClass {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            return;
        }

        File input = new File(args[0]);
        File output = new File(args[1]);

        List<KeyValue> keyValues = readValuesFromBundle(input);
        exportToAndroidString(keyValues, output);
    }

    private static List<KeyValue> readValuesFromBundle(File input) throws Exception {
        try (BufferedSource src = Okio.buffer(Okio.source(input))) {
            ArrayList<KeyValue> values = new ArrayList<>();

            // process file
            while (!src.exhausted()) {
                String line = src.readUtf8Line();
                if (line == null) {
                    break;
                }

                // ignore comments
                if (line.startsWith("#")) {
                    continue;
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] keyval = line.split("=");
                values.add(new KeyValue(keyval[0], keyval[1]));
            }

            return values;
        }
    }

    private static void exportToAndroidString(List<KeyValue> values, File output) throws Exception {
        try (BufferedSink out = Okio.buffer(Okio.sink(output))) {
            out.writeUtf8("<resources>\n");
            for (KeyValue value : values) {
                out.writeUtf8("\t<string name=\"");
                // write key
                out.writeUtf8(value.key.replace('.', '_'));
                out.writeUtf8("\">");
                // write value
                out.writeUtf8(value.value);
                out.writeUtf8("</string>\n");
            }
            out.writeUtf8("</resources>\n");

            out.flush();
        }
    }

    private static class KeyValue {
        final String key;
        final String value;

        KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}