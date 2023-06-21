package com.leftbin.commons.lib.kubernetes.pod.logs;

import com.leftbin.commons.proto.v1.grpc.stream.OutputLine;
import io.grpc.stub.StreamObserver;
import lombok.Builder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream implementation that streams log data to a gRPC response observer.
 * This class is designed to stream logs of a pod running on a Kubernetes cluster
 * to clients over gRPC. When written to, it collects bytes and sends them
 * as {@link OutputLine} messages to the gRPC observer.
 *
 * <p>Example usage:
 * <pre>
 *     StreamObserver&lt;OutputLine&gt; responseObserver = ...; // Obtain a gRPC response observer
 *     PodLogGrpcStreamAdapter podLogGrpcStreamAdapter = PodLogGrpcStreamAdapter.builder()
 *                                                .streamObserver(responseObserver)
 *                                                .build();
 *     byte[] logData = ...; // Obtain log data bytes
 *     podLogGrpcStreamAdapter.write(logData, 0, logData.length);
 * </pre>
 * </p>
 *
 * @author Swarup Donepudi
 */
@Builder
public class PodLogGrpcStreamAdapter extends OutputStream {

    /**
     * The gRPC response observer to which log data is streamed.
     */
    private final StreamObserver<OutputLine> streamObserver;

    /**
     * A buffer to collect bytes before sending them to the gRPC response observer.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Writes a single byte to the internal buffer. Note that this method does not
     * immediately send the byte to the gRPC response observer.
     *
     * @param b the byte as an integer, which must be in the range 0 to 255.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(int b) throws IOException {
        buffer.append((char) b);
    }

    /**
     * Writes an array of bytes to the internal buffer and sends them to the gRPC response observer
     * as an {@link OutputLine}. This method should be used to stream log content bytes.
     *
     * @param b   the byte array to be written.
     * @param off the start offset in the byte array.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.append(new String(b, off, len));
        streamObserver.onNext(OutputLine.newBuilder().setValue(new String(b, off, len)).build());
    }
}
