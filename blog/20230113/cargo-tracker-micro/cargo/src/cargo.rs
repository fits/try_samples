
use chrono::prelude::*;
use serde::{Deserialize, Serialize};
use std::fmt;

pub type TrackingId = String;
pub type UnLocode = String;
pub type VoyageNo = String;
pub type Date = DateTime<Local>;

#[derive(Default, Debug, Clone, PartialEq, Deserialize, Serialize)]
pub struct RouteSpec {
    pub origin: UnLocode,
    pub destination: UnLocode,
    pub deadline: Date,
}

#[derive(Debug, Clone, PartialEq, Deserialize, Serialize)]
pub struct Itinerary(pub Vec<Leg>);

#[derive(Debug, Clone, PartialEq, Deserialize, Serialize)]
pub struct LocationTime {
    pub location: UnLocode, 
    pub time: Date,
}

#[derive(Debug, Clone, PartialEq, Deserialize, Serialize)]
pub struct Leg {
    pub voyage_no: VoyageNo,
    pub load: LocationTime,
    pub unload: LocationTime,
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub enum Cargo {
    Nothing,
    Unrouted { tracking_id: TrackingId, route_spec: RouteSpec },
    Routed { tracking_id: TrackingId, route_spec: RouteSpec, itinerary: Itinerary },
    Misrouted { tracking_id: TrackingId, route_spec: RouteSpec, itinerary: Itinerary },
    Closed { tracking_id: TrackingId, route_spec: RouteSpec, itinerary: Itinerary },
}

#[derive(Debug, Clone, Deserialize, Serialize)]
pub enum Event {
    Created { tracking_id: TrackingId, route_spec: RouteSpec },
    AssignedRoute { tracking_id: TrackingId, itinerary: Itinerary },
    ChangedDestination { tracking_id: TrackingId, destination: UnLocode },
    ChangedDeadline { tracking_id: TrackingId, deadline: Date },
    Closed { tracking_id: TrackingId },
}

#[derive(Debug, Clone)]
pub enum Command {
    Create(TrackingId, RouteSpec),
    AssignRoute(Itinerary),
    ChangeDestination(UnLocode),
    ChangeDeadline(Date),
    Close,
}

#[derive(Debug, Clone)]
pub enum CommandError {
    InvalidState,
    EmptyItinerary,
    PastDeadline,
    NoChange(&'static str),
}

impl fmt::Display for CommandError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            Self::InvalidState => write!(f, "invalid state"),
            Self::EmptyItinerary => write!(f, "itinerary is empty"),
            Self::PastDeadline => write!(f, "deadline is past"),
            Self::NoChange(name) => write!(f, "no change {}", name),
        }
    }
}

impl std::error::Error for CommandError {}

pub type CargoResult<T> = std::result::Result<T, CommandError>;

