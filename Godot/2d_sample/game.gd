extends Node

@export var start_position = Vector2(100, 100)
@export var goal_position = Vector2(800, 500)

@onready var _player = $Player
@onready var _goal = $Goal
@onready var _hud = $HUD

func _new_game():
	_goal.start(goal_position)
	_player.start(start_position)

func _end_game():
	_goal.init()
	_player.init()
	_hud.end_game()



func _on_player_goal_touched():
	_end_game()


func _on_hud_game_started():
	_new_game()
