{ name =
    "my-project"
, dependencies =
    [ "console", "effect", "hyper", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
