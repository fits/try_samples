(component
    (core module $A
        (func (export "one") (result i32) (i32.const 1))
    )
    (core instance $a (instantiate $A))
    (func $f (result s32) (canon lift (core func $a "one")))
    (export "f" (func $f))
)