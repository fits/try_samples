(module
    (tag $verr (export "valueError") (param i32))

    (func $check (export "checkValue") (param i32)
        (if (i32.lt_s (local.get 0) (i32.const 5))
            (then (throw $verr (local.get 0)))
        )
    )
)