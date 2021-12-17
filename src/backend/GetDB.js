import {dbService} from '../firebase';

const today = new Date();
let getToday = today.getMonth()+1 + "." + ("0"+today.getDate()).slice(-2);
const yesterday = new Date();
yesterday.setDate(today.getDate()-1);
let getYesterday = yesterday.getMonth()+1 + "." + ("0"+yesterday.getDate()).slice(-2);
const beforeYesterday = new Date();
beforeYesterday.setDate(today.getDate()-2);
let getBeforeYesterday = beforeYesterday.getMonth()+1 + "." + ("0"+beforeYesterday.getDate()).slice(-2);

export async function getConfirmStateDB(callback1, callback2, callback3) {
    // const ref1 = dbService.collection('ConfirmState').doc(getToday);
    const ref1 = dbService.collection('ConfirmState').doc('12.17');
    const doc1 = await ref1.get();
    // const ref2 = dbService.collection('ConfirmState').doc(getYesterday);
    const ref2 = dbService.collection('ConfirmState').doc('12.16');
    const doc2 = await ref2.get();
    // const ref3 = dbService.collection('ConfirmState').doc(getBeforeYesterday);
    const ref3 = dbService.collection('ConfirmState').doc('12.16');
    const doc3 = await ref3.get();
    if (!doc1.exists || !doc2.exists || !doc3.exists) {
      console.log('No document!');
    } else {
      await callback1(doc1.data());
      await callback2(doc2.data());
      await callback3(doc3.data());
    }
}

export async function getDaejeonDB(callback1, callback2, callback3) {
  // const ref1 = dbService.collection('ConfirmState').doc(getToday);
  const ref1 = dbService.collection('DaejeonState').doc('12.17');
  const doc1 = await ref1.get();
  // const ref2 = dbService.collection('ConfirmState').doc(getYesterday);
  const ref2 = dbService.collection('DaejeonState').doc('12.16');
  const doc2 = await ref2.get();
  // const ref3 = dbService.collection('ConfirmState').doc(getBeforeYesterday);
  const ref3 = dbService.collection('DaejeonState').doc('12.16');
  const doc3 = await ref3.get();
  if (!doc1.exists || !doc2.exists || !doc3.exists) {
    console.log('No document!');
  } else {
    await callback1(doc1.data());
    await callback2(doc2.data());
    await callback3(doc3.data());
  }
}

export async function getClinic(callback) {
    const ref = dbService.collection('Clinic');
    const docs = await ref.get();
    docs.forEach(doc => {
        callback(doc.data());
    })
}
export async function getRoute(callback) {
  const ref = dbService.collection('ConfirmRoute');
  const docs = await ref.get();
  docs.forEach(doc => {
      callback(doc.data());
  })
}
export async function getError(callback) {
  const ref = dbService.collection('Error');
  const docs = await ref.get();
  docs.forEach(doc => {
      callback(doc.data());
  })
}