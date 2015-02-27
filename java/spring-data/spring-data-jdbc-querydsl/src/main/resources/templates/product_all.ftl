<!DOCTYPE html>
<html>
<head lang="ja">
    <meta charset="UTF-8">
    <title>product list</title>
</head>
<body>
  <h1>Product List</h1>
  <form method="post" action="/product">
    <input type="number" name="id" />
    <input type="text" name="name" />
    <input type="number" name="price" />
    <input type="submit" value="ADD" />
  </form>

  <ul>
    <#list products as p>
      <li>${p.id}, ${p.name}, ${p.price}, ${p.releaseDate}</li>
    </#list>
  </ul>
</body>
</html>