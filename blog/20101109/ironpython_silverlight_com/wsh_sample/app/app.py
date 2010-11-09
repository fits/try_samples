from System.Windows import *
from System.Windows.Controls import UserControl
from System.Runtime.InteropServices.Automation import AutomationFactory

class App:
  def __init__(self):
    root = Application.Current.LoadRootVisual(UserControl(), "app.xaml")
    root.Message.Text = "Notepad Sample"
    
    root.NotepadButton.Content = "Run Notepad"
    root.NotepadButton.Click += self.button_click

  def button_click(self, sender, e):
    if AutomationFactory.IsAvailable:
      try:
        wsh = AutomationFactory.CreateObject("WScript.Shell")
        wsh.Run("notepad.exe")

      except Exception as e:
        MessageBox.Show("Error: " . e)

    else:
      MessageBox.Show("AutomationFactory: NG")

App()