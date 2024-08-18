use std::collections::HashMap;

use models::{
    Amount, DiscountAction, DiscountMethod, DiscountRule, GroupCondition::*, ItemCondition::*,
    OrderItem,
};

mod models;

fn main() {
    let items = vec![
        order_item("o1", "item1", to_amount(1100), "A1", "Brand1"),
        order_item("o2", "item1", to_amount(1100), "A1", "Brand1"),
        order_item("o3", "item1", to_amount(1100), "A1", "Brand1"),
        order_item("o4", "item2", to_amount(2200), "B2", "Brand2"),
        order_item("o5", "item2", to_amount(2200), "B2", "Brand2"),
        order_item("o6", "item3", to_amount(3300), "C3", "Brand1"),
        order_item("o7", "item4", to_amount(4400), "A1", "Brand3"),
        order_item("o8", "item4", to_amount(4400), "A1", "Brand3"),
        order_item("o9", "item5", to_amount(5500), "C3", "Brand3"),
    ];

    let rule1 = DiscountRule {
        condition: Items(Attribute("category".into(), vec!["A1".into(), "B2".into()]).not()),
        action: DiscountAction::Whole(DiscountMethod::rate(to_amount(10))),
    };

    println!("* rule1 = {:?}", rule1.apply(&items));

    let rule2 = DiscountRule {
        condition: Items(Item(vec!["item1".into()])).qty_limit(2, Some(2)),
        action: DiscountAction::each_with_skip(DiscountMethod::rate(to_amount(100)), 1),
    };

    println!("* rule2 = {:?}", rule2.apply(&items));

    let rule3 = DiscountRule {
        condition: PickOne(vec![Item(vec!["item1".into()]), Item(vec!["item1".into()])]),
        action: DiscountAction::each_with_skip(DiscountMethod::rate(to_amount(50)), 1),
    };

    println!("* rule3 = {:?}", rule3.apply(&items));

    let rule4 = DiscountRule {
        condition: PickOne(vec![
            Attribute("category".into(), vec!["A1".into()]),
            Attribute("category".into(), vec!["B2".into()]),
        ]),
        action: DiscountAction::Whole(DiscountMethod::price(to_amount(2500))),
    };

    println!("* rule4 = {:?}", rule4.apply(&items));

    let rule5 = DiscountRule {
        condition: Items(PriceRange(to_amount(3000), None)),
        action: DiscountAction::each_with_skip_take(DiscountMethod::value(to_amount(500)), 0, 2),
    };

    println!("* rule5 = {:?}", rule5.apply(&items));

    let rule6 = DiscountRule {
        condition: Items(Attribute("brand".into(), vec!["Brand1".into()]).not()).qty_limit(2, None),
        action: DiscountAction::Whole(DiscountMethod::value(to_amount(1000))),
    };

    println!("* rule6 = {:?}", rule6.apply(&items));

    let rule7 = DiscountRule {
        condition: Items(
            Attribute("brand".into(), vec!["Brand2".into()])
                .or(Attribute("category".into(), vec!["C3".into()])),
        ),
        action: DiscountAction::each(DiscountMethod::rate(to_amount(15))),
    };

    println!("* rule7 = {:?}", rule7.apply(&items));
}

fn to_amount(v: usize) -> Amount {
    Amount::from_integer(v.into())
}

fn order_item(
    id: &'static str,
    item_id: &'static str,
    price: Amount,
    category: &'static str,
    brand: &'static str,
) -> OrderItem {
    OrderItem {
        id: id.into(),
        item_id: item_id.into(),
        price,
        attrs: HashMap::from([
            ("category".into(), category.into()),
            ("brand".into(), brand.into()),
        ]),
    }
}
