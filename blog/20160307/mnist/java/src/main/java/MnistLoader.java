
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.FeatureUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class MnistLoader {
    private final static int LABELS_NUM = 10;

    /**
     *
     * @param imageFileName
     * @param labelFileName
     * @return
     */
    public static CompletableFuture<DataSet> loadMnist(String imageFileName, String labelFileName) {

        return CompletableFuture.supplyAsync(() -> loadImages(imageFileName))
            .thenCombineAsync(
                CompletableFuture.supplyAsync(() -> loadLabels(labelFileName)),
                DataSet::new
            );
    }

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private static INDArray loadImages(String fileName) {
        try (FileChannel fc = FileChannel.open(Paths.get(fileName))) {
            ByteBuffer headerBuf = ByteBuffer.allocateDirect(16);

            fc.read(headerBuf);

            headerBuf.rewind();

            int magicNum = headerBuf.getInt();
            int num = headerBuf.getInt();
            int rowNum = headerBuf.getInt();
            int colNum = headerBuf.getInt();

            ByteBuffer buf = ByteBuffer.allocateDirect(num * rowNum * colNum);

            fc.read(buf);

            buf.rewind();

            int dataSize = rowNum * colNum;

            INDArray res = Nd4j.create(num, dataSize);

            for (int n = 0; n < num; n++) {
                INDArray d = Nd4j.create(1, dataSize);

                for (int i = 0; i < dataSize; i++) {
                    d.putScalar(i, buf.get() & 0xff);
                }

                res.putRow(n, d);
            }
            return res;

        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private static INDArray loadLabels(String fileName) {
        try (FileChannel fc = FileChannel.open(Paths.get(fileName))) {

            ByteBuffer headerBuf = ByteBuffer.allocateDirect(8);

            fc.read(headerBuf);

            headerBuf.rewind();

            int magicNum = headerBuf.getInt();
            int num = headerBuf.getInt();

            ByteBuffer buf = ByteBuffer.allocateDirect(num);

            fc.read(buf);

            buf.rewind();

            INDArray res = Nd4j.create(num, LABELS_NUM);

            for (int i = 0; i < num; i++) {
                res.putRow(i, FeatureUtil.toOutcomeVector(buf.get(), LABELS_NUM));
            }

            return res;

        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
