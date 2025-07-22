import {HomePageLayout} from "./Dashboard.tsx";
import {type course_data} from "../ui/Courses.tsx";
import {useParams} from "react-router";
import {ErrorBack} from "../ui/utils.tsx";
import {Button, Divider, List, message, Typography} from "antd";
import {useEffect, useState} from "react";
import {useAtomValue} from "jotai/index";
import {user_id_atom} from "../states/user.ts";
import {$fetch} from "../api/api-utils.ts";
import {ModalForm, ProFormDigit, ProFormText} from "@ant-design/pro-components";
import {type speech_data, SpeechListItem} from "../ui/Speeches.tsx";


export function CoursePage() {
    const {course_id} = useParams();
    const c_id = parseInt(course_id ? course_id : '-1');

    const [messageApi, contextHolder] = message.useMessage()
    const [course, setCourse] = useState<course_data>();
    const [speeches, setSpeeches] = useState<speech_data[]>();
    const user_id = useAtomValue(user_id_atom);

    async function fetchCourse() {
        const {data, error} = await $fetch.GET('/api/getCourseById', {
            params: {
                query: {
                    course_id: c_id
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

    async function fetchSpeeches() {
        // course_id ,user_id
        const {data, error} = await $fetch.GET('/api/getSpeeches', {
            params: {
                query: {
                    courseid: c_id
                }
            }
        })
        if (error) {
            console.log(error)
            messageApi.error('获取课程演讲列表失败')
        } else {
            setSpeeches(data)
        }
    }

    useEffect(() => {
        fetchCourse()
        fetchSpeeches()
    }, [course_id, user_id]);

    return (<HomePageLayout children={!course_id ? <ErrorBack/> :
        <>
            {contextHolder}
            {course && <>
                <Typography.Title level={2}>课程：{course.title}</Typography.Title>
                <Typography>课程ID：{course.organizer_id}</Typography>
                <Typography>组织者：{course.organizer_id}</Typography>
                <Typography>简介：{course.description}</Typography>
                <Divider/>
                <Typography.Title level={2}>
                    演讲列表：
                </Typography.Title>
                {course.organizer_id === user_id &&
                    <ModalForm<{ title: string, speaker_id: number, course_id: number }>
                        title={'新建演讲'}
                        trigger={<Button>新建演讲</Button>}
                        onFinish={async (formData) => {
                            const {data, error} = await $fetch.POST('/api/createSpeech', {
                                body: {
                                    ...formData
                                }
                            })
                            if (error || data === -1) {
                                console.log(error)
                                messageApi.error('创建演讲失败');
                            } else {
                                messageApi.success(`创建演讲成功，演讲ID：${data}`)
                                await fetchSpeeches();
                            }
                        }}
                    >
                        <ProFormText
                            name={'title'}
                            label={'标题'}
                        />
                        <ProFormDigit
                            name={'speaker_id'}
                            label={'演讲者ID'}
                            initialValue={user_id}
                        />
                        <ProFormDigit
                            name={'course_id'}
                            label={'所属课程ID'}
                            initialValue={course_id}
                        />
                    </ModalForm>}
                <List
                    dataSource={speeches}
                    renderItem={(item) => <SpeechListItem data={item}/>}
                />
            </>}
        </>}/>)
}