extends CanvasLayer

signal game_started

func _ready():
	$EndMessage.hide()
	$StartButton.show()

func end_game():
	$EndMessage.show()
	$StartButton.show()


func _on_start_button_pressed():
	$StartButton.hide()
	$EndMessage.hide()
	
	game_started.emit()
