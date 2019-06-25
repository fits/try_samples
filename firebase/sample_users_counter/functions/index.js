
const functions = require('firebase-functions').region('asia-northeast1')
const admin = require('firebase-admin')

admin.initializeApp()

async function updateValue(tx, ref, diff) {
    const doc = await tx.get(ref)
    tx.update(ref, {value: doc.data().value + diff})
}

exports.sampleLogin = functions.auth.user().onCreate( (user) => {
    const db = admin.firestore()

    return db.collection('sample').doc(user.uid).set({value: 0})
})

exports.createSampleRequest =
    functions.firestore.document('sample/{userId}/requests/{rid}')
            .onCreate( async (snap, ctx) => {

    const db = admin.firestore()
    const data = snap.data()

    console.log(`oncreate : eventid=${ctx.eventId}, uid=${ctx.params.userId}, doc=${ctx.params.rid}, data=${JSON.stringify(data)}`)

    const diff = data.action == 'up' ? data.value : -1 * data.value

    const docRef = snap.ref.parent.parent

    await db.runTransaction(tx => updateValue(tx, docRef, diff))

    return snap.ref.update('done', new Date())
})
