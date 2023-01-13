
use chrono::prelude::*;
use serde::{Deserialize, Serialize};
use std::fmt;

pub type TrackingId = String;
pub type UnLocode = String;
pub type VoyageNo = String;
pub type Date = DateTime<Local>;

#[derive(Debug, Clone, Deserialize, Serialize)]
pub enum Event {
    Created { tracking_id: TrackingId },
    Received { tracking_id: TrackingId, location: UnLocode, completion_time: Date },
    Loaded { tracking_id: TrackingId, voyage_no: VoyageNo, location: UnLocode, completion_time: Date },
    Unloaded { tracking_id: TrackingId, voyage_no: VoyageNo, location: UnLocode, completion_time: Date },
    Claimed { tracking_id: TrackingId, completion_time: Date },
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub enum Delivery {
    Nothing,
    NotReceived { tracking_id: TrackingId },
    InPort { tracking_id: TrackingId, location: UnLocode },
    OnBoardCarrier { tracking_id: TrackingId, voyage_no: VoyageNo, location: UnLocode },
    Claimed { tracking_id: TrackingId, location: UnLocode, claimed_time: Date },
}

#[derive(Debug, Clone)]
pub enum Command {
    Create(TrackingId),
    Receive(UnLocode, Date),
    Load(VoyageNo, Date),
    Unload(UnLocode, Date),
    Claim(Date),
}

#[derive(Debug, Clone)]
pub enum CommandError {
    InvalidState,
}

impl fmt::Display for CommandError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::InvalidState => write!(f, "invalid state"),
        }
    }
}

impl std::error::Error for CommandError {}

pub type DeliveryResult<T> = std::result::Result<T, CommandError>;

impl Delivery {
    pub fn action(&self, cmd: &Command) -> DeliveryResult<(Self, Event)> {
        match self {
            Self::Nothing => match cmd {
                Command::Create(t) => {
                    Ok((
                        Self::NotReceived { tracking_id: t.clone() },
                        Event::Created { tracking_id: t.clone() }
                    ))
                }
                _ => invalid_state()
            }
            Self::NotReceived { tracking_id } => match cmd {
                Command::Receive(l, d) => {
                    Ok((
                        Self::InPort { tracking_id: tracking_id.clone(), location: l.clone() },
                        Event::Received { tracking_id: tracking_id.clone(), location: l.clone(), completion_time: d.clone() }
                    ))
                }
                _ => invalid_state()
            }
            Self::InPort { tracking_id, location } => match cmd {
                Command::Load(v, d) => {
                    Ok((
                        Self::OnBoardCarrier { tracking_id: tracking_id.clone(), voyage_no: v.clone(), location: location.clone() },
                        Event::Loaded { tracking_id: tracking_id.clone(), voyage_no: v.clone(), location: location.clone(), completion_time: d.clone() }
                    ))
                }
                Command::Claim(d) => {
                    Ok((
                        Self::Claimed { tracking_id: tracking_id.clone(), location: location.clone(), claimed_time: d.clone() },
                        Event::Claimed { tracking_id: tracking_id.clone(), completion_time: d.clone() }
                    ))
                }
                _ => invalid_state()
            }
            Self::OnBoardCarrier { tracking_id, voyage_no, .. } => match cmd {
                Command::Unload(l, d) => {
                    Ok((
                        Self::InPort { tracking_id: tracking_id.clone(), location: l.clone() },
                        Event::Unloaded { tracking_id: tracking_id.clone(), voyage_no: voyage_no.clone(), location: l.clone(), completion_time: d.clone() }
                    ))
                }
                _ => invalid_state()
            }
            Self::Claimed { .. } => invalid_state()
        }
    }
}

fn invalid_state<T>() -> DeliveryResult<T> {
    Err(CommandError::InvalidState)
}
