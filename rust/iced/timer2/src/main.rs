use iced::{
    Subscription,
    widget::{Column, button, center, column, text},
};
use std::time::{Duration, Instant};

#[derive(Debug, Clone)]
enum Message {
    Start,
    Stop,
    CheckPoint,
}

#[derive(Default)]
struct Timer {
    instant: Option<Instant>,
    last_elapsed: Option<Duration>,
}

impl Timer {
    fn update(&mut self, message: Message) {
        match message {
            Message::Start => {
                self.instant = Some(Instant::now());
                self.last_elapsed = None;
            }
            Message::Stop => {
                self.instant = None;
            }
            Message::CheckPoint => {
                if let Some(d) = self.instant {
                    self.last_elapsed = Some(d.elapsed());
                }
            }
        }
    }

    fn view(&self) -> Column<'_, Message> {
        let time = self
            .last_elapsed
            .map(|x| format!("{:.2}", x.as_secs_f32()))
            .unwrap_or("-".to_string());

        let btn = if self.instant.is_none() {
            button("START").on_press(Message::Start)
        } else {
            button("STOP").on_press(Message::Stop)
        };

        column!(center(text(time)), center(btn),).padding(30)
    }

    fn subscribe(&self) -> Subscription<Message> {
        iced::time::every(Duration::from_millis(10)).map(|_| Message::CheckPoint)
    }
}

fn main() -> iced::Result {
    iced::application("timer", Timer::update, Timer::view)
        .subscription(Timer::subscribe)
        .window_size((320., 240.))
        .resizable(false)
        .run()
}
