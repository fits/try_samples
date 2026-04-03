
export default grammar({
  name: "state_diagram",

  rules: {
    diagram: $ => seq('stateDiagram-v2', repeat1($.node)),
    node: $ => choice($.state, $.transition),
    transition: $ => seq($.state, '-->', $.state),
    state: $ => choice($.start_end, $.identifier),
    start_end: _ => '[*]',
    identifier: _ => /\w+/,
  }
});
