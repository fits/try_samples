<#assign x = 10543>
<#assign y = 7654321.1234567>
<#assign z = 0.1234567>

#x = #{x}
#y = #{y}
#z = #{z}

(1) default

$x = ${x}
$y = ${y}
$z = ${z}

<#setting number_format="0.#">
(2) 0.#

$x = ${x}
$y = ${y}
$z = ${z}

<#setting number_format="0.##">
(3) 0.##

$x = ${x}
$y = ${y}
$z = ${z}

<#setting number_format="#,##0.###">
(4) #,##0.###

$x = ${x}
$y = ${y}
$z = ${z}
