
import { assert, assertExists, assertEquals } from 'std/testing/asserts.ts'
import { describe, it, beforeAll, afterAll } from 'std/testing/bdd.ts'

const runServerWait = parseInt(Deno.env.get('RUN_SERVER_WAIT') ?? '100')

const cargoPort = parseInt(Deno.env.get('CARGO_PORT') ?? '8280')
const locationPort = parseInt(Deno.env.get('LOCATION_PORT') ?? '8281')
const deliveryPort = parseInt(Deno.env.get('DELIVERY_PORT') ?? '8282')

const cargoUrl = `http://localhost:${cargoPort}`
const locationUrl = `http://localhost:${locationPort}`
const deliveryUrl = `http://localhost:${deliveryPort}`

describe('location', () => {
    let pm: ProcessManager

    beforeAll(async () => {
        pm = new ProcessManager()
        await pm.start()
    })

    afterAll(async () => {
        await pm?.stop()
    })

    it('find', async () => {
        const r = await findLocation('JNTKO')
        assertExists(r.data.find)
    })

    it('find invalid location', async () => {
        const r = await findLocation('ABTKO')
        assert(r.data.find == null)
    })
})

describe('single route', () => {
    let pm: ProcessManager
    let trackingId: string

    beforeAll(async () => {
        pm = new ProcessManager()
        await pm.start()
    })

    afterAll(async () => {
        await pm?.stop()
    })

    it('create cargo', async () => {
        const r = await createCargo('USNYC', 'JNTKO', afterDays(30).toISOString())
        assertExists(r.data.create)
    
        trackingId = r.data.create.trackingId
    })

    it('assign route', async () => {
        const d1 = afterDays(1).toISOString()

        const r = await assignCargo(trackingId, [
            {
                voyageNo: '0100S', 
                loadLocation: 'USNYC', 
                loadTime: d1, 
                unloadLocation: 'JNTKO', 
                unloadTime: afterDays(2).toISOString()
            }
        ])
        assertExists(r.data.assignToRoute)

        const leg = r.data.assignToRoute.itinerary.legs[0]

        assertEquals(leg.voyageNo, '0100S')
        assertEquals(leg.load.location, 'USNYC')
        assertEquals(leg.load.time, d1)
    })

    it('receive', async () => {
        const r = await receiveDelivery(trackingId, 'USNYC')
        assertExists(r.data.receive)

        assertEquals(await isMisdirected(trackingId), false)
        assertEquals(await isUnloadedAtDestination(trackingId), false)
    })

    it('load', async () => {
        const r = await loadDelivery(trackingId, '0100S', 'USNYC')
        assertExists(r.data.load)

        assertEquals(await isMisdirected(trackingId), false)
        assertEquals(await isUnloadedAtDestination(trackingId), false)
    })

    it('unload at destination', async () => {
        const r = await unloadDelivery(trackingId, 'JNTKO')
        assertExists(r.data.unload)
    
        assertEquals(await isMisdirected(trackingId), false)
        assert(await isUnloadedAtDestination(trackingId))
    })

    it('claim', async () => {
        const r = await claimDelivery(trackingId)
        assertExists(r.data.claim)
    })

    it('close', async () => {
        const r = await closeCargo(trackingId)
        assertExists(r.data.close)
    })
})

describe('misdirected', () => {
    let pm: ProcessManager
    let trackingId: string

    beforeAll(async () => {
        pm = new ProcessManager()
        await pm.start()
    })

    afterAll(async () => {
        await pm?.stop()
    })

    it('create cargo', async () => {
        const r = await createCargo('USNYC', 'JNTKO', afterDays(30).toISOString())
        assertExists(r.data.create)
    
        trackingId = r.data.create.trackingId
    })

    it('assign route', async () => {
        const d1 = afterDays(1).toISOString()

        const r = await assignCargo(trackingId, [
            {
                voyageNo: '0100S', 
                loadLocation: 'USNYC', 
                loadTime: d1, 
                unloadLocation: 'JNTKO', 
                unloadTime: afterDays(2).toISOString()
            }
        ])
        assertExists(r.data.assignToRoute)
    })

    it('receive', async () => {
        const r = await receiveDelivery(trackingId, 'USNYC')
        assertExists(r.data.receive)

        assertEquals(await isMisdirected(trackingId), false)
        assertEquals(await isUnloadedAtDestination(trackingId), false)
    })

    it('load', async () => {
        const r = await loadDelivery(trackingId, '0100S', 'USNYC')
        assertExists(r.data.load)

        assertEquals(await isMisdirected(trackingId), false)
        assertEquals(await isUnloadedAtDestination(trackingId), false)
    })

    it('unload misdirected', async () => {
        const r = await unloadDelivery(trackingId, 'USDAL')
        assertExists(r.data.unload)
    
        assertEquals(await isMisdirected(trackingId), true)
        assertEquals(await isUnloadedAtDestination(trackingId), false)
    })
})

class ProcessManager {
    private ps: Deno.Process[] = []

