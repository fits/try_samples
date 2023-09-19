(module
  (func (export "calc") (param i32 i32) (result i32)
    local.get 0
    local.get 1
    i32.add
    i32.const 2
    i32.mul
  )
)