impl Cargo {
    pub fn action(&self, cmd: &Command) -> CargoResult<(Self, Event)> {
        match self {
            Self::Nothing => match cmd {
                Command::Create(t, r) => 
                    if r.deadline <= now() {
                        past_deadline()
                    } else {
                        Ok((
                            Self::Unrouted { tracking_id: t.clone(), route_spec: r.clone() },
                            Event::Created { tracking_id: t.clone(), route_spec: r.clone() }
                        ))
                    }
                _ => invalid_state()
            }
            Self::Unrouted { tracking_id, route_spec } => match cmd {
                Command::AssignRoute(it) =>
                    if it.0.is_empty() {
                        empty_itinerary()
                    } else {
                        Ok((
                            create_routed(
                                tracking_id.clone(), 
                                route_spec.clone(), 
                                it.clone(),
                            ),
                            Event::AssignedRoute {
                                tracking_id: tracking_id.clone(), 
                                itinerary: it.clone(),
                            }
                        ))
                    }
                Command::ChangeDestination(l) =>
                    if l == &route_spec.destination {
                        no_change("destination")
                    } else {
                        Ok((
                            Self::Unrouted {
                                tracking_id: tracking_id.clone(), 
                                route_spec: RouteSpec { destination: l.clone(), ..route_spec.clone() }
                            },
                            Event::ChangedDestination { 
                                tracking_id: tracking_id.clone(), 
                                destination: l.clone()
                            }
                        ))
                    }
                Command::ChangeDeadline(d) => 
                    if d == &route_spec.deadline {
                        no_change("deadline")
                    } else if d <= &now() {
                        past_deadline()
                    } else {
                        Ok((
                            Self::Unrouted {
                                tracking_id: tracking_id.clone(), 
                                route_spec: RouteSpec { deadline: d.clone(), ..route_spec.clone() }
                            },
                            Event::ChangedDeadline { 
                                tracking_id: tracking_id.clone(), 
                                deadline: d.clone()
                            }
                        ))
                    }
                _ => invalid_state()
            }
            Self::Routed { tracking_id, route_spec, itinerary } | 
            Self::Misrouted { tracking_id, route_spec, itinerary } => match cmd {
                Command::AssignRoute(it) =>
                    if it.0.is_empty() {
                        empty_itinerary()
                    } else if it == itinerary {
                        no_change("itinerary")
                    } else {
                        Ok((
                            create_routed(
                                tracking_id.clone(), 
                                route_spec.clone(), 
                                it.clone(),
                            ),
                            Event::AssignedRoute {
                                tracking_id: tracking_id.clone(), 
                                itinerary: it.clone(),
                            }
                        ))
                    }
                Command::ChangeDestination(l) =>
                    if l == &route_spec.destination {
                        no_change("destination")
                    } else {
                        Ok((
                            create_routed(
                                tracking_id.clone(), 
                                RouteSpec { destination: l.clone(), ..route_spec.clone() }, 
                                itinerary.clone()
                            ),
                            Event::ChangedDestination { 
                                tracking_id: tracking_id.clone(), 
                                destination: l.clone()
                            }
                        ))
                    }
                Command::ChangeDeadline(d) => 
                    if d == &route_spec.deadline {
                        no_change("deadline")
                    } else if d <= &now() {
                        past_deadline()
                    } else {
                        Ok((
                            create_routed(
                                tracking_id.clone(), 
                                RouteSpec { deadline: d.clone(), ..route_spec.clone() }, 
                                itinerary.clone()
                            ),
                            Event::ChangedDeadline { 
                                tracking_id: tracking_id.clone(), 
                                deadline: d.clone()
                            }
                        ))
                    }
                Command::Close => {
                    Ok((
                        Cargo::Closed {
                            tracking_id: tracking_id.clone(), 
                            route_spec: route_spec.clone(), 
                            itinerary: itinerary.clone()
                        },
                        Event::Closed { tracking_id: tracking_id.clone() }
                    ))
                }
                _ => invalid_state()
            }
            Self::Closed { .. } => invalid_state()
        }
    }

    pub fn is_destination(&self, location: UnLocode) -> bool {
        match self {
            Self::Nothing =>
                false,
            Self::Unrouted { route_spec, .. } =>
                route_spec.destination == location,
            Self::Routed { route_spec, itinerary, .. } |
            Self::Misrouted { route_spec, itinerary, .. } |
            Self::Closed { route_spec, itinerary, .. } =>
                route_spec.destination == location || 
                    itinerary.0.last().map_or_else(|| false, |l| l.unload.location == location)
        }
    }

    pub fn is_on_route(&self, location: UnLocode, voyage_no: Option<VoyageNo>) -> Option<bool> {
        match self {
            Self::Routed { itinerary, .. } |
            Self::Misrouted { itinerary, .. } => {
                let r = itinerary.0
                    .iter()
                    .any(|l| 
                        voyage_no.as_ref().map_or_else(|| true, |v| v == &l.voyage_no) &&
                        (l.load.location == location || l.unload.location == location)
                    );

                Some(r)
            }
            _ => None
        }
    }
}

fn now() -> Date {
    Local::now()
}

fn is_satisfied_with_route(route_spec: &RouteSpec, itinerary: &Itinerary) -> bool {
    let fst_d: Date = Date::MIN_UTC.into();
    let fst = Some((&route_spec.origin, &fst_d));

    let lst = itinerary.0.iter().fold(fst, |acc, x| 
        acc.and_then(|(l, d)|
            if l == &x.load.location && d <= &x.load.time {
                Some((&x.unload.location, &x.unload.time))
            } else {
                None
            }
        )
    );

    lst.map_or(false, |(l, d)| 
        l == &route_spec.destination && d <= &route_spec.deadline
    )
}

