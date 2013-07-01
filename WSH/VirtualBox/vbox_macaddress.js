
var args = WScript.Arguments;

if (args.Count() < 1) {
	WScript.Echo("<machine name>");
	WScript.Quit();
}

var vb = WScript.CreateObject("VirtualBox.VirtualBox");

// 仮想マシンの取得
var mc = vb.findMachine(args.Item(0));

// 最初のネットワークアダプタ取得
var na = mc.getNetworkAdapter(0);

// MACアドレスの出力
WScript.Echo(na.MACAddress);
