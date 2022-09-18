
import { assertExists, assertEquals, assert } from "https://deno.land/std@0.155.0/testing/asserts.ts"
import { create, changeDestination, changeDeadline, assignToRoute, close } from './models.ts'

const daysAfter = (n: number) => new Date(Date.now() + (n * 24 * 60 * 60 * 1000))

Deno.test('create cargo', () => {
    const r = create("t1", "loc1", "loc2", daysAfter(1))

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.unrouted")
    assertEquals(r.event.tag, "cargo-event.created")
})

Deno.test('create cargo with past deadline', () => {
    const r = create("t1", "loc1", "loc2", daysAfter(-1))

    assertEquals(r, undefined)
})

Deno.test('change destination', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(1))
    const r2 = changeDestination("loc3")(r1!.cargo)

    assertExists(r2)
    assertEquals(r2.cargo.tag, "cargo.unrouted")
    assertEquals(r2.event.tag, "cargo-event.destination-changed")

    if ('routeSpec' in r2.cargo) {
        assertEquals(r2.cargo.routeSpec.destination, "loc3")
    }
    else {
        assert(false)
    }
    
    if ('routeSpec' in r1!.cargo) {
        assertEquals(r1!.cargo.routeSpec.destination, "loc2")
    }
    else {
        assert(false)
    }
})

Deno.test('change destination with same location', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(1))
    const r2 = changeDestination("loc2")(r1!.cargo)

    assertEquals(r2, undefined)
})

Deno.test('change deadline', () => {
    const d1 = daysAfter(1)
    const r1 = create("t1", "loc1", "loc2", d1)

    const d2 = daysAfter(2)
    const r2 = changeDeadline(d2)(r1!.cargo)

    assertExists(r2)
    assertEquals(r2.cargo.tag, "cargo.unrouted")
    assertEquals(r2.event.tag, "cargo-event.deadline-changed")

    if ('routeSpec' in r2.cargo) {
        assertEquals(r2.cargo.routeSpec.arrivalDeadline, d2)
    }
    else {
        assert(false)
    }
    if ('routeSpec' in r1!.cargo) {
        assertEquals(r1!.cargo.routeSpec.arrivalDeadline, d1)
    }
    else {
        assert(false)
    }
})

Deno.test('change deadline with past deadline', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(1))
    const r2 = changeDeadline(daysAfter(-1))(r1!.cargo)

    assertEquals(r2, undefined)
})

Deno.test('change deadline with same deadline', () => {
    const d = daysAfter(1)
    const r1 = create("t1", "loc1", "loc2", d)
    const r2 = changeDeadline(new Date(d.getTime()))(r1!.cargo)

    assertEquals(r2, undefined)
})

Deno.test('assignToRoute with 2 route', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(5))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc2', time: daysAfter(4) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)

    assertExists(r2)
    assertEquals(r2.cargo.tag, "cargo.routed")
    assertEquals(r2.event.tag, "cargo-event.route-assigned")
})

Deno.test('assignToRoute with misroute', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(5))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locB', time: daysAfter(3) }, 
                unload: { location: 'locC', time: daysAfter(4) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)

    assertExists(r2)
    assertEquals(r2.cargo.tag, "cargo.misrouted")
    assertEquals(r2.event.tag, "cargo-event.route-assigned")
})

Deno.test('assignToRoute with past deadline route', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(5))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc2', time: daysAfter(6) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)

    assertExists(r2)
    assertEquals(r2.cargo.tag, "cargo.misrouted")
    assertEquals(r2.event.tag, "cargo-event.route-assigned")
})

Deno.test('change destination for routed cargo', () => {
    const cargo = routedCargo()
    const r = changeDestination("loc3")(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.misrouted")
    assertEquals(r.event.tag, "cargo-event.destination-changed")

    if ('routeSpec' in r.cargo) {
        assertEquals(r.cargo.routeSpec.destination, "loc3")
    }
    else {
        assert(false)
    }
})

Deno.test('change destination for misrouted cargo', () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(5))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc3', time: daysAfter(4) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)
    const r3 = changeDestination("loc3")(r2!.cargo)

    assertExists(r3)
    assertEquals(r3.cargo.tag, "cargo.routed")
    assertEquals(r3.event.tag, "cargo-event.destination-changed")

    if ('routeSpec' in r3.cargo) {
        assertEquals(r3.cargo.routeSpec.destination, "loc3")
    }
    else {
        assert(false)
    }
})

