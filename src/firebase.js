import firebase from "firebase/app";
import "firebase/auth";

const firebaseConfig = {
    //추가할곳
};
firebase.initializeApp(firebaseConfig);
export default firebase.initializeApp(firebaseConfig);
export  const authService = firebase.auth();