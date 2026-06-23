use gpui::prelude::*;
use gpui::{
    Application, Bounds, MouseButton, MouseDownEvent, Render, Window, WindowBounds, WindowOptions,
    div, px, size, rgb, white,
};

struct Counter {
    count: usize,
}

impl Counter {
    fn count_up(&mut self, _event: &MouseDownEvent, _window: &mut Window, cx: &mut Context<Self>) {
        self.count += 1;

        cx.notify();
    }
}

impl Render for Counter {
    fn render(&mut self, _window: &mut Window, cx: &mut Context<Self>) -> impl IntoElement {
        div()
            .size_full()
            .flex()
            .flex_col()
            .p_10()
            .bg(white())
            .justify_center()
            .child("Counter:")
            .child(
                div()
                    .bg(rgb(0xAAEEAA))
                    .text_center()
                    .text_3xl()
                    .child(format!("{}", self.count))
                    .on_mouse_down(MouseButton::Left, cx.listener(Self::count_up)),
            )
    }
}

fn main() {
    let app = Application::new();

    app.run(|cx| {
        let bounds = Bounds::centered(None, size(px(500.), px(300.)), cx);

        let opts = WindowOptions {
            window_bounds: Some(WindowBounds::Windowed(bounds)),
            ..Default::default()
        };

        cx.open_window(opts, |_, cx| cx.new(|_| Counter { count: 1 }))
            .unwrap();

        cx.activate(true);
    });
}
