import {useParams} from "react-router";
import {HomePageLayout} from "./Dashboard.tsx";
import {ErrorBack} from "../ui/utils.tsx";
import {SpeechShowcase} from "../ui/Speeches.tsx";

export function SpeechPage() {
    const {speech_id} = useParams();
    const s_id = parseInt(speech_id ? speech_id : '-1');
    return <HomePageLayout children={speech_id ? <SpeechShowcase speech_id={s_id}/> : <ErrorBack/>}/>
}