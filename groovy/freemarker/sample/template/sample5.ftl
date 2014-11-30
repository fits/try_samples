<html>
<body>

<#macro pre name>
	<#local prefix="sample:" />
	<#assign y="46" />
	<div>${prefix}${name}${y}</div>
</#macro>

<#assign x="123" />

<@pre x />
<@pre "abc" />

${y}
${prefix!}

</body>
</html>