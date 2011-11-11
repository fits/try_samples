<table>
	<tr>
		<td>テストデータ</td>
	</tr>
</table>

<table>
	<tr>
		<th>${data.name}</th>
	</tr>

	<% data.itemList.each { %>
	<tr>
		<td>${it.itemName}</td>
	</tr>
	<% } %>
</table>

