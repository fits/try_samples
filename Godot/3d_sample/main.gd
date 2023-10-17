extends Node

@onready var player = $Player

func _ready():
	player.position = Vector3(0, 3, 3)
