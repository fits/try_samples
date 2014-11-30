<#import "func.ftl" as f />
<html>
<body>

<div>
	${name}, ${value}
</div>

<h2>items</h2>
<div>
	<ul>
		<#list items as item>
		${item.name}
		</#list>
	</ul>
</div>

<#--
<div>note1: ${note}</div>
-->

<div>null note1: ${note!"aaa"}</div>
<div>null note2: ${note!}</div>

<#if note??><div>null note3: ${note}</div></#if>

<#include "footer.ftl" />
%%%
<#import "footer.ftl" as fo />

</body>
</html>