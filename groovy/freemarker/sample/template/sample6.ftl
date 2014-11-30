<html>
<body>

<#assign w="<div>test</div>" />

<h2>${w}</h2>
<h2>${w?html}</h2>

<h2>escape1</h2>
<#escape x as x?html>
	<div>
		${w}
	</div>
	<div>
		<#noescape>${w}</#noescape>
	</div>

	<#include "footer.ftl" />
</#escape>

</body>
</html>