import jwt

res = jwt.encode({'user': 'u1'}, 'secret', algorithm = 'HS256')
print(res)
