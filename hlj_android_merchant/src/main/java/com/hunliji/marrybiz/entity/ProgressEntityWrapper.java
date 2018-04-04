/**
 *
 */
package com.hunliji.marrybiz.entity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

/**
 * @author iDay
 */
public class ProgressEntityWrapper extends HttpEntityWrapper {
    private ProgressListener progressListener;

    static class ProgressOutputStream extends FilterOutputStream {
        private ProgressListener progressListener;
        private int transferred;

        public ProgressOutputStream(OutputStream out, ProgressListener progressListener) {
            super(out);
            this.progressListener = progressListener;
            this.transferred = 0;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.io.FilterOutputStream#write(byte[], int, int)
         */
        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            this.transferred += length;
            this.progressListener.transferred(this.transferred);
            out.write(buffer, offset, length);
        }

        /*
         * (non-Javadoc)
         *
         * @see java.io.FilterOutputStream#write(int)
         */
        @Override
        public void write(int oneByte) throws IOException {
            this.progressListener.transferred(++this.transferred);
            out.write(oneByte);
        }

    }

    public ProgressEntityWrapper(HttpEntity wrapped, ProgressListener progressListener) {
        super(wrapped);
        this.progressListener = progressListener;
        this.progressListener.setContentLength(wrapped.getContentLength());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.http.entity.HttpEntityWrapper#writeTo(java.io.OutputStream)
     */
    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(outstream instanceof ProgressOutputStream ? outstream : new ProgressOutputStream(outstream, progressListener));
    }

}
