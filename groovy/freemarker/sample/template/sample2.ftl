<#import "func.ftl" as f />
<html>
<body>

<h2>include, import</h2>
<div>
<#include "footer.ftl" />
%%%
<#import "footer.ftl" as fo />
</div>

<div>
a append b = ${f.append("a", "b")}
</div>

<div>
name append !!! = ${f.append(name, "!!!")}
</div>

</body>
</html>