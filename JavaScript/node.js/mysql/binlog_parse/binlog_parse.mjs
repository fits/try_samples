import * as fs from 'fs'

const eventMap = {
    0x00: 'UNKNOWN_EVENT',
    0x01: 'START_EVENT_V3',
    0x02: 'QUERY_EVENT',
    0x03: 'STOP_EVENT',
    0x04: 'ROTATE_EVENT',
    0x05: 'INTVAR_EVENT',
    0x06: 'LOAD_EVENT',
    0x07: 'SLAVE_EVENT',
    0x08: 'CREATE_FILE_EVENT',
    0x09: 'APPEND_BLOCK_EVENT',
    0x0a: 'EXEC_LOAD_EVENT',
    0x0b: 'DELETE_FILE_EVENT',
    0x0c: 'NEW_LOAD_EVENT',
    0x0d: 'RAND_EVENT',
    0x0e: 'USER_VAR_EVENT',
    0x0f: 'FORMAT_DESCRIPTION_EVENT',
    0x10: 'XID_EVENT',
    0x11: 'BEGIN_LOAD_QUERY_EVENT',
    0x12: 'EXECUTE_LOAD_QUERY_EVENT',
    0x13: 'TABLE_MAP_EVENT',
    0x14: 'WRITE_ROWS_EVENT_V0',
    0x15: 'UPDATE_ROWS_EVENT_V0',
    0x16: 'DELETE_ROWS_EVENT_V0',
    0x17: 'WRITE_ROWS_EVENT_V1',
    0x18: 'UPDATE_ROWS_EVENT_V1',
    0x19: 'DELETE_ROWS_EVENT_V1',
    0x1a: 'INCIDENT_EVENT',
    0x1b: 'HEARTBEAT_EVENT',
    0x1c: 'IGNORABLE_EVENT',
    0x1d: 'ROWS_QUERY_EVENT',
    0x1e: 'WRITE_ROWS_EVENT_V2',
    0x1f: 'UPDATE_ROWS_EVENT_V2',
    0x20: 'DELETE_ROWS_EVENT_V2',
    0x21: 'GTID_EVENT',
    0x22: 'ANONYMOUS_GTID_EVENT',
    0x23: 'PREVIOUS_GTIDS_EVENT'
}

const logFile = process.argv[2]
const logPos = parseInt(process.argv[3])

const buf = fs.readFileSync(logFile)
const ab = new Uint8Array(buf).buffer

const dv = new DataView(ab)

const getUint48 = (dataView, p) => {
    const low = dataView.getUint32(p, true)
    const high = dataView.getUint16(p + 4, true)

    return (high * Math.pow(2, 32)) + low
}

const parseEvent = (typeName, eventStart, eventSize) => {
    const view = new DataView(dv.buffer, eventStart, eventSize)
    const res = {}

    switch (typeName) {
        case 'TABLE_MAP_EVENT':
            res.table_id = getUint48(view, 0)
            break
        case 'WRITE_ROWS_EVENT_V2':
        case 'UPDATE_ROWS_EVENT_V2':
        case 'DELETE_ROWS_EVENT_V2':
            res.table_id = getUint48(view, 0)
            break
    }

    return res
}

const parseBinLog = p => {
    const timestamp = dv.getUint32(p, true)
    const typeCode = dv.getUint8(p + 4, true)
    const serverId = dv.getUint32(p + 5, true)
    const eventLen = dv.getUint32(p + 9, true)
    const nextPos = dv.getUint32(p + 13, true)
    const flags = dv.getUint16(p + 17, true)

    const typeName = eventMap[typeCode]

    return {
        header: {
            timestamp: new Date(timestamp * 1000),
            type_code: typeCode,
            type_name: typeName,
            server_id: serverId,
            event_length: eventLen,
            next_position: nextPos,
            flags
        },
        event: parseEvent(typeName, p + 19, eventLen - 19)
    }
}

let pos = logPos

while(true) {
    if (dv.byteLength <= pos) {
        break
    }

    const r = parseBinLog(pos)

    console.log(r)

    pos = r.header.next_position
}
