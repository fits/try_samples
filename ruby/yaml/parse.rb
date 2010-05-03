require 'yaml'

str = <<EOS
test:
    - name: test
      point: 10
check:
    - name: ƒeƒXƒg
      point: 1
EOS

p YAML::load(str)
