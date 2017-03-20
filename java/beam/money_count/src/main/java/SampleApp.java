import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.ToString;

public class SampleApp {
    public static void main(String... args) throws Exception {
        PipelineOptions opt = PipelineOptionsFactory.create();
        Pipeline p = Pipeline.create(opt);

        p.apply(TextIO.Read.from(args[0]))
                .apply(Count.perElement())
                .apply(ToString.kvs())
                .apply(ParDo.of(new DoFn<String, String>() {
                    @ProcessElement
                    public void process(ProcessContext ctx) {
                        System.out.println(ctx.element());
                    }
                }));

        p.run().waitUntilFinish();
    }
}