import {dbService} from '../firebase';

export async function SendError(data) {
    await dbService.collection("Error").add({
        title: data.inputReportTitle,
        content: data.inputReportContent
    });
}