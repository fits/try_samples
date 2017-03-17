
local BasePlugin = require "kong.plugins.base_plugin"
local SampleHandler = BasePlugin:extend()

function SampleHandler:new()
  SampleHandler.super.new(self, "sample")
end

function SampleHandler:access(config)
  SampleHandler.super.access(self)

  print("***** sample access: "..config.title)

  ngx.header["Sample"] = config.title
end

return SampleHandler
