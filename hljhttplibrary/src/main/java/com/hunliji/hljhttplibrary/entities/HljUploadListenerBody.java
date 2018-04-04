package com.hunliji.hljhttplibrary.entities;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * Created by Suncloud on 2016/8/24.
 */
public class HljUploadListenerBody extends RequestBody {
    private RequestBody requestBody;
    private HljUploadListener listener;

    public HljUploadListenerBody(RequestBody requestBody, HljUploadListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        try {
            this.listener.setContentLength(requestBody.contentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(new ForwardingSink(sink) {
            private long bytesWritten = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                listener.transferred(bytesWritten);
            }
        });
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();

    }
}
