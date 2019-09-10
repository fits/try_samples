{ name =
    "sqlite3-sample"
, dependencies =
    [ "console", "effect", "node-sqlite3", "psci-support" ]
, packages =
    ./packages.dhall
, sources =
    [ "src/**/*.purs" ]
}
