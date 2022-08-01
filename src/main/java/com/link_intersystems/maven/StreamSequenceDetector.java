package com.link_intersystems.maven;

import java.io.IOException;
import java.io.PushbackReader;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class StreamSequenceDetector {

    private CharSequence sequence;

    public StreamSequenceDetector(CharSequence sequence) {
        this.sequence = sequence;
    }

    public CharSequence getSequence() {
        return sequence;
    }

    public boolean detect(PushbackReader pushbackReader) throws IOException {
        StringBuilder pushbackBuff = new StringBuilder();

        int index = 0;
        int read;

        while ((read = pushbackReader.read()) != -1) {
            pushbackBuff.append((char) read);

            if (index >= sequence.length() || sequence.charAt(index) != read) {
                break;
            }
            index++;
        }

        pushbackReader.unread(pushbackBuff.toString().toCharArray());
        return sequence.length() == index;
    }

    public boolean consume(PushbackReader pushbackReader) throws IOException {
        StringBuilder pushbackBuff = new StringBuilder();

        int index = 0;
        int read;

        while ((read = pushbackReader.read()) != -1) {
            if (index >= sequence.length() || sequence.charAt(index) != read) {
                pushbackReader.unread(read);
                break;
            }
            index++;
            pushbackBuff.append((char) read);
        }

        if (sequence.length() == index) {
            return true;
        }

        pushbackReader.unread(pushbackBuff.toString().toCharArray());
        return sequence.length() == index;
    }
}
