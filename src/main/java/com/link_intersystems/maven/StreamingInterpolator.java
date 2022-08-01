package com.link_intersystems.maven;


import org.codehaus.plexus.interpolation.ValueSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class StreamingInterpolator extends Reader {

    private List<ValueSource> valueSources = new ArrayList<>();

    public static final String DEFAULT_START_EXPR = "${";

    public static final String DEFAULT_END_EXPR = "}";

    private PushbackReader reader;

    private StreamSequenceDetector escapeString;
    private int escapeCount = 0;

    private StringBuilder pushback = new StringBuilder();
    private StreamSequenceDetector startSequenceDetector;
    private final StreamSequenceDetector endSequenceDetector;


    public StreamingInterpolator(Reader reader) {
        this(reader, DEFAULT_START_EXPR, DEFAULT_END_EXPR);
    }

    public StreamingInterpolator(Reader reader, String startExpr, String endExpr) {
        this.reader = new PushbackReader(new BufferedReader(reader), 4096);
        startSequenceDetector = new StreamSequenceDetector(startExpr);
        endSequenceDetector = new StreamSequenceDetector(endExpr);
    }

    public String getEscapeString() {
        return escapeString.getSequence().toString();
    }

    public void setEscapeString(String escapeString) {
        this.escapeString = new StreamSequenceDetector(escapeString);
    }

    public void addValueSource(ValueSource valueSource) {
        valueSources.add(valueSource);
    }

    public void removeValuesSource(ValueSource valueSource) {
        valueSources.remove(valueSource);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int readChars = 0;
        while (len-- > 0) {
            if (escapeCount == 0 && escapeString != null && escapeString.detect(reader)) {
                escapeCount = escapeString.getSequence().length() + 1;
            }

            if (escapeCount == 0 && startSequenceDetector.consume(reader)) {
                StringBuilder interpolationBuffer = new StringBuilder();

                boolean endSequenceDetected;
                while (!(endSequenceDetected = endSequenceDetector.consume(reader))) {
                    int read = reader.read();
                    if (read == -1) {
                        break;
                    }

                    interpolationBuffer.append((char) read);
                }

                if (endSequenceDetected) {
                    String interpolated = interpolate(interpolationBuffer.toString());
                    reader.unread(interpolated.toCharArray());
                } else {
                    reader.unread(interpolationBuffer.toString().toCharArray());
                }
            }

            int read = reader.read();
            if (read == -1) {
                break;
            }
            if (escapeCount > 0) {
                escapeCount--;
            }
            readChars++;
            cbuf[off++] = (char) read;
        }

        if (readChars == 0) {
            return -1;
        }

        return readChars;
    }

    private String interpolate(String expression) {
        Object value = null;
        for (ValueSource valueSource : valueSources) {
            value = valueSource.getValue(expression);

            if (value != null) {
                break;
            }
        }

        return String.valueOf(value);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }


}
