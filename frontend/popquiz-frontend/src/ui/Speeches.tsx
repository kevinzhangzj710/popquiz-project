import {List, message, Space, Typography} from "antd";
import {Link} from "react-router";
import {useEffect, useState} from "react";
import {useAtomValue} from "jotai/index";
import {user_id_atom} from "../states/user.ts";
import {ErrorBack} from "./utils.tsx";
import {$fetch} from "../api/api-utils.ts";
import {QuestionList} from "./Questions.tsx";

type speech_data = { id: number, title: string, speaker_id: number, course_id: number }

const test_data: speech_data[] = [
    {id: 1, title: 't1', speaker_id: 1, course_id: 2},
    {id: 2, title: 't1', speaker_id: 1, course_id: 2},
    {id: 3, title: 't1', speaker_id: 1, course_id: 2},
]

export function SpeechListItem({data}: { data: speech_data }) {
    return (<List.Item
    >
        <List.Item.Meta
            title={data.title}
            description={<>
                <div>
                    演讲者：{data.speaker_id}
                </div>
            </>}/>
        <Space size={'middle'}>
            <Link to={`/speech/${data.id}`}>查看</Link>
        </Space>
    </List.Item>)
}

export function SpeechList({course_id}: { course_id: number }) {
    const [speeches, setSpeeches] = useState<speech_data[]>();
    const user_id = useAtomValue(user_id_atom);
    const [messageApi, contextHolder] = message.useMessage()

    async function fetchSpeeches() {
        // course_id ,user_id
        const {data, error} = await $fetch.GET('/api/getSpeeches', {
            params: {
                query: {
                    courseid: course_id
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
        fetchSpeeches()
    }, [user_id, course_id]);

    return (<>
        {contextHolder}
        <Typography.Title level={2}>
            演讲列表：
        </Typography.Title>
        <List
            dataSource={speeches}
            renderItem={(item) => <SpeechListItem data={item}/>}
        />
    </>)
}

export function SpeechShowcase({speech_id}: { speech_id: number }) {
    const [speech, setSpeech] = useState<speech_data>();
    const user_id = useAtomValue(user_id_atom);
    const [messageApi, contextHolder] = message.useMessage()

    async function fetchSpeech() {
        const {data, error} = await $fetch.GET('/api/getSpeechById', {
            params: {
                query: {
                    speech_id: speech_id
                }
            }
        })
        if (error) {
            console.log(error)
            messageApi.error('获取演讲失败')
        } else {
            setSpeech(data)
        }
    }

    useEffect(() => {
        fetchSpeech()
    }, [speech_id, user_id]);

    return <>
        {contextHolder}
        {!speech && <ErrorBack/>}
        {speech && <>
            <Typography.Title level={2}>演讲：{speech.title}</Typography.Title>
            <Typography>演讲ID：{speech.id}</Typography>
            <Typography>所属课程ID：{speech.course_id}</Typography>
            <Typography>演讲者ID：{speech.speaker_id}</Typography>
            <QuestionList speech_id={speech.id}/>
        </>
        }
    </>
}