    async start() {
        this.ps.push(runServer(
            'location/server.ts', 
            { 'LOCATION_PORT':  `${locationPort}` }
        ))

        this.ps.push(runServer(
            'cargo/server.ts', 
            {
                'CARGO_PORT':  `${cargoPort}`,
                'LOCATION_ENDPOINT': locationUrl
            }
        ))

        this.ps.push(runServer(
            'delivery/server.ts', 
            {
                'DELIVERY_PORT':  `${deliveryPort}`,
                'CARGO_ENDPOINT': cargoUrl
            }
        ))

        await sleep()

        this.ps.push(runServer(
            'handling/cargo_event_handler.ts', 
            {
                'DELIVERY_ENDPOINT':  deliveryUrl,
                'DURABLE_NAME': 'cargo-test1'
            }
        ))

        this.ps.push(runServer(
            'handling/delivery_event_handler.ts', 
            {
                'DELIVERY_ENDPOINT':  deliveryUrl,
                'DURABLE_NAME': 'delivery-test1'
            }
        ))

        this.ps.push(runServer(
            'handling/tracking_event_handler.ts', 
            { 'DURABLE_NAME': 'tracking-test1' }
        ))

        await sleep()
    }

    async stop() {
        await sleep()

        this.ps.forEach(p => {
            p.kill('SIGTERM')
            p.close()
        })
    }
}


const postJson = async (url: string, body: unknown) => {
    const r = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    })

    return r.json()
}

const findLocation = (code: string) => postJson(locationUrl, {
    query: `
        query ($code: ID!) {
            find(unLocode: $code) {
                unLocode
                name
            }
        }
    `,
    variables: { code }
})

const createCargo = (ori: string, dest: string, deadline: string) => postJson(cargoUrl, {
    query: `
        mutation ($ori: ID!, $dest: ID!, $deadline: Date!) {
            create(origin: $ori, destination: $dest, deadline: $deadline) {
                trackingId
            }
        }
    `,
    variables: { ori, dest, deadline }
})

const assignCargo = ( tid: string, legs: unknown[] ) => postJson(cargoUrl, {
    query: `
        fragment ItineraryPart on Itinerary {
            legs {
                voyageNo
                load {
                    location
                    time
                }
                unload {
                    location
                    time
                }
            }    
        }
        
        mutation ($tid: ID!, $legs: [LegInput!]!) {
            assignToRoute(trackingId: $tid, legs: $legs) {
                __typename
                trackingId
                routeSpec {
                    origin
                    destination
                    arrivalDeadline
                }
                ... on Routing {
                    itinerary {
                        ...ItineraryPart
                    }
                }
            }
        }
    `,
    variables: { tid, legs }
})

const closeCargo = ( tid: string ) => postJson(cargoUrl, {
    query: `
        mutation ($tid: ID!) {
            close(trackingId: $tid) {
                __typename
                trackingId
            }
        }
    `,
    variables: { tid }
})

const receiveDelivery = (tid: string, loc: string) => postJson(deliveryUrl, {
    query: `
        mutation ($tid: ID!, $loc: ID!) {
            receive(trackingId: $tid, location: $loc) {
                __typename
                trackingId
                ... on Locating {
                    location
                }
            }
        }
    `,
    variables: { tid, loc }
})

const loadDelivery = (tid: string, vno: string, loc: string) => postJson(deliveryUrl, {
    query: `
        mutation ($tid: ID!, $vno: ID!, $loc: ID!) {
            load(trackingId: $tid, voyageNo: $vno, location: $loc) {
                __typename
                trackingId
                ... on Locating {
                    location
                }
                ... on OnBoarding {
                    currentVoyageNo
                }
            }
        }
    `,
    variables: { tid, vno, loc }
})

const unloadDelivery = (tid: string, loc: string) => postJson(deliveryUrl, {
    query: `
        mutation ($tid: ID!, $loc: ID!) {
            unload(trackingId: $tid, location: $loc) {
                __typename
                trackingId
                ... on Locating {
                    location
                }
                ... on OnBoarding {
                    currentVoyageNo
                }
            }
        }
    `,
    variables: { tid, loc }
})

const claimDelivery = (tid: string) => postJson(deliveryUrl, {
    query: `
        mutation ($tid: ID!) {
            claim(trackingId: $tid) {
                __typename
                trackingId
                ... on Locating {
                    location
                }
                ... on Claiming {
                    claimedTime
                }
            }
        }
    `,
    variables: { tid }
})

const isMisdirected = async (tid: string) => {
    const r = await postJson(deliveryUrl, {
        query: `
            query ($tid: ID!) {
                isMisdirected(trackingId: $tid)
            }
        `,
        variables: { tid }
    })

    return r.data.isMisdirected
}

const isUnloadedAtDestination = async (tid: string) => {
    const r = await postJson(deliveryUrl, {
        query: `
            query ($tid: ID!) {
                isUnloadedAtDestination(trackingId: $tid)
            }
        `,
        variables: { tid }
    })

    return r.data.isUnloadedAtDestination
}

const afterDays = (n: number) => new Date(Date.now() + n * 24 * 60 * 60 * 1000)

const sleep = (t = runServerWait) => new Promise((resolve) => {
    setTimeout(resolve, t)
})

const runServer = (file: string, env = {}) => Deno.run({
    cmd: ['deno', 'run', '--allow-all', file],
    env
})
