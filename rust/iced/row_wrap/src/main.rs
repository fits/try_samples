use iced::widget::{row, text};

#[derive(Debug, Clone)]
enum Message {}

#[derive(Default)]
struct App;

impl App {
    fn view(&self) -> iced::Element<'_, Message> {
        row![
            text("item-1").size(50),
            text("item-2").size(50),
            text("item-3").size(50),
            text("item-4").size(50),
            text("item-5").size(70),
            text("item-6").size(30),
            text("item-7").size(50),
            text("item-8").size(50),
            text("item-9").size(50),
        ]
        .padding(10)
        .spacing(30)
        .wrap()
        .into()
    }

    fn update(&mut self, _msg: Message) {}
}

fn main() -> iced::Result {
    iced::run("app", App::update, App::view)
}
