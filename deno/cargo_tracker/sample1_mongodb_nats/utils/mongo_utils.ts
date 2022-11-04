
import { ObjectId, Collection } from 'mongo'
import { Maybe } from '../utils/type_utils.ts'

export { MongoClient, ObjectId } from 'mongo'

export type Revision = number

export type StoredState<STATE, EVENT> = {
    _id: ObjectId,
    rev: Revision,
    state?: Maybe<STATE>,
    events: EVENT[],
    updatedAt: Date
}

export type StoredAction<STATE, EVENT> = (state: STATE) => Maybe<{ state: STATE, event: EVENT }>

export class Store<STATE, EVENT> {
    private col: Collection<StoredState<STATE, EVENT>>

    constructor(col: Collection<StoredState<STATE, EVENT>>) {
        this.col = col
    }

    async create(oid?: ObjectId, state?: STATE): Promise<[ObjectId, Revision]> {
        const rev = 0
        const id = await this.col.insertOne({ _id: oid, rev, state, events: [], updatedAt: new Date() })

        return [ id, rev ]
    }

    async find(oid: ObjectId): Promise<Maybe<StoredState<STATE, EVENT>>> {
        const res = await this.col.findOne({ _id: oid })
        return (res?.state) ? res : undefined
    }

    update(oid: ObjectId, rev: Revision, state: STATE, event: EVENT): Promise<Maybe<StoredState<STATE, EVENT>>> {

        return this.col.findAndModify(
            { '$and': [ { _id: oid }, { rev } ] },
            {
                update: {
                    '$inc': { rev: 1 },
                    '$set': {
                        state,
                        updatedAt: new Date()
                    },
                    '$push': {
                        events: event
                    }
                },
                new: true
            }
        )
    }

    async updateWithAction(oid: ObjectId, action: StoredAction<STATE, EVENT>): Promise<Maybe<StoredState<STATE, EVENT>>> {    
        const c = await this.find(oid)
    
        if (c?.state) {
            const s = action(c.state)
    
            if (s) {
                return await this.update(oid, c.rev, s.state, s.event)
            }
        }
    
        return undefined
    }
}
