include System::Windows
include System::Windows::Controls
include System::Runtime::InteropServices::Automation

class App
  def initialize
    @root = Application.current.load_root_visual(UserControl.new, "app.xaml")

    @root.find_name('Message').text = "Notepad Sample"

    btn = @root.find_name('NotepadButton')
    btn.content = "Run Notepad"
    btn.Click {|sender, e|
    	if AutomationFactory.IsAvailable
    		begin
    			wsh = AutomationFactory.CreateObject("WScript.Shell")
    			#Run メソッドは正常に実行されない（例外も発生せず落ちる模様）
    			r = wsh.Run("notepad.exe")
    		rescue => e
    			MessageBox.Show("Error: " + e)
    		end
    	else
			MessageBox.Show("AutomationFactory: NG")
    	end
	}
  end
end

$app = App.new
