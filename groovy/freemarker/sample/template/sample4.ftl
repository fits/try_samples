<html>
<body>

<#macro product name>
	<div>${name}</div>
</#macro>

<#macro products items type="A">
	<#list items as item>
		<div>${item.name} : ${type}</div>
	</#list>
</#macro>

<#macro products2 items type="A">
	<h3><#nested /></h3>
	<@products items />
</#macro>

<#macro products3 items type="A">
	<#list items as it>
		<#nested it.id it.name />
	</#list>
</#macro>


<h2>sample1</h2>
<#list items as item >
	<@product item.name />
</#list>

<h2>sample2</h2>
<@products items />

<h2>sample3</h2>
<@products items "B" />

<h2>sample4</h2>
<@products type="B" items=items />

<h2>sample5</h2>
<@products2 items "C">test</@products2>

<h2>sample6</h2>
<@products3 items "C" ; id, name>
	${id} - ${name} <br/>
</@products3>

</body>
</html>