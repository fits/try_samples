import com.sun.tools.attach.VirtualMachine

VirtualMachine.list().each {
	println "----- ${it.id()} -----"
	println it.displayName()
	println ''
}
