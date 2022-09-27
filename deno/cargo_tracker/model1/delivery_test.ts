
import { assertExists, assertEquals, assert } from 'https://deno.land/std@0.155.0/testing/asserts.ts'

import { RouteSpecification, Itinerary } from './common.ts'

import {
    create, load, unload, receive, claim, isMisdirected,
    InPortDelivery, OnBoardCarrierDelivery, NotReceivedDelivery, ClaimedDelivery
} from './delivery.ts'

const daysAfter = (n: number) => new Date(Date.now() + (n * 24 * 60 * 60 * 1000))

Deno.test('create', () => {
    const d = create('t-1')

    assertExists(d)
    assertEquals(d.tag, 'delivery.not-received')
})

Deno.test('creat with empty trackingIde', () => {
    const d = create('')

    assertEquals(d, undefined)
})

Deno.test('creat with space trackingIde', () => {
    const d = create('   ')

    assertEquals(d, undefined)
})

Deno.test('receive', () => {
    const now = new Date()

    const d = receive('t-1', 'loc-1', now)(create('t-1')!)

    assertExists(d)
    assertEquals(d.delivery.trackingId, 't-1')

    if (d.delivery.tag == 'delivery.in-port') {
        assertEquals(d.delivery.location, 'loc-1')
    }
    else {
        assert(false)
    }

    if (d.event.tag == 'transport-event.received') {
        assertEquals(d.event.trackingId, 't-1')
        assertEquals(d.event.location, 'loc-1')
        assertEquals(d.event.completionTime, now)
    }
    else {
        assert(false)
    }
})

Deno.test('receive in in-port', () => {
    const now = new Date()

    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-1'
    }

    const d1 = receive('t-1', 'loc-1', now)(d)

    assertEquals(d1, undefined)
})

Deno.test('receive in onboard-carrier', () => {
    const now = new Date()

    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        location: 'loc-1',
        currentVoyageNo: 'v1'
    }

    const d1 = receive('t-1', 'loc-1', now)(d)

    assertEquals(d1, undefined)
})

Deno.test('receive in claimed', () => {
    const now = new Date()

    const d: ClaimedDelivery = {
        tag: 'delivery.claimed',
        trackingId: 't-1',
        location: 'loc-1',
        claimedTime: daysAfter(1)
    }

    const d1 = receive('t-1', 'loc-1', now)(d)

    assertEquals(d1, undefined)
})

Deno.test('load', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = load('t-1', 'v1', 'loc-1', daysAfter(1), _tid => it)(d)

    assertExists(d1)
    assertEquals(d1.delivery.trackingId, 't-1')

    if (d1.delivery.tag == 'delivery.onboard-carrier') {
        assertEquals(d1.delivery.currentVoyageNo, 'v1')
        assertEquals(d1.delivery.location, 'loc-1')
    }
    else {
        assert(false)
    }

    if (d1.event.tag == 'transport-event.loaded') {
        assertEquals(d1.event.voyageNo, 'v1')
        assertEquals(d1.event.trackingId, 't-1')
        assertEquals(d1.event.location, 'loc-1')
    }
    else {
        assert(false)
    }
})

Deno.test('load in not-received', () => {
    const d: NotReceivedDelivery = {
        tag: 'delivery.not-received',
        trackingId: 't-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = load('t-1', 'v1', 'loc-1', daysAfter(1), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('load in onboard-carrier', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'v1',
        location: 'loc-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = load('t-1', 'v1', 'loc-2', daysAfter(1), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('load in claimed', () => {
    const d: ClaimedDelivery = {
        tag: 'delivery.claimed',
        trackingId: 't-1',
        location: 'loc-4',
        claimedTime: daysAfter(5)
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = load('t-1', 'v1', 'loc-1', daysAfter(1), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('load with unknown vayage', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = load('t-1', 'vA', 'loc-1', daysAfter(1), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('load with unknown location', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = load('t-1', 'v1', 'loc-A', daysAfter(1), _tid => it)(d)

    assertExists(d1)
    assertEquals(d1.delivery.trackingId, 't-1')

    if (d1.delivery.tag == 'delivery.onboard-carrier') {
        assertEquals(d1.delivery.currentVoyageNo, 'v1')
        assertEquals(d1.delivery.location, 'loc-A')
    }
    else {
        assert(false)
    }

    if (d1.event.tag == 'transport-event.loaded') {
        assertEquals(d1.event.voyageNo, 'v1')
        assertEquals(d1.event.trackingId, 't-1')
        assertEquals(d1.event.location, 'loc-A')
    }
    else {
        assert(false)
    }
})

Deno.test('unload', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'v1',
        location: 'loc-1'
    }
    
    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = unload('t-1', 'loc-2', daysAfter(2), _tid => it)(d)

    assertExists(d1)
    assertEquals(d1.delivery.trackingId, 't-1')

    if (d1.delivery.tag == 'delivery.in-port') {
        assertEquals(d1.delivery.location, 'loc-2')
    }
    else {
        assert(false)
    }

    if (d1.event.tag == 'transport-event.unloaded') {
        assertEquals(d1.event.voyageNo, 'v1')
        assertEquals(d1.event.trackingId, 't-1')
        assertEquals(d1.event.location, 'loc-2')
    }
    else {
        assert(false)
    }
})

Deno.test('unload in in-port', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-1'
    }
    
    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = unload('t-1', 'loc-2', daysAfter(2), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('unload in claimed', () => {
    const d: ClaimedDelivery = {
        tag: 'delivery.claimed',
        trackingId: 't-1',
        location: 'loc-1',
        claimedTime: daysAfter(3)
    }
    
    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = unload('t-1', 'loc-2', daysAfter(2), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('unload with unknown location', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'v1',
        location: 'loc-1'
    }
    
    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = unload('t-1', 'loc-A', daysAfter(2), _tid => it)(d)

    assertExists(d1)
    assertEquals(d1.delivery.trackingId, 't-1')

    if (d1.delivery.tag == 'delivery.in-port') {
        assertEquals(d1.delivery.location, 'loc-A')
    }
    else {
        assert(false)
    }

    if (d1.event.tag == 'transport-event.unloaded') {
        assertEquals(d1.event.voyageNo, 'v1')
        assertEquals(d1.event.trackingId, 't-1')
        assertEquals(d1.event.location, 'loc-A')
    }
    else {
        assert(false)
    }
})

Deno.test('unload with unknown voyage', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'vA',
        location: 'loc-1'
    }
    
    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-3', time: daysAfter(3)}, 
                unload: {location: 'loc-4', time: daysAfter(4)}
            }
        ]
    }

    const d1 = unload('t-1', 'loc-2', daysAfter(2), _tid => it)(d)

    assertEquals(d1, undefined)
})

Deno.test('claimed', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-3'
    }

    const r: RouteSpecification = {
        origin: 'loc-1', 
        destination: 'loc-3', 
        arrivalDeadline: daysAfter(10)
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const d1 = claim('t-1', daysAfter(6), _tid => [it, r])(d)

    assertExists(d1)
    assertEquals(d1.delivery.trackingId, 't-1')

    if (d1.delivery.tag == 'delivery.claimed') {
        assertEquals(d1.delivery.location, 'loc-3')
    }
    else {
        assert(false)
    }

    if (d1.event.tag == 'transport-event.claimed') {
        assertEquals(d1.event.trackingId, 't-1')
    }
    else {
        assert(false)
    }
})

