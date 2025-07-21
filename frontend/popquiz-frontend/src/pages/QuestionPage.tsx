import {HomePageLayout} from "./Dashboard.tsx";
import {useParams} from "react-router";
import {ErrorBack} from "../ui/utils.tsx";
import {QuestionShowcase} from "../ui/Questions.tsx";

export function QuestionPage() {
    const {question_id} = useParams();
    const q_id = parseInt(question_id ? question_id : '-1')
    return (<HomePageLayout children={question_id ? <QuestionShowcase question_id={q_id}/> : <ErrorBack/>}/>)
}

export function TeacherQuestionPage() {
    const {question_id} = useParams();
    const q_id = parseInt(question_id ? question_id : '-1')
    return (<HomePageLayout children={!question_id ? <ErrorBack/> : <>
        {/*lack of API for teacher statics*/}
        {q_id}
    </>}/>)
}

export function StudentQuestionPage() {
    const {question_id} = useParams();
    const q_id = parseInt(question_id ? question_id : '-1')
    return (<HomePageLayout children={!question_id ? <ErrorBack/> : <>
        <QuestionShowcase question_id={q_id}/>
    </>}/>)
}