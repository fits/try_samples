return {
  no_consumer = false,
  fields = {
    title = { type = "string", default = "" }
  },
  self_check = function(schema, plugin_t, dao, is_updating)
    return true
  end
}
