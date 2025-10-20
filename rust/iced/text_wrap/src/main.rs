use iced::widget::{column, text, text::Wrapping};

#[derive(Debug, Clone)]
enum Message {}

#[derive(Default)]
struct App;

impl App {
    fn view(&self) -> iced::Element<'_, Message> {
        let m1 = "abcdefg1234567";
        let m2 = "test the wrapping strategy";

        column![
            text(m1).size(16),
            text(m1).size(16).width(100),
            text(m1).size(16).width(100).wrapping(Wrapping::None),
            text(m1).size(16).width(100).wrapping(Wrapping::WordOrGlyph),
            text(m1).size(16).width(100).height(20),
            text(m1)
                .size(16)
                .width(100)
                .height(20)
                .wrapping(Wrapping::None),
            text(m1)
                .size(16)
                .width(100)
                .height(20)
                .wrapping(Wrapping::WordOrGlyph),
            text(m2).size(16),
            text(m2).size(16).width(100),
            text(m2).size(16).width(100).wrapping(Wrapping::None),
            text(m2).size(16).width(100).wrapping(Wrapping::WordOrGlyph),
            text(m2).size(16).width(100).height(20),
            text(m2)
                .size(16)
                .width(100)
                .height(20)
                .wrapping(Wrapping::None),
            text(m2)
                .size(16)
                .width(100)
                .height(20)
                .wrapping(Wrapping::WordOrGlyph),
        ]
        .spacing(20)
        .into()
    }

    fn update(&mut self, _msg: Message) {}
}

fn main() -> iced::Result {
    iced::run("app", App::update, App::view)
}
