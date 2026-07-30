use gpui::prelude::*;
use gpui::{
    Application, Bounds, MouseButton, MouseDownEvent, Render, Window, WindowBounds, WindowOptions,
    div, px, rgb, size, white,
};

struct Counter {
    count: isize,
}

impl Counter {
    fn up(&mut self, _event: &MouseDownEvent, _window: &mut Window, ctx: &mut Context<Self>) {
        self.count += 1;
        ctx.notify();
    }

    fn down(&mut self, _event: &MouseDownEvent, _window: &mut Window, ctx: &mut Context<Self>) {
        self.count -= 1;
        ctx.notify();
    }
}

impl Render for Counter {
    fn render(&mut self, _window: &mut Window, ctx: &mut Context<Self>) -> impl IntoElement {
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
                    .on_mouse_down(MouseButton::Left, ctx.listener(Self::up))
                    .on_mouse_down(MouseButton::Right, ctx.listener(Self::down)),
            )
    }
}

fn main() {
    Application::new().run(|app| {
        let bounds = Bounds::centered(None, size(px(500.), px(300.)), app);

        let opts = WindowOptions {
            window_bounds: Some(WindowBounds::Windowed(bounds)),
            ..Default::default()
        };

        app.open_window(opts, |_, app| app.new(|_| Counter { count: 1 }))
            .unwrap();

        app.activate(true);
    });
}