Deno.test('change destination with misroute for misrouted cargo', () => {
    const cargo = misroutedCargo()
    const r = changeDestination("loc4")(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.misrouted")
    assertEquals(r.event.tag, "cargo-event.destination-changed")
})

Deno.test('change deadline for routed cargo', () => {
    const cargo = routedCargo()

    const d = daysAfter(6)
    const r = changeDeadline(d)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.routed")
    assertEquals(r.event.tag, "cargo-event.deadline-changed")

    if ('routeSpec' in r.cargo) {
        assertEquals(r.cargo.routeSpec.arrivalDeadline, d)
    }
    else {
        assert(false)
    }
})

Deno.test('change deadline with not enough deadline for routed cargo', () => {
    const cargo = routedCargo()

    const d = daysAfter(3)
    const r = changeDeadline(d)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.misrouted")
    assertEquals(r.event.tag, "cargo-event.deadline-changed")

    if ('routeSpec' in r.cargo) {
        assertEquals(r.cargo.routeSpec.arrivalDeadline, d)
    }
    else {
        assert(false)
    }
})

Deno.test('change deadline for misrouted deadline cargo', () => {
    const cargo = misroutedDeadlineCargo()

    const d = daysAfter(5)
    const r = changeDeadline(d)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.routed")
    assertEquals(r.event.tag, "cargo-event.deadline-changed")

    if ('routeSpec' in r.cargo) {
        assertEquals(r.cargo.routeSpec.arrivalDeadline, d)
    }
    else {
        assert(false)
    }
})

Deno.test('assignToRoute for routed cargo', () => {
    const cargo = routedCargo()

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'locB', time: daysAfter(4) },
            },
            {
                voyageNo: 'v56', 
                load: { location: 'locB', time: daysAfter(5) }, 
                unload: { location: 'loc2', time: daysAfter(6) },
            }
        ]
    }

    const r = assignToRoute(it)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.routed")
    assertEquals(r.event.tag, "cargo-event.route-assigned")
})

Deno.test('assignToRoute with misroute for routed cargo', () => {
    const cargo = routedCargo()

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locB', time: daysAfter(3) }, 
                unload: { location: 'locC', time: daysAfter(4) },
            }
        ]
    }

    const r = assignToRoute(it)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.misrouted")
    assertEquals(r.event.tag, "cargo-event.route-assigned")
})

Deno.test('assignToRoute for misrouted cargo', () => {
    const cargo = misroutedCargo()

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc2', time: daysAfter(4) },
            }
        ]
    }

    const r = assignToRoute(it)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.routed")
    assertEquals(r.event.tag, "cargo-event.route-assigned")
})

Deno.test('assignToRoute with misroute for misrouted cargo', () => {
    const cargo = misroutedCargo()

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'locB', time: daysAfter(4) },
            }
        ]
    }

    const r = assignToRoute(it)(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.misrouted")
    assertEquals(r.event.tag, "cargo-event.route-assigned")
})

Deno.test('close for unrouted cargo', () => {
    const cargo = create("t1", "loc1", "loc2", daysAfter(1))!.cargo

    const r = close(cargo)

    assertEquals(r, undefined)
})

Deno.test('close for routed cargo', () => {
    const cargo = routedCargo()

    const r = close(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.closed")
    assertEquals(r.event.tag, "cargo-event.closed")
})

Deno.test('close for misrouted cargo', () => {
    const cargo = misroutedCargo()

    const r = close(cargo)

    assertExists(r)
    assertEquals(r.cargo.tag, "cargo.closed")
    assertEquals(r.event.tag, "cargo-event.closed")
})

Deno.test('close for closed cargo', () => {
    const cargo = routedCargo()

    const r = close(cargo)
    const r2 = close(r!.cargo)

    assertEquals(r2, undefined)
})

Deno.test('change destination for closed cargo', () => {
    const cargo = close(routedCargo())!.cargo
    const r = changeDestination("loc3")(cargo)

    assertEquals(r, undefined)
})

Deno.test('change deadline for closed cargo', () => {
    const cargo = close(routedCargo())!.cargo
    const r = changeDeadline(daysAfter(15))(cargo)

    assertEquals(r, undefined)
})

Deno.test('assignToRoute for closed cargo', () => {
    const cargo = close(routedCargo())!.cargo

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'loc2', time: daysAfter(2) },
            }
        ]
    }

    const r = assignToRoute(it)(cargo)

    assertEquals(r, undefined)
})


const routedCargo = () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(10))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc2', time: daysAfter(4) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)
    
    return r2!.cargo
}

const misroutedCargo = () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(5))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc3', time: daysAfter(4) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)
    
    return r2!.cargo
}

const misroutedDeadlineCargo = () => {
    const r1 = create("t1", "loc1", "loc2", daysAfter(3))

    const it = {
        legs: [
            {
                voyageNo: 'v12', 
                load: { location: 'loc1', time: daysAfter(1) }, 
                unload: { location: 'locA', time: daysAfter(2) },
            },
            {
                voyageNo: 'v34', 
                load: { location: 'locA', time: daysAfter(3) }, 
                unload: { location: 'loc2', time: daysAfter(4) },
            }
        ]
    }

    const r2 = assignToRoute(it)(r1!.cargo)
    
    return r2!.cargo
}