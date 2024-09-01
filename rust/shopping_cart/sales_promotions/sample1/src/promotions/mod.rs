#![allow(dead_code)]

pub mod discount;
use discount::{DiscountRule, ItemCondition, DiscountReward};

use std::collections::HashMap;

use num::BigRational;
use num_traits::Zero;

pub type OrderId = String;
pub type OrderLineId = String;
pub type ItemId = String;
pub type Amount = BigRational;
pub type Quantity = usize;

pub type AttrKey = String;
pub type AttrValue = String;
pub type Attrs = HashMap<AttrKey, AttrValue>;

#[derive(Debug, Clone)]
pub struct PromotionRule {
    condition: OrderCondition,
    action: PromotionAction,
}

#[derive(Debug, Clone)]
pub enum OrderCondition {
    Anything,
    Attribute(AttrKey, Vec<AttrValue>),
    SubtotalRange(Amount, Option<Amount>),
    IncludeItem(ItemCondition),
    Not(Box<Self>),
    And(Box<Self>, Box<Self>),
    Or(Box<Self>, Box<Self>),
}

#[derive(Debug, Clone)]
pub enum PromotionAction {
    All(RewardAction),
    Any(Quantity, RewardAction),
}

#[derive(Debug, Clone)]
pub enum RewardAction {
    Discount(DiscountRule),
}

#[derive(Debug, Clone)]
pub struct Order {
    pub id: OrderId,
    pub attrs: Attrs,
    pub lines: Vec<OrderLine>,
}

#[derive(Debug, Clone, PartialEq)]
pub struct OrderLine {
    pub line_id: OrderLineId,
    pub attrs: Attrs,
    pub item_id: ItemId,
    pub price: Amount,
}

#[derive(Debug, Clone)]
pub enum RewardTarget<T> {
    None,
    Group(Vec<T>),
    Single(T),
}

#[derive(Debug, Clone)]
pub enum Reward<T> {
    Discount(DiscountReward<T>),
}
