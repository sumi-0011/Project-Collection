import firebase from "firebase/app";
import "firebase/auth";
import "firebase/firestore";
const firebaseConfig = {

};
firebase.initializeApp(firebaseConfig);
export const firebaseInstance = firebase;
export const dbService = firebase.firestore();
export  const authService = firebase.auth();

export default firebase;