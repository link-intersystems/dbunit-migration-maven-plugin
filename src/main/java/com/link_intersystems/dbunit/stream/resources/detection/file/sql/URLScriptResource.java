package com.link_intersystems.dbunit.stream.resources.detection.file.sql;

import com.link_intersystems.sql.io.SqlScript;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class URLScriptResource implements SqlScript.ScriptResource {

    public static URLScriptResource fromFile(File file) {
        try {
            return new URLScriptResource(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL url;

    private Charset charset = StandardCharsets.UTF_8;
    private int streamBufferSize = 8192;

    public URLScriptResource(URL url) {
        this.url = requireNonNull(url);
    }

    public void setStreamBufferSize(int streamBufferSize) {
        if (streamBufferSize < 1) {
            throw new IllegalArgumentException("streamBufferSize must be 1 or greater");
        }
        this.streamBufferSize = streamBufferSize;
    }

    public void setCharset(Charset charset) {
        this.charset = requireNonNull(charset);
    }

    @Override
    public Reader open() throws IOException {
        return new InputStreamReader(new BufferedInputStream(url.openStream(), streamBufferSize), charset);
    }
}
