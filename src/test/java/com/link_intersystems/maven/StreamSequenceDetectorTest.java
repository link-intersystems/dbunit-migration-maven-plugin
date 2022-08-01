package com.link_intersystems.maven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
class StreamSequenceDetectorTest {

    public static final String START_TOKEN = "<artifactId>";
    public static final String END_TOKEN = "</artifactId>";
    public static final String SEQUENCE = "${artifactId}";
    private StreamSequenceDetector streamSequenceDetector;
    private PushbackReader pushbackReader;

    @BeforeEach
    void setUp() {
        pushbackReader = new PushbackReader(new StringReader(START_TOKEN + SEQUENCE + END_TOKEN), 14);
        streamSequenceDetector = new StreamSequenceDetector(SEQUENCE);
    }

    @Test
    void consume() throws IOException {
        assertFalse(streamSequenceDetector.consume(pushbackReader));

        char[] buff12 = new char[12];
        assertEquals(12, pushbackReader.read(buff12));
        assertArrayEquals(START_TOKEN.toCharArray(), buff12);

        assertTrue(streamSequenceDetector.consume(pushbackReader));
        char[] buff13 = new char[13];

        assertEquals(13, pushbackReader.read(buff13));
        assertArrayEquals(END_TOKEN.toCharArray(), buff13);
        assertFalse(streamSequenceDetector.consume(pushbackReader));
    }


    @Test
    void detect() throws IOException {
        assertFalse(streamSequenceDetector.detect(pushbackReader));

        char[] buff12 = new char[12];
        assertEquals(12, pushbackReader.read(buff12));
        assertArrayEquals(START_TOKEN.toCharArray(), buff12);

        assertTrue(streamSequenceDetector.detect(pushbackReader));
        char[] buff13 = new char[13];
        assertEquals(13, pushbackReader.read(buff13));
        assertArrayEquals(SEQUENCE.toCharArray(), buff13);

        assertEquals(13, pushbackReader.read(buff13));
        assertArrayEquals(END_TOKEN.toCharArray(), buff13);
        assertFalse(streamSequenceDetector.detect(pushbackReader));
    }

    @Test
    void detectSingleChar() throws IOException {
        streamSequenceDetector = new StreamSequenceDetector("}");
        assertFalse(streamSequenceDetector.detect(pushbackReader));

        char[] buff12 = new char[12];
        assertEquals(12, pushbackReader.read(buff12));
        assertArrayEquals(START_TOKEN.toCharArray(), buff12);

        assertFalse(streamSequenceDetector.detect(pushbackReader));
        char[] buff13 = new char[13];
        assertEquals(12, pushbackReader.read(buff12));
        assertTrue(streamSequenceDetector.detect(pushbackReader));
        assertEquals('}', pushbackReader.read());

        assertEquals(13, pushbackReader.read(buff13));
        assertArrayEquals(END_TOKEN.toCharArray(), buff13);
        assertFalse(streamSequenceDetector.detect(pushbackReader));
    }

    @Test
    void consumeSingleChar() throws IOException {
        streamSequenceDetector = new StreamSequenceDetector("}");

        assertFalse(streamSequenceDetector.consume(pushbackReader));

        char[] buff12 = new char[12];
        assertEquals(12, pushbackReader.read(buff12));
        assertArrayEquals(START_TOKEN.toCharArray(), buff12);

        assertFalse(streamSequenceDetector.consume(pushbackReader));
        assertEquals(12, pushbackReader.read(buff12));
        assertTrue(streamSequenceDetector.consume(pushbackReader));
        char[] buff13 = new char[13];

        assertEquals(13, pushbackReader.read(buff13));
        assertArrayEquals(END_TOKEN.toCharArray(), buff13);
        assertFalse(streamSequenceDetector.consume(pushbackReader));
    }
}