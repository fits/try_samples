
export interface None {
    readonly _tag: 'None'
}

export interface Some<A> {
    readonly _tag: 'Some'
    readonly value: A
}
export declare type Option<A> = None | Some<A>

export declare const some: <A>(a: A) => Option<A>
export declare const none: Option<never>
export declare const map: <A, B>(f: (a: A) => B) => (fa: Option<A>) => Option<B>
