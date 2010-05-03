import clr
clr.AddReference("PresentationFramework")
clr.AddReference("System.Xml")

from System.Xml import XmlReader
from System.Windows.Markup import XamlReader

xaml = XmlReader.Create("app.xaml")
app = XamlReader.Load(xaml)

xaml.Close()

app.Run()
