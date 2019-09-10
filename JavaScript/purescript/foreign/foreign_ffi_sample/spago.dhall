{ name =
    "foreign-ffi-sample"
, dependencies =
    [ "console", "effect", "foreign", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
