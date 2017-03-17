
package = "kong-plugin-sample"
version = "0.1.0-1"

supported_platforms = {"linux", "macosx"}

dependencies = {
}

local pluginName = "sample"

build = {
  type = "builtin",
  modules = {
    ["kong.plugins."..pluginName..".handler"] = "kong/plugins/"..pluginName.."/handler.lua",
    ["kong.plugins."..pluginName..".schema"] = "kong/plugins/"..pluginName.."/schema.lua",
  }
}
