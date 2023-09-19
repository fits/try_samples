(component
  (core module $A
    (func (export "calc") (param i32 i32) (result i32)
      local.get 0
      local.get 1
      i32.add
      i32.const 2
      i32.mul
    )
  )
  (core instance $a (instantiate $A))
  (func $f (param "x" s32) (param "y" s32) (result s32) (canon lift (core func $a "calc")))
  (export "f" (func $f))
)