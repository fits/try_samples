
{ name =
    "sample"
, dependencies =
    [ "console", "effect", "free", "maybe", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
