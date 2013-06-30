
var args = WScript.Arguments;

if (args.Count() < 1) {
	WScript.Echo("<virtual machine name>");
	WScript.Quit();
}

var vb = WScript.CreateObject("VirtualBox.VirtualBox");

WScript.Echo(vb.version);
WScript.Echo(vb.versionNormalized);

var mc = vb.findMachine(args.Item(0));

if (mc) {
	WScript.Echo(mc.name + ", " + mc.id);
}

//WScript.Echo("" + mc.machines);
