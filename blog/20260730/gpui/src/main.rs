use gpui::{AppContext, Application, ParentElement, Render, Styled, WindowOptions, div, rems};
use std::env;

struct SimpleView(String);

impl Render for SimpleView {
    fn render(
        &mut self,
        _window: &mut gpui::Window,
        _ctx: &mut gpui::prelude::Context<Self>,
    ) -> impl gpui::prelude::IntoElement {
        div()
            .size_full()
            .bg(gpui::white())
            .text_center()
            .text_size(rems(7.))
            .child(self.0.clone())
    }
}

fn main() {
    let text = env::args()
        .skip(1)
        .next()
        .unwrap_or("GPUI Test View".into());

    Application::new().run(|app| {
        app.open_window(WindowOptions::default(), |_, app| {
            app.new(|_| SimpleView(text))
        })
        .unwrap();

        app.activate(true);
    });
}
