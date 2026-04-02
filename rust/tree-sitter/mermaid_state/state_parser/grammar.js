
export default grammar({
  name: "state_parser",

  rules: {
    diagram: $ => seq('stateDiagram-v2', repeat1($.node)),
    node: $ => choice($.state, $.transition),
    transition: $ => seq($.state, '-->', $.state),
    state: _ => choice('[*]', /\w+/),
  }
});
