extends Area2D


func _ready():
	init()

func init():
	hide()

func start(pos):
	position = pos
	show()
