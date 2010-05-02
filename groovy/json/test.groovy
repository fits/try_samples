import grails.converters.JSON

def t = ["name": "テストデータ", "point": 100]

println new JSON(t)

