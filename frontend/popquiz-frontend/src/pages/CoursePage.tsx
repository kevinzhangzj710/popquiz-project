import {HomePageLayout} from "./Dashboard.tsx";
import {CourseShowcase} from "../ui/Courses.tsx";
import {useParams} from "react-router";
import {ErrorBack} from "../ui/utils.tsx";

export function CoursePage() {
    const {course_id} = useParams();
    const c_id = parseInt(course_id ? course_id : '-1');
    return (<HomePageLayout children={course_id ? <CourseShowcase course_id={c_id}/> : <ErrorBack/>}/>)
}