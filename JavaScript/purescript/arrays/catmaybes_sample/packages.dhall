
let mkPackage =
      https://raw.githubusercontent.com/purescript/package-sets/psc-0.13.0-20190614/src/mkPackage.dhall sha256:0b197efa1d397ace6eb46b243ff2d73a3da5638d8d0ac8473e8e4a8fc528cf57

let upstream =
      https://raw.githubusercontent.com/purescript/package-sets/psc-0.13.0-20190614/src/packages.dhall sha256:5cbf2418298e7de762401c5719c6eb18eda4c67ba512b3f076b50a793a7fc482

let overrides = {=}

let additions = {=}

in  upstream // overrides // additions
