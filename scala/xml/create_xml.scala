
val index = "2"
val num = 100

val data =
<root>
	<sub id="1">
		<item>{num * 5}</item>
	</sub>
	<sub id={index} category="test">
	</sub>
</root>;

println(data)

