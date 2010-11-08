include System::Windows
include System::Windows::Controls
include System::Runtime::InteropServices::Automation

class App
  def initialize
    @root = Application.current.load_root_visual(UserControl.new, "app.xaml")

    @root.find_name('message').text = "Notepad Sample"

    btn = @root.find_name('notepadButton')
    btn.content = "Run Notepad"

    btn.Click {|sender, e|
    	if AutomationFactory.IsAvailable
    		begin
    			wsn = AutomationFactory.CreateObject("WScript.Network")
    			#MessageBox.Show(wsh.public_methods.to_s)
    			MessageBox.Show(wsn.ComputerName)

    			wsh = AutomationFactory.CreateObject("WScript.Shell")
    			#Run メソッドは正常に実行できない
    			r = wsh.Run("notepad.exe")

    		rescue => e
    			MessageBox.Show("AutomationFactory Error" + e)
    		end
    	else
			MessageBox.Show("AutomationFactory: NG")
    	end
	}
  end
end

$app = App.new
