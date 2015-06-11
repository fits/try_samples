@Grab('org.ow2.asm:asm-all:5.0.4')
import static org.objectweb.asm.Opcodes.*

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.Type
import org.objectweb.asm.commons.GeneratorAdapter
import org.objectweb.asm.commons.Method
import org.objectweb.asm.commons.InstructionAdapter

def node = new ClassNode()

def cr = new ClassReader(new File('Proc.class').newInputStream())

cr.accept(node, 0)

node.methods.each {
	println it.name

	if (it.name == 'call') {
		def inst = it.instructions
		inst.clear()

		inst.insert(new InsnNode(IRETURN))
		inst.insert(new IntInsnNode(BIPUSH, 50))
	}
}

def cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)

node.accept(cw)

def buf = cw.toByteArray()

def cls = ClassLoader.getSystemClassLoader().defineClass(null, buf, 0, buf.length)

println cls

def s = cls.newInstance()

println s.call('a')

println new Proc().call('sample')
