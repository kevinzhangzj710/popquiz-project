import {useEffect, useState} from "react";
import {List, message, Space, Typography} from "antd";
import {Link, useNavigate} from "react-router";
import {useAtomValue} from "jotai";
import {user_id_atom} from "../states/user.ts";
import {SpeechList} from "./Speeches.tsx";
import {$fetch} from "../api/api-utils.ts";

type course_data = {
    id: number,
    title: string,
    description: string,
    organizer_id: number,
}
const test_data: course_data[] = [
    {
        id: 1,
        title: 'test1',
        description: 'abcd abcd abcd abcd abcd abcd abcd abcd',
        organizer_id: 2,
    }, {
        id: 2,
        title: 'test2',
        description: 'abcd abcd abcd abcd abcd abcd abcd abcd',
        organizer_id: 3,
    }
]

export function CourseListItem({data}: { data: course_data }) {
    return (
        <List.Item
        >
            <List.Item.Meta
                title={data.title}
                description={<>
                    <div>
                        组织者：{data.organizer_id}
                    </div>
                    <div>
                        简介：{data.description}
                    </div>
                </>}/>
            <Space size={'middle'}>
                <Link to={`/course/${data.id}`}>查看</Link>
                <Link to={`/course/${data.id}????`}>退出</Link>
            </Space>
        </List.Item>
    )
}

export function CourseList({teaching}: { teaching: boolean }) {
    const [courses, setCourses] = useState<course_data[]>([]);
    const user_id = useAtomValue(user_id_atom);
    const [messageApi, contextHolder] = message.useMessage()

    // api fetching
    async function fetchCourses() {
        if (teaching) {
            // teaching a class api
            const {data, error} = await $fetch.GET('/api/getTeachingCourse', {
                params: {
                    query: {
                        uid: user_id
                    }
                }
            });
            if (error) {
                console.log(error)
                messageApi.error('获取课程失败')
            } else {
                setCourses(data);
            }
        } else {
            // listening a class api
            const {data, error} = await $fetch.GET('/api/getListeningCourse', {
                params: {
                    query: {
                        uid: user_id
                    }
                }
            });
            if (error) {
                console.log(error)
                messageApi.error('获取课程失败')
            } else {
                setCourses(data)
            }
        }
    }

    useEffect(() => {
        fetchCourses()
    }, [user_id, teaching]);
    return (<>
        {contextHolder}
        <Typography.Title level={2}>我{teaching ? '教' : '听'}的课：</Typography.Title>
        <List
            dataSource={courses}
            renderItem={(item) => <CourseListItem data={item}/>}
        />
    </>)
}

export function CourseShowcase({course_id}: { course_id: number }) {
    const [course, setCourse] = useState<course_data>();
    const user_id = useAtomValue(user_id_atom);
    const navi = useNavigate();
    const [messageApi, contextHolder] = message.useMessage()

    async function fetchCourse() {
        const {data, error} = await $fetch.GET('/api/getCourseById', {
            params: {
                query: {
                    course_id: course_id
                }
            }
        })
        if (error) {
            console.log(error)
            messageApi.error('获取课程失败')
        } else {
            setCourse(data)
        }
    }

    useEffect(() => {
        fetchCourse()
    }, [course_id, user_id]);

    return (<>
        {contextHolder}
        {!course && <a onClick={() => {
            navi(-1);
        }}>发生错误，请返回</a>}
        {course && <>
            <Typography.Title level={2}>课程：{course.title}</Typography.Title>
            <Typography>课程ID：{course.organizer_id}</Typography>
            <Typography>组织者：{course.organizer_id}</Typography>
            <Typography>简介：{course.description}</Typography>
            <SpeechList course_id={course_id}/>
        </>}

    </>)
}