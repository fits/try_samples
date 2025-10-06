use iced::widget::{center, text};

#[derive(Debug)]
enum Message {}

#[derive(Default)]
struct App;

impl App {
    fn view(&self) -> iced::Element<'_, Message> {
        center(text("test app").size(50)).into()
    }

    fn update(&mut self, _msg: Message) {}
}

fn main() -> iced::Result {
    iced::run("app", App::update, App::view)
}
