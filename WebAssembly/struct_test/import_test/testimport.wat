(module
    (import "dt" "now" (func $now (result anyref)))

    (type $data (struct (field $id i32) (field $date anyref)))

    (func $create (export "create") (param i32) (result (ref $data))
        (struct.new $data (local.get 0) (call $now))
    )

    (func $get-id (export "getId") (param (ref null $data)) (result i32)
        (struct.get $data $id (local.get 0))
    )

    (func $get-date (export "getDate") (param (ref null $data)) (result anyref)
        (struct.get $data $date (local.get 0))
    )
)