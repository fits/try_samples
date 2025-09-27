use iced::{
    Subscription,
    widget::{Column, center, column, text},
};
use std::time::{Duration, Instant};

#[derive(Debug, Clone)]
enum Message {
    Show,
}

struct Timer {
    instant: Instant,
    last_elapsed: Option<Duration>,
}

impl Timer {
    fn update(&mut self, message: Message) {
        match message {
            Message::Show => {
                self.last_elapsed = Some(self.instant.elapsed());
            }
        }
    }

    fn view(&self) -> Column<'_, Message> {
        let time = self
            .last_elapsed
            .map(|x| format!("{:.1}", x.as_secs_f32()))
            .unwrap_or("-".to_string());

        column!(center(text(time)),).padding(30)
    }

    fn subscribe(&self) -> Subscription<Message> {
        iced::time::every(Duration::from_millis(100)).map(|_| Message::Show)
    }
}

impl Default for Timer {
    fn default() -> Self {
        Self {
            instant: Instant::now(),
            last_elapsed: None,
        }
    }
}

fn main() -> iced::Result {
    iced::application("timer", Timer::update, Timer::view)
        .subscription(Timer::subscribe)
        .window_size((320., 240.))
        .run()
}