fn create_routed(tracking_id: TrackingId, route_spec: RouteSpec, itinerary: Itinerary) -> Cargo {
    if is_satisfied_with_route(&route_spec, &itinerary) {
        Cargo::Routed {
            tracking_id, 
            route_spec, 
            itinerary,
        }
    } else {
        Cargo::Misrouted {
            tracking_id, 
            route_spec, 
            itinerary,
        }
    }
}

fn invalid_state<T>() -> CargoResult<T> {
    Err(CommandError::InvalidState)
}

fn past_deadline<T>() -> CargoResult<T> {
    Err(CommandError::PastDeadline)
}

fn empty_itinerary<T>() -> CargoResult<T> {
    Err(CommandError::EmptyItinerary)
}

fn no_change<T>(name: &'static str) -> CargoResult<T> {
    Err(CommandError::NoChange(name))
}


#[cfg(test)]
mod tests {
    use super::*;
    use chrono::Days;

    fn next_day(d: u64) -> Date {
        Local::now().checked_add_days(Days::new(d)).unwrap()
    }

    fn test_route_spec(deadline: Date) -> RouteSpec {
        RouteSpec { 
            origin: "loc1".to_string(), 
            destination: "loc2".to_string(), 
            deadline,
        }      
    }

    fn test_itinerary() -> Itinerary {
        Itinerary(
            vec![
                Leg {
                    voyage_no: "v1".to_string(), 
                    load: LocationTime { location: "loc1".to_string(), time: next_day(1) },
                    unload: LocationTime { location: "locA".to_string(), time: next_day(2) },
                },
                Leg {
                    voyage_no: "v2".to_string(), 
                    load: LocationTime { location: "locA".to_string(), time: next_day(3) },
                    unload: LocationTime { location: "loc2".to_string(), time: next_day(4) },
                },
            ],
        )
    }

    #[test]
    fn create_nothing() {
        let d = next_day(5);
        let rs = test_route_spec(d.clone());
        
        let cmd = Command::Create("t1".to_string(), rs.clone());

        if let Ok((s, e)) = Cargo::Nothing.action(&cmd) {
            if let Cargo::Unrouted { tracking_id, route_spec } = s {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(route_spec, rs);
            } else {
                assert!(false);
            }

            if let Event::Created { tracking_id, route_spec } = e {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(route_spec, rs);
            } else {
                assert!(false);
            }

        } else {
            assert!(false);
        }
    }

    #[test]
    fn create_nothing_with_past() {
        let rs = test_route_spec(Local::now());
        
        let cmd = Command::Create("t1".to_string(), rs);

        assert!(Cargo::Nothing.action(&cmd).is_err());
    }

