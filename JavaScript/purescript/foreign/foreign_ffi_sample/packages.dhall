
let upstream =
      https://github.com/purescript/package-sets/releases/download/psc-0.13.2-20190725/packages.dhall sha256:60cc03d2c3a99a0e5eeebb16a22aac219fa76fe6a1686e8c2bd7a11872527ea3

let overrides = {=}

let additions = {=}

in  upstream // overrides // additions
