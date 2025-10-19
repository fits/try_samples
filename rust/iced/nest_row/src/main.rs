use iced::{
    Alignment::Center,
    widget::{column, row, text},
};

#[derive(Debug, Clone)]
enum Message {}

#[derive(Default)]
struct App;

impl App {
    fn view(&self) -> iced::Element<'_, Message> {
        row![
            item("item-1", 1000),
            item("item-2", 200),
            item("item-3", 30000),
            item("item-43005", 440),
            item("item-5", 55005),
            item("item-6", 6),
            item("item-71000", 7189000),
        ]
        .padding(10)
        .spacing(20)
        .wrap()
        .into()
    }

    fn update(&mut self, _msg: Message) {}
}

fn item<'a>(name: &'a str, price: u64) -> iced::Element<'a, Message> {
    let r1 = row![text("NAME:").size(20), text(name).size(16),]
        .spacing(5)
        .align_y(Center);

    let r2 = row![text("PRICE:").size(20), text!("{price}").size(16)]
        .spacing(5)
        .align_y(Center);

    column![r1, r2].width(140).height(60).into()
}

fn main() -> iced::Result {
    iced::run("app", App::update, App::view)
}
