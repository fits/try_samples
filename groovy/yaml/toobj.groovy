import org.ho.yaml.*

//YAML
str = """
- ƒeƒXƒg
- 100
- test: 1
  name: "abc"
"""

obj = Yaml.load(str)
println obj

