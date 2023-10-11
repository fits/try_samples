extends Area2D

signal goal_touched

@export var speed = 200

@onready var _sprite = $AnimatedSprite2D
@onready var _colli = $CollisionShape2D

func _ready():
	init()

func _process(delta):
	var v = Vector2.ZERO
	
	if Input.is_action_pressed("ui_up"):
		v.y -= 1
	elif Input.is_action_pressed("ui_down"):
		v.y += 1
	elif Input.is_action_pressed("ui_left"):
		v.x -= 1
	elif Input.is_action_pressed("ui_right"):
		v.x += 1
	
	if v.length() > 0:
		position += v * speed * delta
		_sprite.play()
	else:
		_sprite.stop()

func init():
	hide()
	_disable_collision()

func start(pos):
	position = pos
	show()
	_enable_collision()

func _enable_collision():
	_colli.set_deferred("disabled", false)

func _disable_collision():
	_colli.set_deferred("disabled", true)



func _on_area_entered(area):
	var c_layer = area.get_collision_layer()
	print("entered: name=%s, layer=%d" % [area.name, c_layer])
	
	goal_touched.emit()
