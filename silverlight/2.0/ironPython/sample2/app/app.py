
from System.Windows import Application
from System.Windows.Controls import UserControl
from System.Windows.Browser import HtmlPage

def onClickButton(sender, args):
	HtmlPage.Window.Alert("ボタンクリック");
#	scene.testBlock.Text += " - ボタンクリック"

scene = Application.Current.LoadRootVisual(UserControl(), 'app.xaml')

scene.testButton.Click += onClickButton
