{ name =
    "httpure-sample"
, dependencies =
    [ "console", "effect", "httpure", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
