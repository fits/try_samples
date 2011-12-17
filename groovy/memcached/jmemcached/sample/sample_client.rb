require 'rubygems'
require 'memcache'

mcache = MemCache::new("localhost:11211", :debug => true)

mcache.set("a1", "test data")

puts mcache.get("a1")
