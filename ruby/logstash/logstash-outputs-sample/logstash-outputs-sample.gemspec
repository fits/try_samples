# coding: utf-8

Gem::Specification.new do |spec|
  spec.name          = "logstash-outputs-sample"
  spec.version       = "0.0.1"
  spec.authors       = [""]
  spec.email         = [""]

  spec.summary       = %q{logstash outputs plugin sample.}
  spec.description   = %q{}

  spec.files = Dir['lib/**/*','spec/**/*','vendor/**/*','*.gemspec','*.md','Gemfile']

  spec.require_paths = ["lib"]

  spec.metadata = { "logstash_plugin" => "true", "logstash_group" => "output" }

  spec.add_runtime_dependency "logstash-core", ">= 2.2.0", "< 3.0.0"
  spec.add_runtime_dependency 'logstash-codec-line'

  spec.add_development_dependency 'logstash-devutils'
  spec.add_development_dependency "bundler", "~> 1.11"
  spec.add_development_dependency "rake", "~> 10.0"
  spec.add_development_dependency "rspec", "~> 3.0"
end
