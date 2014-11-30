<#import "fold.ftl" as f />

<#function add x y>
	<#return x + y />
</#function>

<#function subtract x y>
	<#return x - y />
</#function>

res1 = ${f.fold([1, 5, 6, 11], 100, add)}
res2 = ${f.fold([1, 5, 6, 11], 100, subtract)}
