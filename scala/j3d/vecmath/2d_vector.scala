import javax.vecmath._

val v1 = new Vector2d(3, 0)
val v2 = new Vector2d(2, 2)

println("長さ: " + v1.length)
println("長さ（2乗）: " + v1.lengthSquared)

//内積
println("内積: " + v1.dot(v2))

//角度（ベクトルのなす角）
println("ベクトル間の角度（radian）: " + v1.angle(v2))