Deno.test('claimed in destination', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-4'
    }

    const r: RouteSpecification = {
        origin: 'loc-1', 
        destination: 'loc-4', 
        arrivalDeadline: daysAfter(10)
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const d1 = claim('t-1', daysAfter(6), _tid => [it, r])(d)

    assertExists(d1)
    assertEquals(d1.delivery.trackingId, 't-1')

    if (d1.delivery.tag == 'delivery.claimed') {
        assertEquals(d1.delivery.location, 'loc-4')
    }
    else {
        assert(false)
    }

    if (d1.event.tag == 'transport-event.claimed') {
        assertEquals(d1.event.trackingId, 't-1')
    }
    else {
        assert(false)
    }
})

Deno.test('claimed in passage location', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-2'
    }

    const r: RouteSpecification = {
        origin: 'loc-1', 
        destination: 'loc-3', 
        arrivalDeadline: daysAfter(10)
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const d1 = claim('t-1', daysAfter(6), _tid => [it, r])(d)

    assertEquals(d1, undefined)
})

Deno.test('isMisdirected in not-received', () => {
    const d: NotReceivedDelivery = {
        tag: 'delivery.not-received',
        trackingId: 't-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assertEquals(res, false)
})

Deno.test('isMisdirected in received in-port', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assertEquals(res, false)
})

Deno.test('isMisdirected in in-port', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-2'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assertEquals(res, false)
})

Deno.test('isMisdirected in in-port with unknown location', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-A'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assert(res)
})

Deno.test('isMisdirected in onboard-carrier', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'v2',
        location: 'loc-2'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assertEquals(res, false)
})

Deno.test('isMisdirected in onboard-carrier with unknown location', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'v1',
        location: 'loc-A'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assert(res)
})

Deno.test('isMisdirected in onboard-carrier with unknown voyageNo', () => {
    const d: OnBoardCarrierDelivery = {
        tag: 'delivery.onboard-carrier',
        trackingId: 't-1',
        currentVoyageNo: 'vA',
        location: 'loc-1'
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assert(res)
})

Deno.test('isMisdirected in claimed', () => {
    const d: ClaimedDelivery = {
        tag: 'delivery.claimed',
        trackingId: 't-1',
        location: 'loc-3',
        claimedTime: daysAfter(1)
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assertEquals(res, false)
})

Deno.test('isMisdirected in claimed with unknown location', () => {
    const d: ClaimedDelivery = {
        tag: 'delivery.claimed',
        trackingId: 't-1',
        location: 'loc-A',
        claimedTime: daysAfter(1)
    }

    const it: Itinerary = {
        legs: [
            {
                voyageNo: 'v1',
                load: {location: 'loc-1', time: daysAfter(1)}, 
                unload: {location: 'loc-2', time: daysAfter(2)}
            },
            {
                voyageNo: 'v2',
                load: {location: 'loc-2', time: daysAfter(3)}, 
                unload: {location: 'loc-3', time: daysAfter(4)}
            }
        ]
    }

    const res = isMisdirected(d, _tid => it)

    assert(res)
})

Deno.test('isMisdirected with empty legs', () => {
    const d: InPortDelivery = {
        tag: 'delivery.in-port',
        trackingId: 't-1',
        location: 'loc-2'
    }

    const it: Itinerary = {
        legs: []
    }

    const res = isMisdirected(d, _tid => it)

    assertEquals(res, false)
})
