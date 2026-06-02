(module
    (tag $ex1 (export "ex1") (param i32))

    (func $check (export "check") (param i32)
        (if (i32.lt_s (local.get 0) (i32.const 5))
            (then (throw $ex1 (local.get 0)))
        )
    )
)