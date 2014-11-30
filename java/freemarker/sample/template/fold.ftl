
<#function fold xs value proc>
	<#local result = value />
	<#list xs as x>
		<#local result = proc(result, x) />
	</#list>
	<#return result />
</#function>
