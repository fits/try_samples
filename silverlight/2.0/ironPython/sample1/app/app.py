
from System.Windows import Application
from System.Windows.Controls import UserControl

def onClickTextBlock1(sender, args):
	sender.FontSize *= 2

def onClickTextBlock2(sender, args):
	scene.TextBlock1.FontSize = defaultFontSize

scene = Application.Current.LoadRootVisual(UserControl(), 'app.xaml')

defaultFontSize = scene.TextBlock1.FontSize

scene.TextBlock1.MouseLeftButtonUp += onClickTextBlock1
scene.TextBlock2.MouseLeftButtonUp += onClickTextBlock2
