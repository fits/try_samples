package test;

import java.lang.instrument.*;
import java.security.*;

public class SampleTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        
        System.out.println("transform class: " + className);
        
        return null;
    }
}
