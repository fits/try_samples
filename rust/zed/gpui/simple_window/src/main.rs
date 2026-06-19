#[allow(unused_imports)]
use gpui::prelude::*;
use gpui::{AppContext, Application, ParentElement, Render, Styled, WindowOptions, div, rems};

struct CustomView;

impl Render for CustomView {
    fn render(
        &mut self,
        _window: &mut gpui::Window,
        _cx: &mut gpui::prelude::Context<Self>,
    ) -> impl gpui::prelude::IntoElement {
        div()
            .size_full()
            .bg(gpui::white())
            .text_center()
            .text_size(rems(5.))
            .child("Zed GPUI Test View")
    }
}

fn main() {
    let app = Application::new();

    app.run(|ctx| {
        ctx.open_window(WindowOptions::default(), |_, c| c.new(|_| CustomView {}))
            .unwrap();

        ctx.activate(true);
    });
}
