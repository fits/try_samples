include System

wsh = Activator.CreateInstance(Type.GetTypeFromProgID("WScript.Shell"))
wsh.Run("notepad.exe")

