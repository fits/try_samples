
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;

public class AsyncFileReadSample {
	public static void main(String[] args) throws Exception {

		AsynchronousFileChannel fc = 
				AsynchronousFileChannel.open(Paths.get(args[0]));

		final ByteBuffer buf = ByteBuffer.allocate((int)fc.size());

		fc.read(buf, 0, null, new CompletionHandler<Integer, Object>() {
			@Override
			public void completed(Integer result, Object attach) {
				System.out.println("*** completed ***");
				System.out.println("result = " + result);

				System.out.println(buf.toString());
				buf.rewind();
				System.out.println(buf.toString());

				CharBuffer chb = Charset.defaultCharset().decode(buf);

                //ファイルの内容を出力
                System.out.println(chb.toString());
			}

			@Override
			public void failed(Throwable e, Object attach) {
				System.out.println("*** failed ***");
				e.printStackTrace();
			}
		});

		System.in.read();
	}
}