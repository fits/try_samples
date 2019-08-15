
let mkPackage =
      https://raw.githubusercontent.com/purescript/package-sets/psc-0.13.2-20190804/src/mkPackage.dhall sha256:0b197efa1d397ace6eb46b243ff2d73a3da5638d8d0ac8473e8e4a8fc528cf57

let upstream =
      https://raw.githubusercontent.com/purescript/package-sets/psc-0.13.2-20190804/src/packages.dhall sha256:2230fc547841b54bca815eb0058414aa03ed7b675042f8b3dda644e1952824e5

let overrides = {=}

let additions = {=}

in  upstream // overrides // additions
