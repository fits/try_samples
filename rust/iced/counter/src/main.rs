use iced::widget::{button, column, row, text, Column};

#[derive(Default, Debug, Clone)]
struct Counter(i32);

#[derive(Debug, Clone)]
enum Message {
    Up(i32),
    Down(i32),
}

impl Counter {
    pub fn view(&self) -> Column<Message> {
        column!(
            text(self.0).size(200),
            row![
                button("2down").on_press(Message::Down(2)),
                button("down").on_press(Message::Down(1)),
                button("up").on_press(Message::Up(1)),
                button("2up").on_press(Message::Up(2)),
            ]
            .spacing(10),
        )
        .spacing(20)
        .padding(30)
    }

    pub fn update(&mut self, msg: Message) {
        match msg {
            Message::Up(n) => self.0 += n,
            Message::Down(n) => self.0 -= n,
        }
    }
}

fn main() -> iced::Result {
    iced::run("counter", Counter::update, Counter::view)
}
