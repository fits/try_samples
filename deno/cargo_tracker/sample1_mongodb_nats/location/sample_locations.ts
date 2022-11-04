import { UnLocode } from '../common.ts'

export type { UnLocode } from '../common.ts'

export type Location = { unLocode: UnLocode, name: string }

export const locations: Location[] = [
    { unLocode: 'CNHKG', name: 'Hong Kong' },
    { unLocode: 'AUMEL', name: 'Melbourne' },
    { unLocode: 'SESTO', name: 'Stockholm' },
    { unLocode: 'FIHEL', name: 'Helsinki' },
    { unLocode: 'USCHI', name: 'Chicago' },
    { unLocode: 'JNTKO', name: 'Tokyo' },
    { unLocode: 'DEHAM', name: 'Hamburg' },
    { unLocode: 'CNSHA', name: 'Shanghai' },
    { unLocode: 'NLRTM', name: 'Rotterdam' },
    { unLocode: 'SEGOT', name: 'Gothenburg' },
    { unLocode: 'CNHGH', name: 'Hangzhou' },
    { unLocode: 'USNYC', name: 'New York' },
    { unLocode: 'USDAL', name: 'Dallas' },
]