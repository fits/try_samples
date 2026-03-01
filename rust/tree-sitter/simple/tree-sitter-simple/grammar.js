/**
 * @file Simple grammar for tree-sitter
 * @author fits
 * @license MIT
 */

/// <reference types="tree-sitter-cli/dsl" />
// @ts-check

export default grammar({
  name: "simple",

  rules: {
    greeting: $ => seq("hello", optional($.name)),
    name: $ => /[a-zA-Z0-9_]+/,
  }
});
