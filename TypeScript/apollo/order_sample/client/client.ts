import { request, gql } from 'graphql-request'

const uri = process.env.GQL_URI ?? 'http://localhost:4000/graphql'

const print = (v: any) => console.log(JSON.stringify(v, null, 2))

const dumpOrder = async (id: string) => {
    const r = await request(
        uri,
        gql`
            query FindOrder($id: ID!){
                find(id: $id) {
                    items {
                        item
                        price
                        qty
                    }
                    payment {
                        fee
                    }
                    delivery {
                        fee
                    }
                    billing {
                        total
                    }
                }
            }
        `,
        { id }
    )

    print(r)
}

const run = async () => {
    await dumpOrder('id1')

    const r2 = await request(
        uri,
        gql`
            mutation {
                start(input: { user: "user1" }) {
                    id
                }
            }
        `
    )

    print(r2)

    const id = r2.start.id

    const r3 = await request(
        uri,
        gql`
            mutation ChangeItem($input: ItemRequest!) {
                changeItem(input: $input) {
                    id
                }
            }
        `,
        { input: { id, item: 'item-1', qty: 2 } }
    )

    print(r3)

    await dumpOrder(id)

    const r4 = await request(
        uri,
        gql`
            mutation Payment($input: PaymentRequest!) {
                selectPayment(input: $input) {
                    id
                }
            }
        `,
        { input: { id, paymentId: 'payment-1' } }
    )

    print(r4)

    const r5 = await request(
        uri,
        gql`
            mutation Delivery($input: DeliveryRequest!) {
                selectDelivery(input: $input) {
                    id
                }
            }
        `,
        { input: { id, deliveryId: 'delivery-1' } }
    )

    print(r5)

    await dumpOrder(id)

    const r6 = await request(
        uri,
        gql`
            mutation Calculate($input: CalculateRequest!) {
                calculate(input: $input) {
                    id
                }
            }
        `,
        { input: { id } }
    )

    print(r6)

    const r7 = await request(
        uri,
        gql`
            mutation Confirm($input: ConfirmRequest!) {
                confirm(input: $input) {
                    id
                }
            }
        `,
        { input: { id } }
    )

    print(r7)

    await dumpOrder(id)
}

run().catch(err => console.error(err))