    #[test]
    fn create_unrouted() {
        let rs = test_route_spec(next_day(5));
        
        let cmd = Command::Create("t1".to_string(), rs.clone());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn assgin_unrouted() {
        let rs = test_route_spec(next_day(5));
        let it = test_itinerary();
        
        let cmd = Command::AssignRoute(it.clone());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Routed { tracking_id, route_spec, itinerary } = s {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(route_spec, rs);
                assert_eq!(itinerary, it);
            } else {
                assert!(false);
            }

            if let Event::AssignedRoute { tracking_id, itinerary } = e {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(itinerary, it);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn assgin_unrouted_with_empty_leg() {
        let rs = test_route_spec(next_day(5));
        
        let cmd = Command::AssignRoute(Itinerary(vec![]));

        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn assgin_unrouted_with_other_location_route() {
        let rs = test_route_spec(next_day(5));
        let it = Itinerary(
            vec![
                Leg {
                    voyage_no: "v1".to_string(), 
                    load: LocationTime { location: "loc1".to_string(), time: next_day(1) },
                    unload: LocationTime { location: "loc9".to_string(), time: next_day(2) },
                },
            ],
        );

        let cmd = Command::AssignRoute(it.clone());

        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Misrouted { tracking_id, route_spec, itinerary } = s {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(route_spec, rs);
                assert_eq!(itinerary, it);
            } else {
                assert!(false);
            }

            if let Event::AssignedRoute { .. } = e {
                assert!(true);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn assgin_unrouted_with_past_deadline() {
        let rs = test_route_spec(next_day(2));
        
        let cmd = Command::AssignRoute(test_itinerary());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs };

        if let Ok((s, _e)) = state.action(&cmd) {
            if let Cargo::Misrouted { .. } = s {
                assert!(true);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn reassgin_routed() {
        let rs = test_route_spec(next_day(5));
        
        let it = Itinerary(
            vec![
                test_itinerary().0, 
                vec![Leg {
                    voyage_no: "v3".to_string(), 
                    load: LocationTime { location: "loc2".to_string(), time: next_day(5) },
                    unload: LocationTime { location: "loc3".to_string(), time: next_day(6) },
                }],
            ].concat()
        );

        let cmd = Command::AssignRoute(it.clone());
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs, itinerary: test_itinerary() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Misrouted { .. } = s {
                assert!(true);
            } else {
                assert!(false);
            }

            if let Event::AssignedRoute { itinerary, .. } = e {
                assert_eq!(itinerary, it);
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn reassgin_routed_same_itinerary() {
        let rs = test_route_spec(next_day(5));
        let it = test_itinerary();

        let cmd = Command::AssignRoute(it.clone());
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs, itinerary: it.clone() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn close_routed() {
        let rs = test_route_spec(next_day(5));

        let cmd = Command::Close;
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs, itinerary: test_itinerary() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Closed { .. } = s {
                assert!(true);
            } else {
                assert!(false);
            }

            if let Event::Closed { tracking_id }  = e {
                assert_eq!(tracking_id, "t1");
            } else {
                assert!(false);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_destination_unrouted() {
        let rs = test_route_spec(next_day(2));
        
        let cmd = Command::ChangeDestination("loc9".to_string());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Unrouted { route_spec, .. } = s {
                assert_eq!(route_spec.destination, "loc9".to_string());
                assert_eq!(route_spec.origin, rs.origin);
                assert_eq!(route_spec.deadline, rs.deadline);
            } else {
                assert!(false);
            }

            if let Event::ChangedDestination { tracking_id, destination } = e {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(destination, "loc9".to_string());
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_destination_unrouted_with_same() {
        let rs = test_route_spec(next_day(2));
        
        let cmd = Command::ChangeDestination("loc2".to_string());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn change_destination_routed() {
        let rs = test_route_spec(next_day(10));
        let it = test_itinerary();
        
        let cmd = Command::ChangeDestination("loc9".to_string());
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs.clone(), itinerary: it.clone() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Misrouted { tracking_id, route_spec, itinerary } = s {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(route_spec.destination, "loc9".to_string());
                assert_eq!(route_spec.origin, rs.origin);
                assert_eq!(route_spec.deadline, rs.deadline);
                assert_eq!(itinerary, it)
            } else {
                assert!(false);
            }

            if let Event::ChangedDestination { tracking_id, destination } = e {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(destination, "loc9".to_string());
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_destination_routed_with_same() {
        let rs = test_route_spec(next_day(10));
        let it = test_itinerary();
        
        let cmd = Command::ChangeDestination("loc2".to_string());
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs.clone(), itinerary: it.clone() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn change_deadline_unrouted() {
        let rs = test_route_spec(next_day(5));
        
        let d = next_day(7);

        let cmd = Command::ChangeDeadline(d.clone());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Unrouted { route_spec, .. } = s {
                assert_eq!(route_spec.origin, rs.origin);
                assert_eq!(route_spec.destination, rs.destination);
                assert_eq!(route_spec.deadline, d);
            } else {
                assert!(false);
            }

            if let Event::ChangedDeadline { tracking_id, deadline } = e {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(deadline, d);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_deadline_unrouted_with_same() {
        let rs = test_route_spec(next_day(5));

        let cmd = Command::ChangeDeadline(rs.deadline.clone());
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn change_deadline_unrouted_with_past() {
        let d = now();

        let rs = test_route_spec(next_day(5));

        let cmd = Command::ChangeDeadline(d);
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: rs.clone() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn change_deadline_routed() {
        let rs = test_route_spec(next_day(5));
        let it = test_itinerary();
        
        let d = next_day(7);

        let cmd = Command::ChangeDeadline(d.clone());
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs.clone(), itinerary: it.clone() };

        if let Ok((s, e)) = state.action(&cmd) {
            if let Cargo::Routed { tracking_id, route_spec, itinerary } = s {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(route_spec.origin, rs.origin);
                assert_eq!(route_spec.destination, rs.destination);
                assert_eq!(route_spec.deadline, d);
                assert_eq!(itinerary, it);
            } else {
                assert!(false);
            }

            if let Event::ChangedDeadline { tracking_id, deadline } = e {
                assert_eq!(tracking_id, "t1".to_string());
                assert_eq!(deadline, d);
            }
        } else {
            assert!(false);
        }
    }

    #[test]
    fn change_deadline_routed_with_same() {
        let rs = test_route_spec(next_day(2));

        let cmd = Command::ChangeDeadline(rs.deadline.clone());
        let state = Cargo::Misrouted { tracking_id: "t1".to_string(), route_spec: rs, itinerary: test_itinerary() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn change_deadline_routed_with_past() {
        let d = now();

        let rs = test_route_spec(next_day(5));

        let cmd = Command::ChangeDeadline(d);
        let state = Cargo::Routed { tracking_id: "t1".to_string(), route_spec: rs, itinerary: test_itinerary() };

        assert!(state.action(&cmd).is_err());
    }

    #[test]
    fn is_destination_unrouted() {
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: test_route_spec(next_day(5)) };

        assert!(state.is_destination("loc2".to_string()));
        assert_eq!(state.is_destination("loc1".to_string()), false);
    }

    #[test]
    fn is_destination_routed() {
        let it = Itinerary(
            vec![
                test_itinerary().0, 
                vec![Leg {
                    voyage_no: "v3".to_string(), 
                    load: LocationTime { location: "loc2".to_string(), time: next_day(5) },
                    unload: LocationTime { location: "loc3".to_string(), time: next_day(6) },
                }],
            ].concat()
        );

        let state = Cargo::Misrouted { tracking_id: "t1".to_string(), route_spec: test_route_spec(next_day(10)), itinerary: it };

        assert!(state.is_destination("loc2".to_string()));
        assert!(state.is_destination("loc3".to_string()));
        assert_eq!(state.is_destination("loc1".to_string()), false);
    }

    #[test]
    fn is_destination_closed() {
        let it = Itinerary(
            vec![
                test_itinerary().0, 
                vec![Leg {
                    voyage_no: "v3".to_string(), 
                    load: LocationTime { location: "loc2".to_string(), time: next_day(5) },
                    unload: LocationTime { location: "loc3".to_string(), time: next_day(6) },
                }],
            ].concat()
        );

        let state = Cargo::Closed { tracking_id: "t1".to_string(), route_spec: test_route_spec(next_day(10)), itinerary: it };

        assert!(state.is_destination("loc2".to_string()));
        assert!(state.is_destination("loc3".to_string()));
        assert_eq!(state.is_destination("loc1".to_string()), false);
    }

    #[test]
    fn is_on_route_routed() {
        let it = Itinerary(
            vec![
                test_itinerary().0, 
                vec![Leg {
                    voyage_no: "v3".to_string(), 
                    load: LocationTime { location: "loc2".to_string(), time: next_day(5) },
                    unload: LocationTime { location: "loc3".to_string(), time: next_day(6) },
                }],
            ].concat()
        );

        let state = Cargo::Misrouted { tracking_id: "t1".to_string(), route_spec: test_route_spec(next_day(10)), itinerary: it };

        assert!(state.is_on_route("loc1".to_string(), None).unwrap());
        assert!(state.is_on_route("loc2".to_string(), None).unwrap());
        assert!(state.is_on_route("loc3".to_string(), None).unwrap());

        assert!(state.is_on_route("loc1".to_string(), Some("v1".to_string())).unwrap());
        assert_eq!(state.is_on_route("loc1".to_string(), Some("v3".to_string())).unwrap(), false);
        assert!(state.is_on_route("loc2".to_string(), Some("v3".to_string())).unwrap());
        assert!(state.is_on_route("loc3".to_string(), Some("v3".to_string())).unwrap());

        assert_eq!(state.is_on_route("loc9".to_string(), None).unwrap(), false);
    }

    #[test]
    fn is_on_route_unrouted() {
        let state = Cargo::Unrouted { tracking_id: "t1".to_string(), route_spec: test_route_spec(next_day(10)) };

        assert!(state.is_on_route("loc1".to_string(), None).is_none());
        assert!(state.is_on_route("locA".to_string(), None).is_none());
    }

    #[test]
    fn is_on_route_closed() {
        let state = Cargo::Closed { tracking_id: "t1".to_string(), route_spec: test_route_spec(next_day(10)), itinerary: test_itinerary() };

        assert!(state.is_on_route("loc1".to_string(), None).is_none());
        assert!(state.is_on_route("locA".to_string(), None).is_none());
    }

}