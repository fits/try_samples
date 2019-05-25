package test;

import java.lang.instrument.*;

public class SampleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain: " + agentArgs);

        inst.addTransformer(new SampleTransformer());
    }
}
