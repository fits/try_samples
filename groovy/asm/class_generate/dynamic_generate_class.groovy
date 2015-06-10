@Grab('org.ow2.asm:asm-all:5.0.4')
import static org.objectweb.asm.Opcodes.*

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method

def cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
cw.visit(V1_8, ACC_PUBLIC, 'Sample', null, 'java/lang/Object', null)

def m1 = Method.getMethod('void <init>()')
def mg1 = new GeneratorAdapter(ACC_PUBLIC, m1, null, null, cw)

mg1.loadThis()
mg1.invokeConstructor(Type.getType(Object), m1)
mg1.returnValue()

mg1.endMethod()

def m2 = Method.getMethod('int call(String)')
def mg2 = new GeneratorAdapter(ACC_PUBLIC, m2, null, null, cw)

mg2.push(5)
mg2.returnValue()

mg2.endMethod()

cw.visitEnd()

def buf = cw.toByteArray()

def cls = ClassLoader.getSystemClassLoader().defineClass(null, buf, 0, buf.length)

println cls

def s = cls.newInstance()

println s.call('a')
