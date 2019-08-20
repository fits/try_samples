{ name =
    "records-sample"
, dependencies =
    [ "effect", "console", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
