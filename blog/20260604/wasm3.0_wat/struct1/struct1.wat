(module
    (type $data (struct (field $id i32) (field $value f32)))

    (func $create (export "create") (param i32 f32) (result (ref $data))
        (struct.new $data (local.get 0) (local.get 1))
    )

    (func $get-id (export "getId") (param (ref null $data)) (result i32)
        (struct.get $data $id (local.get 0))
    )

    (func $get-value (export "getValue") (param (ref null $data)) (result f32)
        (struct.get $data $value (local.get 0))
    )
)