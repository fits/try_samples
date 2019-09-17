{ name =
    "my-project"
, dependencies =
    [ "console", "effect", "foreign-generic", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
