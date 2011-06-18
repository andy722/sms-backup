package com.nsu.smsbackup.mail;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author andy
 */
class StringDataSource implements DataSource {
    private static final String TYPE = "text/plain";

    private final String data;

    public StringDataSource(String data) {
        super();
        this.data = data;
    }

    public String getContentType() {
        return TYPE;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data.getBytes());
    }

    public String getName() {
        return "StringDataSource";
    }

    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not Supported");
    }
}
